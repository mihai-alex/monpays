package com.monpays.mappers.account;

import com.monpays.dtos.account.AccountHistoryEntryDto;
import com.monpays.dtos.account.AccountPendingResponseDto;
import com.monpays.dtos.account.AccountRequestDto;
import com.monpays.dtos.account.AccountResponseDto;
import com.monpays.entities._generic.Currency;
import com.monpays.entities.account.Account;
import com.monpays.entities.account.AccountHistoryEntry;
import com.monpays.entities.account.AccountPending;
import com.monpays.entities.user.User;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.utils.CurrencyXmlParser;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "accountNumber", source = "accountNumber")
    @Mapping(target = "owner", source = "owner.userName")
    @Mapping(target = "currency", source = "currency.code")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "transactionLimit", source = "transactionLimit")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "accountLockStatus", source = "accountLockStatus")
    @Mapping(target = "version", source = "version")
    AccountResponseDto accountToAccountResponseDto(Account account);

    // check if accountNumber is NULL in the request, if so, don't map it. code below:
    // use the mapUsernameToUser and mapCurrencyCodeToCurrency methods to map the username and currency code to the User and Currency objects

    @Mapping(target = "accountNumber", source = "accountNumber", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "owner", expression = "java(mapUsernameToUser(accountRequestDto.getOwner(), userRepository))")
    @Mapping(target = "currency", expression = "java(mapCurrencyCodeToCurrency(accountRequestDto.getCurrency(), currencyXmlParser))")
    @Mapping(target = "name", source = "name", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "transactionLimit", source = "transactionLimit", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "status", source = "status", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "accountLockStatus", source = "accountLockStatus", nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    @Mapping(target = "version", source = "version")
    Account accountRequestDtoToAccount(
            AccountRequestDto accountRequestDto,
            @Context IUserRepository userRepository,
            @Context CurrencyXmlParser currencyXmlParser);


    @Mapping(source = "entity.accountNumber", target = "accountNumber")
    @Mapping(source = "entity.owner.userName", target = "owner")
    @Mapping(source = "entity.currency.code", target = "currency")
    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.transactionLimit", target = "transactionLimit")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "entity.accountLockStatus", target = "accountLockStatus")
    AccountHistoryEntryDto toHistoryEntryDto(AccountHistoryEntry entity);

    default User mapUsernameToUser(String username, @Context IUserRepository userRepository) {
        return userRepository.findByUserName(username).orElseThrow();
    }

    default Currency mapCurrencyCodeToCurrency(String code, @Context CurrencyXmlParser currencyXmlParser) {
        return currencyXmlParser.getCurrencyByCode(code).orElseThrow();
    }

    @Mapping(source = "entity.accountNumber", target = "accountNumber")
    @Mapping(source = "entity.owner.userName", target = "ownerUserName")
    @Mapping(source = "entity.currency.code", target = "currency")
    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.transactionLimit", target = "transactionLimit")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "entity.accountLockStatus", target = "accountLockStatus")
    @Mapping(source = "username", target = "actorUserName")
    AccountPending toAccountPending(Account entity, String username);

    default AccountPending mapToAccountPending(Account entity, String username) {
        AccountPending accountPending = toAccountPending(entity, username);
        accountPending.setOriginalAccount(entity);
        return accountPending;
    }

    @Mapping(source = "entity.version", target = "version")
    @Mapping(source = "entity.accountNumber", target = "accountNumber")
    @Mapping(source = "entity.owner.userName", target = "owner")
    @Mapping(source = "entity.currency.code", target = "currency")
    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.transactionLimit", target = "transactionLimit")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "entity.accountLockStatus", target = "accountLockStatus")
    AccountResponseDto toResponseDto(Account entity);

    @Mapping(source = "entity.accountNumber", target = "accountNumber")
    @Mapping(source = "entity.ownerUserName", target = "ownerUserName")
    @Mapping(source = "entity.currency", target = "currency")
    @Mapping(source = "entity.name", target = "name")
    @Mapping(source = "entity.transactionLimit", target = "transactionLimit")
    @Mapping(source = "entity.status", target = "status")
    @Mapping(source = "entity.accountLockStatus", target = "accountLockStatus")
    AccountPendingResponseDto toAccountPendingResponseDto(AccountPending entity);
}
