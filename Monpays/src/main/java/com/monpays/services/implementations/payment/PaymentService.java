package com.monpays.services.implementations.payment;

import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.dtos.payment.PaymentHistoryEntryDto;
import com.monpays.dtos.payment.PaymentRequestDto;
import com.monpays.dtos.payment.PaymentResponseDto;
import com.monpays.entities._generic.AuditEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.account.Account;
import com.monpays.entities.account.enums.EAccountOperation;
import com.monpays.entities.payment.Payment;
import com.monpays.entities.payment.PaymentHistoryEntry;
import com.monpays.entities.payment.enums.EPaymentStatus;
import com.monpays.entities.profile.enums.EProfileType;
import com.monpays.entities.user.User;
import com.monpays.mappers._generic.AuditMapper;
import com.monpays.mappers.payment.PaymentMapper;
import com.monpays.persistence.repositories.account.IAccountRepository;
import com.monpays.persistence.repositories.payment.IPaymentRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.interfaces._generic.IAuditService;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.account.IAccountService;
import com.monpays.services.interfaces.balance.IBalanceService;
import com.monpays.services.interfaces.payment.IPaymentHistoryService;
import com.monpays.services.interfaces.payment.IPaymentService;
import com.monpays.services.interfaces._generic.ICurrencyConversionService;
import com.monpays.utils.AccountNumberGenerator;
import com.monpays.utils.CurrencyXmlParser;
import com.sun.jdi.InternalException;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class PaymentService implements IPaymentService {
    @Autowired
    private IAuditService auditService;
    @Autowired
    private IPaymentHistoryService paymentHistoryService;
    @Autowired
    private IPaymentRepository paymentRepository;
    @Autowired
    private PaymentMapper paymentMapper;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IBalanceService balanceService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private CurrencyXmlParser currencyXmlParser;
    @Autowired
    private AuditMapper auditMapper;
    @Autowired
    private IUserActivityService userActivityService;
    @Autowired
    private ICurrencyConversionService currencyConversionService;

    @Override
    public PaymentResponseDto getOne(String username, String paymentNumber, boolean needsHistory, boolean needsAudit) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "getOne", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("Sir, you don't have the right to view all payments.");
        }

        auditService.add(actor, operation, null);

        Payment payment = paymentRepository.findByNumber(paymentNumber).orElse(null);
        if(payment == null) {
            return null;
        }
        auditService.add(actor, operation, paymentNumber);

        PaymentResponseDto paymentResponseDto = paymentMapper.fromPayment(payment);
        if(needsHistory) {
            List<PaymentHistoryEntryDto> paymentHistoryEntryDtos = new ArrayList<>();
            List<PaymentHistoryEntry> paymentHistoryEntries = paymentHistoryService.getByObject(payment);

            // TODO: user mapper
            paymentHistoryEntries.forEach(paymentHistoryEntry -> {
                PaymentHistoryEntryDto paymentHistoryEntryDto = new PaymentHistoryEntryDto();
                paymentHistoryEntryDto.setNumber(paymentHistoryEntry.getNumber());
                paymentHistoryEntryDto.setTimestamp(paymentHistoryEntry.getTimestamp());
                paymentHistoryEntryDto.setCurrency(paymentHistoryEntry.getCurrency().getCode());
                paymentHistoryEntryDto.setAmount(paymentHistoryEntry.getAmount());
                paymentHistoryEntryDto.setDebitAccount(paymentHistoryEntry.getDebitAccount().getAccountNumber());
                paymentHistoryEntryDto.setCreditAccount(paymentHistoryEntry.getCreditAccount().getAccountNumber());
                paymentHistoryEntryDto.setDescription(paymentHistoryEntry.getDescription());
                paymentHistoryEntryDto.setType(paymentHistoryEntry.getType());
                paymentHistoryEntryDto.setStatus(paymentHistoryEntry.getStatus());
                paymentHistoryEntryDtos.add(paymentHistoryEntryDto);
            });
            paymentResponseDto.setHistory(paymentHistoryEntryDtos);
        }

        if(needsAudit) {
            List<AuditEntryDto> auditEntryDtos = new ArrayList<>();
            List<AuditEntry> auditEntries = auditService.getByObject(Payment.class.getSimpleName(), payment.getNumber());

            auditEntries.forEach(auditEntry -> {
                auditEntryDtos.add(auditMapper.toAuditEntryDto(auditEntry));
            });

            paymentResponseDto.setAudit(auditEntryDtos);
        }

        return paymentResponseDto;
    }

    @Override
    public List<PaymentResponseDto> getAll(String username) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "listAll", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("Sir, you don't have the right to view all payments.");
        }

        auditService.add(actor, operation, null);
        return paymentRepository.findAll()
                .stream()
                .map(paymentMapper::fromPayment)
                .toList();
    }

    @Override
    public List<PaymentResponseDto> getAllByAccountNumber(String username, String accountNumber) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "listAllByAccount", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("Sir, you don't have the right to view all payments.");
        }

        Account account = accountService.classifiedGetOne(accountNumber);
        List<Payment> paymentList1 = paymentRepository.findAllByDebitAccount(account);
        List<Payment> paymentList2 = paymentRepository.findAllByCreditAccount(account);

        auditService.add(actor, operation, null);
        return Stream.concat(paymentList1.stream(), paymentList2.stream())
                .sorted((p1, p2) -> p2.getTimestamp().compareTo(p1.getTimestamp()))
                .map(paymentMapper::fromPayment)
                .toList();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public PaymentResponseDto create(String username, PaymentRequestDto paymentRequestDto) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "create", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.CREATE, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Payment payment = paymentMapper.toPayment(paymentRequestDto, accountRepository, currencyXmlParser);

        if(!Objects.equals(payment.getDebitAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, your transaction is illegal.");
        }
        if(!payment.getDebitAccount().hasRight(EAccountOperation.SEND)) {
            throw new ServiceException("Sir, your account is not allowed to send money.");
        }
        if(!payment.getCreditAccount().hasRight(EAccountOperation.RECEIVE)) {
            throw new ServiceException("Sir, the account you are trying to send money to is not allowed to receive money.");
        }
        if(paymentRepository.findByNumber(payment.getNumber()).isPresent()) {
            throw new ServiceException("Sir, payment number already exists.");
        }

        // Currency conversion
        BigDecimal convertedAmount = currencyConversionService.convert(
                BigDecimal.valueOf(payment.getAmount()),
                payment.getDebitAccount().getCurrency().getCode(),
                payment.getCreditAccount().getCurrency().getCode()
        );

        payment.setConvertedAmount(convertedAmount.longValue());

        String newPaymentNumber = AccountNumberGenerator.generateUniqueAccountNumber(accountRepository);
        payment.setNumber(newPaymentNumber);
        payment.setTimestamp(new Timestamp(System.currentTimeMillis()));
        payment.setStatus(EPaymentStatus.CREATED);

        payment = paymentRepository.save(payment);

        paymentHistoryService.addEntry(payment);
        auditService.add(actor, operation, payment.getNumber());
        return paymentMapper.fromPayment(payment);
    }

    @Override
    public PaymentResponseDto repair(String username, String paymentNumber, PaymentRequestDto paymentRequestDto) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "repair", Payment.class.getSimpleName());

        Payment repairPayment = paymentMapper.toPayment(paymentRequestDto, accountRepository, currencyXmlParser);

        if(paymentRepository.findByNumber(repairPayment.getNumber()).isEmpty()) {
            throw new ServiceException("Sir, the account number does not exists.");
        }

        Operation operation = new Operation(EOperationType.REPAIR, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
        if(payment.getStatus() != EPaymentStatus.IN_REPAIR) {
            throw new ServiceException("Sir, the payment is not in a state that can be repaired.");
        }

        // Currency conversion
        BigDecimal convertedAmount = currencyConversionService.convert(
                BigDecimal.valueOf(repairPayment.getAmount()),
                repairPayment.getDebitAccount().getCurrency().getCode(),
                repairPayment.getCreditAccount().getCurrency().getCode()
        );

        repairPayment.setConvertedAmount(convertedAmount.longValue());

        internalUpdatePayment(payment, repairPayment);
        payment.setStatus(EPaymentStatus.REPAIRED);

        payment = paymentRepository.save(payment);

        paymentHistoryService.addEntry(payment);
        auditService.add(actor, operation, payment.getNumber());
        return paymentMapper.fromPayment(payment);
    }

    @Override
    public boolean approve(String username, String paymentNumber) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "approve", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.APPROVE, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();

        if(Objects.equals(payment.getDebitAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, you cannot approve your own payment.");
        }
        if(Objects.equals(payment.getCreditAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, you cannot approve payments directed to you.");
        }
        if(payment.getStatus() != EPaymentStatus.CREATED && payment.getStatus() != EPaymentStatus.REPAIRED) {
            throw new ServiceException("Sir, the payment is not in a state that can be approved.");
        }

        internalHaltPayment(payment);
        if(internalNeedsVerification(payment)) {
            payment.setStatus(EPaymentStatus.WAITING_VERIFICATION);
        }
        else if(internalNeedsAuthorization(payment)) {
            payment.setStatus(EPaymentStatus.WAITING_AUTHORIZATION);
        }
        else {
            internalCompletePayment(payment);
        }
        paymentRepository.save(payment);

        paymentHistoryService.addEntry(payment);
        auditService.add(actor, operation, paymentNumber);
        return true;
    }

    @Override
    public boolean verify(String username, String paymentNumber) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "verify", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.VERIFY, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();

        if(Objects.equals(payment.getDebitAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, you cannot verify your own payment.");
        }
        if(Objects.equals(payment.getCreditAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, you cannot verify payments directed to you.");
        }
        if(payment.getStatus() != EPaymentStatus.WAITING_VERIFICATION) {
            throw new ServiceException("Sir, the payment is not in a state that can be verified.");
        }

        if(internalNeedsAuthorization(payment)) {
            payment.setStatus(EPaymentStatus.WAITING_AUTHORIZATION);
        }
        else {
            internalCompletePayment(payment);
        }
        paymentRepository.save(payment);

        paymentHistoryService.addEntry(payment);
        auditService.add(actor, operation, paymentNumber);
        return true;
    }

    @Override
    public boolean authorize(String username, String paymentNumber) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "authorize", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.AUTHORIZE, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();

        if(Objects.equals(payment.getDebitAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, you cannot authorize your own payment.");
        }
        if(Objects.equals(payment.getCreditAccount().getOwner().getUserName(), username)) {
            throw new ServiceException("Sir, you cannot authorize payments directed to you.");
        }
        if(payment.getStatus() != EPaymentStatus.WAITING_AUTHORIZATION) {
            throw new ServiceException("Sir, the payment is not in a state that can be authorize.");
        }

        internalCompletePayment(payment);
        paymentRepository.save(payment);

        paymentHistoryService.addEntry(payment);
        auditService.add(actor, operation, paymentNumber);
        return true;
    }

    @Override
    public boolean reject(String username, String paymentNumber) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "reject", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REJECT, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("Unauthorized");
        }
        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
        if(Objects.equals(payment.getDebitAccount().getOwner().getUserName(), actor.getUserName())) {
            throw new ServiceException("Sir, you cannot reject your own payment.");
        }

        switch (payment.getStatus().name().toLowerCase()) {
            case "created" -> internalSendToRepair(payment);
            case "repaired" -> internalCancelPaymentNoHalt(payment);
            case "waiting_verification", "waiting_authorization" -> internalCancelPayment(payment);
            default -> throw new ServiceException("Sir, the payment is not in a state that can be rejected.");
        }
        payment = paymentRepository.save(payment);
        paymentHistoryService.addEntry(payment);

        auditService.add(actor, operation, paymentNumber);
        return true;
    }

    @Override
    public boolean cancel(String username, String paymentNumber) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "cancel", Payment.class.getSimpleName());

        Operation operation = new Operation(EOperationType.CLOSE, Payment.class.getSimpleName());
        if(!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
        if(!Objects.equals(payment.getDebitAccount().getOwner().getUserName(), actor.getUserName())) {
            throw new ServiceException("Sir, you cannot cancel a payment that is not yours.");
        }

        switch (payment.getStatus().name().toLowerCase()) {
            case "created", "in_repair", "repaired" -> internalCancelPaymentNoHalt(payment);
            case "waiting_verification", "waiting_authorization" -> internalCancelPayment(payment);
            default -> throw new ServiceException("Sir, the payment is not in a state that can be cancelled.");
        }

        payment = paymentRepository.save(payment);
        paymentHistoryService.addEntry(payment);

        auditService.add(actor, operation, paymentNumber);
        return true;
    }

    private boolean internalNeedsVerification(Payment payment) {
        return payment.getAmount() > payment.getDebitAccount().getTransactionLimit();
    }

    private boolean internalNeedsAuthorization(Payment payment) {
        Long debitAccountWorkingBalance =
                balanceService.classifiedGetCurrentBalance(payment.getDebitAccount())
                        .getAvailableAmount();
        return payment.getAmount() > debitAccountWorkingBalance;
    }

    private void internalCancelPaymentNoHalt(Payment payment) {
        payment.setStatus(EPaymentStatus.CANCELLED);
    }

    private void internalSendToRepair(Payment payment) {
        payment.setStatus(EPaymentStatus.IN_REPAIR);
    }

    private void internalHaltPayment(Payment payment) {
        balanceService.classifiedHaltPayment(payment);
    }

    // the payment MUST HAVE BEEN HALTED before calling this method
    private void internalCompletePayment(Payment payment) {
        payment.setStatus(EPaymentStatus.COMPLETED);
        balanceService.classifiedCompletePayment(payment);
    }

    // the payment MUST HAVE BEEN HALTED before calling this method
    private void internalCancelPayment(Payment payment) {
        payment.setStatus(EPaymentStatus.CANCELLED);
        balanceService.classifiedCancelPayment(payment);
    }

    private void internalUpdatePayment(Payment originalPayment, Payment patchPayment) {
        try {
            for (Field field : Payment.class.getDeclaredFields()) {
                field.setAccessible(true);
                if(field.get(patchPayment) != null) {
                    field.set(originalPayment, field.get(patchPayment));
                }
            }
        }
        catch (IllegalAccessException e) {
            throw new InternalException("Internal Server Error");
        }
    }
}
