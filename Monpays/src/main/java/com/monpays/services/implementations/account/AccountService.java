package com.monpays.services.implementations.account;

import com.monpays.dtos.account.AccountHistoryEntryDto;
import com.monpays.dtos.account.AccountPendingResponseDto;
import com.monpays.dtos.account.AccountRequestDto;
import com.monpays.dtos.account.AccountResponseDto;
import com.monpays.dtos.audit.AuditEntryDto;
import com.monpays.entities._generic.AuditEntry;
import com.monpays.entities._generic.Operation;
import com.monpays.entities._generic.enums.EOperationType;
import com.monpays.entities.account.Account;
import com.monpays.entities.account.AccountHistoryEntry;
import com.monpays.entities.account.AccountPending;
import com.monpays.entities.account.enums.EAccountLockStatus;
import com.monpays.entities.account.enums.EAccountStatus;
import com.monpays.entities.balance.Balance;
import com.monpays.entities.user.User;
import com.monpays.mappers._generic.AuditMapper;
import com.monpays.mappers.account.AccountMapper;
import com.monpays.persistence.repositories.account.IAccountHistoryRepository;
import com.monpays.persistence.repositories.account.IAccountPendingRepository;
import com.monpays.persistence.repositories.account.IAccountRepository;
import com.monpays.persistence.repositories.balance.IBalanceRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.implementations.user.UserService;
import com.monpays.services.interfaces._generic.IAuditService;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.account.IAccountService;
import com.monpays.utils.AccountNumberGenerator;
import com.monpays.utils.CurrencyXmlParser;
import jakarta.transaction.Transactional;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class AccountService implements IAccountService {

    @Autowired
    private IAccountRepository accountRepository;

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IAccountPendingRepository accountPendingRepository;

    @Autowired
    private IAccountHistoryRepository accountHistoryRepository;

    @Autowired
    private IAuditService auditService;

    @Autowired
    private AccountHistoryService accountHistoryService;

    @Autowired
    private CurrencyXmlParser currencyXmlParser;

    @Autowired
    private AccountMapper accountMapper;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    @Lazy
    private UserService userService;

    @Autowired
    private IBalanceRepository balanceRepository;

    @Autowired
    private IUserActivityService userActivityService;

    @Override
    public List<AccountResponseDto> filterAccounts(String actorUsername, String columnName, String filterValue) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "filterAccounts", Account.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("no rights test");
        }

        auditService.add(actor, operation, null);

        return accountRepository.findAll().stream().map(accountMapper::toResponseDto).collect(Collectors.toList());
    }

    @Override
    public List<AccountResponseDto> getAll(String username) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        Operation operation = new Operation(EOperationType.LIST, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        auditService.add(actor, operation, null);

        return accountRepository.findAll().stream()
                .map(accountMapper::accountToAccountResponseDto)
                .toList();
    }

    @Override
    public AccountResponseDto getOne(String username, String accountNumber, boolean needsPending, boolean needsHistory, boolean needsAudit) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "getOne", Account.class.getSimpleName());

        Operation operation = new Operation(EOperationType.LIST, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber).orElse(null);
        if (account == null) {
            return null;
        }
        auditService.add(actor, operation, account.getAccountNumber());

        AccountResponseDto accountResponseDto = accountMapper.accountToAccountResponseDto(account);

        if (needsPending) {
            AccountPendingResponseDto accountPendingResponseDto = accountMapper.toAccountPendingResponseDto(
                    accountPendingRepository.findByOriginalAccountNumber(account.getAccountNumber()).orElse(null));
            accountResponseDto.setPendingEntity(accountPendingResponseDto);
        }

        if (needsHistory) {
            List<AccountHistoryEntryDto> accountHistoryEntryDtos = new ArrayList<>();
            List<AccountHistoryEntry> accountHistoryEntries = accountHistoryService.getByObject(account);

            accountHistoryEntries.forEach(accountHistoryEntry -> {
                accountHistoryEntryDtos.add(accountMapper.toHistoryEntryDto(accountHistoryEntry));
            });

            accountResponseDto.setHistory(accountHistoryEntryDtos);
        }

        if (needsAudit) {
            List<AuditEntryDto> auditEntryDtos = new ArrayList<>();
            List<AuditEntry> auditEntries = auditService.getByObject(Account.class.getSimpleName(), account.getAccountNumber());

            auditEntries.forEach(auditEntry -> {
                auditEntryDtos.add(auditMapper.toAuditEntryDto(auditEntry));
            });

            accountResponseDto.setAudit(auditEntryDtos);
        }
        return accountResponseDto;
    }

    @Override
    public Account classifiedGetOne(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public AccountResponseDto create(String username, AccountRequestDto accountRequestDto) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "create", Account.class.getSimpleName());

        // should be positive
        if (accountRequestDto.getTransactionLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("");
        }

        if (accountRepository.findByAccountNumber(accountRequestDto.getAccountNumber()).isPresent()) {
            return internalRepair(actor, accountRequestDto);
        } else {
            return internalCreate(actor, accountRequestDto);
        }
    }

    private AccountResponseDto internalCreate(User actor, AccountRequestDto accountRequestDto) {
        Operation operation = new Operation(EOperationType.CREATE, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        String newAccountNumber = AccountNumberGenerator.generateUniqueAccountNumber(accountRepository);
        accountRequestDto.setAccountNumber(newAccountNumber);
        accountRequestDto.setStatus(EAccountStatus.CREATED);
        accountRequestDto.setAccountLockStatus(EAccountLockStatus.OPEN);
        Account account = accountMapper.accountRequestDtoToAccount(accountRequestDto, userRepository, currencyXmlParser);

        try {
            account = accountRepository.save(account);
            accountRepository.flush();
            AccountPending accountPending = accountMapper.mapToAccountPending(account, actor.getUserName());
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        accountHistoryService.addEntry(account);
        auditService.add(actor, operation, accountRequestDto.getAccountNumber());
        return accountMapper.toResponseDto(account);
    }

    private AccountResponseDto internalRepair(User actor, AccountRequestDto accountRequestDto) {
        Operation operation = new Operation(EOperationType.REPAIR, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Account account = accountRepository.findByAccountNumber(accountRequestDto.getAccountNumber()).orElseThrow();
        if (account.getStatus() != EAccountStatus.IN_REPAIR) {
            throw new ServiceException("You cannot repair an account that is not in repair.");
        }
        AccountPending accountPending = accountPendingRepository.findByOriginalAccountNumber(account.getAccountNumber()).orElseThrow();

        account.setName(accountPending.getName());
        account.setTransactionLimit(accountPending.getTransactionLimit());
        account.setStatus(EAccountStatus.REPAIRED);

        accountPending.setName(accountRequestDto.getName());
        accountPending.setTransactionLimit(accountRequestDto.getTransactionLimit());
        accountPending.setStatus(EAccountStatus.REPAIRED);

        try {
            accountRepository.save(account);
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        accountHistoryService.addEntry(account);
        auditService.add(actor, operation, accountRequestDto.getAccountNumber());
        return accountMapper.toResponseDto(account);
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public AccountPendingResponseDto modify(String actorUsername, String accountNumber, AccountRequestDto accountRequestDto) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "modify", Account.class.getSimpleName());

        Operation operation = new Operation(EOperationType.MODIFY, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        if (!Objects.equals(accountNumber, accountRequestDto.getAccountNumber())) {
            throw new ServiceException("");
        }

        // Check if the user has pending modifications and requires approval/rejection
        if (this.existsPending(accountRequestDto.getAccountNumber())) {
            throw new ServiceException("");
        }

        // should be positive
        if (accountRequestDto.getTransactionLimit().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("");
        }

        Account originalAccount = accountRepository.findByAccountNumber(accountNumber).orElseThrow();

        AccountPending accountPending = accountMapper.mapToAccountPending(originalAccount, actorUsername);
        accountPending.setName(accountRequestDto.getName());
        accountPending.setTransactionLimit(accountRequestDto.getTransactionLimit());

        try {
            accountPending = accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, accountNumber);

        Account addToHistory = new Account();
        addToHistory.setId(originalAccount.getId());
        addToHistory.setVersion(originalAccount.getVersion());
        addToHistory.setAccountNumber(originalAccount.getAccountNumber());
        addToHistory.setOwner(originalAccount.getOwner());
        addToHistory.setCurrency(originalAccount.getCurrency());
        addToHistory.setName(originalAccount.getName());
        addToHistory.setTransactionLimit(originalAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(originalAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return accountMapper.toAccountPendingResponseDto(accountPending);
    }


    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean remove(String actorUsername, String accountNumber) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "remove", Account.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REMOVE, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Optional<Account> toDeleteAccountOptional = accountRepository.findByAccountNumber(accountNumber);
        Account toDeleteAccount;
        if (toDeleteAccountOptional.isEmpty()) {
            return false;
        } else {
            toDeleteAccount = toDeleteAccountOptional.get();
        }

        // Check if the profile has pending modifications and requires approval/rejection
        if (this.existsPending(toDeleteAccount.getAccountNumber())) {
            throw new ServiceException("");
        }

        if (toDeleteAccount.getAccountLockStatus() != EAccountLockStatus.CLOSED) {
            throw new ServiceException("You cannot remove an account that is not closed.");
        }

        if (toDeleteAccount.getStatus() == EAccountStatus.REMOVED) {
            throw new ServiceException("You cannot remove an account that is already removed.");
        }

        AccountPending accountPending = accountMapper.mapToAccountPending(toDeleteAccount, actorUsername);
        accountPending.setStatus(EAccountStatus.REMOVED);
        try {
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, toDeleteAccount.getAccountNumber());

        Account addToHistory = new Account();
        addToHistory.setId(toDeleteAccount.getId());
        addToHistory.setVersion(toDeleteAccount.getVersion());
        addToHistory.setAccountNumber(toDeleteAccount.getAccountNumber());
        addToHistory.setOwner(toDeleteAccount.getOwner());
        addToHistory.setCurrency(toDeleteAccount.getCurrency());
        addToHistory.setName(toDeleteAccount.getName());
        addToHistory.setTransactionLimit(toDeleteAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(toDeleteAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean approve(String actorUsername, String accountNumber) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "approve", Account.class.getSimpleName());

        Operation operation = new Operation(EOperationType.APPROVE, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        if (account.getStatus() == EAccountStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        AccountPending accountPending = verifyPendingAndNotSameUser(actorUsername, accountNumber);

        boolean result;

        switch (account.getStatus().toString().toLowerCase()) {
            case "created", "repaired" -> {
                result = internalApproveCreation(account, accountPending);
            }
            default -> {
                result = internalApproveModification(account, accountPending);
            }
        }

        auditService.add(actor, operation, accountNumber);
        return result;
    }

    private boolean internalApproveCreation(Account account, AccountPending accountPending) {
        account.setStatus(EAccountStatus.ACTIVE);
        try {
            account = accountRepository.save(account);
            accountRepository.flush();

            Balance balance0 = new Balance();
            balance0.setAccount(account);
            balance0.setAvailableCreditAmount(BigDecimal.ZERO);
            balance0.setAvailableCreditCount(0);
            balance0.setAvailableDebitAmount(BigDecimal.ZERO);
            balance0.setAvailableDebitCount(0);
            balance0.setPendingCreditAmount(BigDecimal.ZERO);
            balance0.setPendingCreditCount(0);
            balance0.setPendingDebitAmount(BigDecimal.ZERO);
            balance0.setPendingDebitCount(0);
            balanceRepository.save(balance0);

        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        // delete the pending entry from the pending table
        accountPendingRepository.delete(accountPending);

        accountHistoryService.addEntry(account);

        return true;
    }

    private boolean internalApproveModification(Account originalAccount, AccountPending accountPending) {
        originalAccount.setName(accountPending.getName());
        originalAccount.setTransactionLimit(accountPending.getTransactionLimit());
        originalAccount.setStatus(accountPending.getStatus());
        originalAccount.setAccountLockStatus(accountPending.getAccountLockStatus());

        try {
            originalAccount = accountRepository.save(originalAccount);
        } catch (OptimisticLockingFailureException e) {
            throw new ServiceException("Concurrent update detected. Please try again.");
        }

        // delete the pending entry from the pending table
        accountPendingRepository.delete(accountPending);

        Account addToHistory = new Account();
        addToHistory.setId(originalAccount.getId());
        addToHistory.setVersion(originalAccount.getVersion());
        addToHistory.setAccountNumber(originalAccount.getAccountNumber());
        addToHistory.setOwner(originalAccount.getOwner());
        addToHistory.setCurrency(originalAccount.getCurrency());
        addToHistory.setName(originalAccount.getName());
        addToHistory.setTransactionLimit(originalAccount.getTransactionLimit());
        addToHistory.setStatus(originalAccount.getStatus());
        addToHistory.setAccountLockStatus(originalAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean reject(String actorUsername, String accountNumber) {
        User actor = userRepository.findByUserName(actorUsername).orElseThrow();

        userActivityService.add(actor, "reject", Account.class.getSimpleName());

        Operation operation = new Operation(EOperationType.REJECT, Account.class.getSimpleName());
        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();
        if (account.getStatus() == EAccountStatus.IN_REPAIR) {
            throw new ServiceException("");
        }

        AccountPending accountPending = verifyPendingAndNotSameUser(actorUsername, accountNumber);
        boolean result;

        switch (account.getStatus().toString().toLowerCase()) {
            case "created" -> {
                result = internalRejectCreation(account, accountPending);
            }
            case "repaired" -> {
                result = internalRejectReparation(account, accountPending);
            }
            default -> {
                result = internalRejectModification(account, accountPending);
            }
        }

        auditService.add(actor, operation, accountNumber);
        return result;
    }

    private boolean internalRejectCreation(Account account, AccountPending accountPending) {
        account.setStatus(EAccountStatus.IN_REPAIR);
        accountPending.setStatus(EAccountStatus.IN_REPAIR);

        try {
            accountRepository.save(account);
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        return true;
    }

    private boolean internalRejectReparation(Account account, AccountPending accountPending) {
        accountHistoryRepository.deleteAll(accountHistoryRepository.findAllByAccount(account));
        accountPendingRepository.delete(accountPending);
        accountRepository.delete(account);

        return true;
    }

    private boolean internalRejectModification(Account oldAccount, AccountPending accountPending) {
        accountPendingRepository.delete(accountPending);

        Account addToHistory = new Account();
        addToHistory.setId(oldAccount.getId());
        addToHistory.setVersion(oldAccount.getVersion());
        addToHistory.setAccountNumber(oldAccount.getAccountNumber());
        addToHistory.setOwner(oldAccount.getOwner());
        addToHistory.setCurrency(oldAccount.getCurrency());
        addToHistory.setName(oldAccount.getName());
        addToHistory.setTransactionLimit(oldAccount.getTransactionLimit());
        addToHistory.setStatus(oldAccount.getStatus());
        addToHistory.setAccountLockStatus(oldAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    private AccountPending verifyPendingAndNotSameUser(String actorUsername, String accountNumber) {
        AccountPending accountPending = accountPendingRepository
                .findByOriginalAccountNumber(accountNumber).orElseThrow();

        if (Objects.equals(accountPending.getActorUserName(), actorUsername)) {
            throw new ServiceException("");
        }

        return accountPending;
    }

    private boolean existsPending(String accountNumber) {
        return accountPendingRepository.findByOriginalAccountNumber(accountNumber).isPresent();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRES_NEW)
    public boolean changeAccountStatus(String username, String accountNumber, String operationType) {
        User actor = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(actor, "changeAccountStatus", Account.class.getSimpleName());

        Account toChangeAccount = accountRepository.findByAccountNumber(accountNumber).orElseThrow();

        if (this.existsPending(toChangeAccount.getAccountNumber())) {
            throw new ServiceException("");
        }

        Supplier<Boolean> internalChangEAccountStatusMethod;
        Operation operation;

        switch (operationType.toLowerCase()) {
            case "block" -> {
                operation = new Operation(EOperationType.BLOCK, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> block(actor, operation, toChangeAccount);
            }
            case "block_credit" -> {
                operation = new Operation(EOperationType.BLOCK_CREDIT, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> blockCredit(actor, operation, toChangeAccount);
            }
            case "block_debit" -> {
                operation = new Operation(EOperationType.BLOCK_DEBIT, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> blockDebit(actor, operation, toChangeAccount);
            }
            case "unblock" -> {
                operation = new Operation(EOperationType.UNBLOCK, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> unblock(actor, operation, toChangeAccount);
            }
            case "unblock_credit" -> {
                operation = new Operation(EOperationType.UNBLOCK_CREDIT, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> unblockCredit(actor, operation, toChangeAccount);
            }
            case "unblock_debit" -> {
                operation = new Operation(EOperationType.UNBLOCK_DEBIT, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> unblockDebit(actor, operation, toChangeAccount);
            }
            case "close" -> {
                operation = new Operation(EOperationType.CLOSE, Account.class.getSimpleName());
                internalChangEAccountStatusMethod = () -> close(actor, operation, toChangeAccount);
            }
            default -> throw new ServiceException("Invalid operation type.");
        }

        if (!actor.hasRight(operation)) {
            throw new ServiceException("");
        }

        return internalChangEAccountStatusMethod.get();
    }

    private boolean block(User actor, Operation operation, Account toBlockAccount) {
        if (toBlockAccount.getAccountLockStatus() != EAccountLockStatus.OPEN) {
            throw new ServiceException("Operation not allowed!");
        }

        AccountPending accountPending = accountMapper.mapToAccountPending(toBlockAccount, actor.getUserName());
        accountPending.setAccountLockStatus(EAccountLockStatus.BLOCKED);
        try {
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, toBlockAccount.getAccountNumber());

        Account addToHistory = new Account();
        addToHistory.setId(toBlockAccount.getId());
        addToHistory.setVersion(toBlockAccount.getVersion());
        addToHistory.setAccountNumber(toBlockAccount.getAccountNumber());
        addToHistory.setOwner(toBlockAccount.getOwner());
        addToHistory.setCurrency(toBlockAccount.getCurrency());
        addToHistory.setName(toBlockAccount.getName());
        addToHistory.setTransactionLimit(toBlockAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(toBlockAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    private boolean blockCredit(User actor, Operation operation, Account toBlockAccount) {
        return this.internalBlockOne(EAccountLockStatus.BLOCKED_CREDIT, EAccountLockStatus.BLOCKED_DEBIT, actor, operation, toBlockAccount);
    }

    private boolean blockDebit(User actor, Operation operation, Account toBlockAccount) {
        return this.internalBlockOne(EAccountLockStatus.BLOCKED_DEBIT, EAccountLockStatus.BLOCKED_CREDIT, actor, operation, toBlockAccount);
    }

    private boolean internalBlockOne(EAccountLockStatus thisBlockStatus, EAccountLockStatus otherBlockStatus,
                                     User actor, Operation operation, Account toBlockAccount) {
        if (toBlockAccount.getAccountLockStatus() != EAccountLockStatus.OPEN &&
                toBlockAccount.getAccountLockStatus() != otherBlockStatus) {
            throw new ServiceException("Operation not allowed!");
        }

        AccountPending accountPending = accountMapper.mapToAccountPending(toBlockAccount, actor.getUserName());

        try {
            if (toBlockAccount.getAccountLockStatus() == EAccountLockStatus.OPEN) {
                accountPending.setAccountLockStatus(thisBlockStatus);
                auditService.add(actor, operation, toBlockAccount.getAccountNumber());
            } else {
                User system = userRepository.findByUserName("system").orElseThrow();
                Operation systemOperation = new Operation(EOperationType.BLOCK, Account.class.getSimpleName());
                accountPending.setAccountLockStatus(EAccountLockStatus.BLOCKED);
                auditService.add(actor, operation, toBlockAccount.getAccountNumber());
                auditService.add(system, systemOperation, toBlockAccount.getAccountNumber());
            }

            try {
                accountPendingRepository.save(accountPending);
            } catch (OptimisticLockingFailureException e) {
                throw new RuntimeException("Concurrent update detected. Please try again.");
            }
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        Account addToHistory = new Account();
        addToHistory.setId(toBlockAccount.getId());
        addToHistory.setVersion(toBlockAccount.getVersion());
        addToHistory.setAccountNumber(toBlockAccount.getAccountNumber());
        addToHistory.setOwner(toBlockAccount.getOwner());
        addToHistory.setCurrency(toBlockAccount.getCurrency());
        addToHistory.setName(toBlockAccount.getName());
        addToHistory.setTransactionLimit(toBlockAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(toBlockAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    private boolean unblock(User actor, Operation operation, Account toUnblockAccount) {
        if (toUnblockAccount.getAccountLockStatus() != EAccountLockStatus.BLOCKED) {
            throw new ServiceException("Operation not allowed!");
        }

        AccountPending accountPending = accountMapper.mapToAccountPending(toUnblockAccount, actor.getUserName());
        accountPending.setAccountLockStatus(EAccountLockStatus.OPEN);
        try {
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, toUnblockAccount.getAccountNumber());

        Account addToHistory = new Account();
        addToHistory.setId(toUnblockAccount.getId());
        addToHistory.setVersion(toUnblockAccount.getVersion());
        addToHistory.setAccountNumber(toUnblockAccount.getAccountNumber());
        addToHistory.setOwner(toUnblockAccount.getOwner());
        addToHistory.setCurrency(toUnblockAccount.getCurrency());
        addToHistory.setName(toUnblockAccount.getName());
        addToHistory.setTransactionLimit(toUnblockAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(toUnblockAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    private boolean unblockCredit(User actor, Operation operation, Account toUnblockAccount) {
        return this.internalUnblockOne(EAccountLockStatus.BLOCKED_CREDIT, EAccountLockStatus.BLOCKED_DEBIT,
                EOperationType.BLOCK_DEBIT, actor, operation, toUnblockAccount);
    }

    private boolean unblockDebit(User actor, Operation operation, Account toUnblockAccount) {
        return this.internalUnblockOne(EAccountLockStatus.BLOCKED_DEBIT, EAccountLockStatus.BLOCKED_CREDIT,
                EOperationType.BLOCK_CREDIT, actor, operation, toUnblockAccount);
    }

    private boolean internalUnblockOne(EAccountLockStatus thisUnblockStatus, EAccountLockStatus otherUnblockStatus,
                                       EOperationType otherOperationType, User actor, Operation operation,
                                       Account toUnblockAccount) {

        if (toUnblockAccount.getAccountLockStatus() != thisUnblockStatus &&
                toUnblockAccount.getAccountLockStatus() != EAccountLockStatus.BLOCKED) {
            throw new ServiceException("Operation not allowed!");
        }

        AccountPending accountPending = accountMapper.mapToAccountPending(toUnblockAccount, actor.getUserName());

        try {
            if (toUnblockAccount.getAccountLockStatus() == thisUnblockStatus) {
                accountPending.setAccountLockStatus(EAccountLockStatus.OPEN);
                auditService.add(actor, operation, toUnblockAccount.getAccountNumber());
            } else {
                User system = userRepository.findByUserName("system").orElseThrow();
                Operation systemOperation = new Operation(otherOperationType, Account.class.getSimpleName());
                accountPending.setAccountLockStatus(otherUnblockStatus);
                auditService.add(actor, operation, toUnblockAccount.getAccountNumber());
                auditService.add(system, systemOperation, toUnblockAccount.getAccountNumber());
            }

            try {
                accountPendingRepository.save(accountPending);
            } catch (OptimisticLockingFailureException e) {
                throw new RuntimeException("Concurrent update detected. Please try again.");
            }
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        Account addToHistory = new Account();
        addToHistory.setId(toUnblockAccount.getId());
        addToHistory.setVersion(toUnblockAccount.getVersion());
        addToHistory.setAccountNumber(toUnblockAccount.getAccountNumber());
        addToHistory.setOwner(toUnblockAccount.getOwner());
        addToHistory.setCurrency(toUnblockAccount.getCurrency());
        addToHistory.setName(toUnblockAccount.getName());
        addToHistory.setTransactionLimit(toUnblockAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(toUnblockAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }

    private boolean close(User actor, Operation operation, Account toCloseAccount) {
        if (toCloseAccount.getAccountLockStatus() == EAccountLockStatus.CLOSED) {
            throw new ServiceException("Operation not allowed!");
        }

        AccountPending accountPending = accountMapper.mapToAccountPending(toCloseAccount, actor.getUserName());
        accountPending.setAccountLockStatus(EAccountLockStatus.CLOSED);
        try {
            accountPendingRepository.save(accountPending);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        auditService.add(actor, operation, toCloseAccount.getAccountNumber());

        Account addToHistory = new Account();
        addToHistory.setId(toCloseAccount.getId());
        addToHistory.setVersion(toCloseAccount.getVersion());
        addToHistory.setAccountNumber(toCloseAccount.getAccountNumber());
        addToHistory.setOwner(toCloseAccount.getOwner());
        addToHistory.setCurrency(toCloseAccount.getCurrency());
        addToHistory.setName(toCloseAccount.getName());
        addToHistory.setTransactionLimit(toCloseAccount.getTransactionLimit());
        addToHistory.setStatus(EAccountStatus.MODIFIED);
        addToHistory.setAccountLockStatus(toCloseAccount.getAccountLockStatus());
        accountHistoryService.addEntry(addToHistory);

        return true;
    }
}
