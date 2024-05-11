package com.monpays.mappers.payment;

import com.monpays.dtos.payment.PaymentRequestDto;
import com.monpays.dtos.payment.PaymentResponseDto;
import com.monpays.entities._generic.Currency;
import com.monpays.entities.account.Account;
import com.monpays.entities.payment.Payment;
import com.monpays.entities.payment.PaymentHistoryEntry;
import com.monpays.entities.payment.enums.EPaymentType;
import com.monpays.persistence.repositories.account.IAccountRepository;
import com.monpays.utils.CurrencyXmlParser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", source = "paymentRequestDto.number")
    @Mapping(target = "timestamp", ignore = true)
    @Mapping(target = "currency", expression = "java(mapCurrencyToCurrency(paymentRequestDto.getCurrency(), currencyXmlParser))")
    @Mapping(target = "amount", source = "paymentRequestDto.amount")
    @Mapping(target = "creditAccount", expression = "java(mapAccountNumberToAccount(paymentRequestDto.getCreditAccountNumber(), accountRepository))")
    @Mapping(target = "debitAccount", expression = "java(mapAccountNumberToAccount(paymentRequestDto.getDebitAccountNumber(), accountRepository))")
    @Mapping(target = "description", source = "paymentRequestDto.description")
    @Mapping(target = "type", expression = "java(getInternalPaymentType())")
    @Mapping(target = "status", ignore = true)
    Payment toPayment(PaymentRequestDto paymentRequestDto, IAccountRepository accountRepository, CurrencyXmlParser currencyXmlParser);


    @Mapping(target = "number", source = "payment.number")
    @Mapping(target = "timestamp", source = "payment.timestamp")
    @Mapping(target = "currency", source = "payment.currency.code")
    @Mapping(target = "amount", source = "payment.amount")
    @Mapping(target = "debitAccountNumber", source = "payment.debitAccount.accountNumber")
    @Mapping(target = "creditAccountNumber", source = "payment.creditAccount.accountNumber")
    @Mapping(target = "description", source = "payment.description")
    @Mapping(target = "type", source = "payment.type")
    @Mapping(target = "status", source = "payment.status")
    PaymentResponseDto fromPayment(Payment payment);


    @Mapping(target = "historyEntryCreationTimestamp", expression = "java(new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "number", source = "payment.number")
    @Mapping(target = "timestamp", source = "payment.timestamp")
    @Mapping(target = "currency", source = "payment.currency")
    @Mapping(target = "amount", source = "payment.amount")
    @Mapping(target = "debitAccount", source = "payment.debitAccount")
    @Mapping(target = "creditAccount", source = "payment.creditAccount")
    @Mapping(target = "description", source = "payment.description")
    @Mapping(target = "type", source = "payment.type")
    @Mapping(target = "status", source = "payment.status")
    @Mapping(target = "payment", source = "payment")
    PaymentHistoryEntry toPaymentHistoryEntry(Payment payment);

    default Currency mapCurrencyToCurrency(String currencyCode, CurrencyXmlParser currencyXmlParser) {
        return currencyXmlParser.getCurrencies()
                .stream()
                .filter(c -> Objects.equals(c.getCode(), currencyCode))
                .findFirst()
                .orElseThrow();
    }

    default Account mapAccountNumberToAccount(String accountNumber, IAccountRepository accountRepository) {
        return accountRepository.findByAccountNumber(accountNumber).orElseThrow();
    }

    default EPaymentType getInternalPaymentType() {
        return EPaymentType.INTERNAL;
    }
}
