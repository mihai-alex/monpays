package com.monpays;

import com.monpays.controllers.AccountController;
import com.monpays.dtos.account.AccountRequestDto;
import com.monpays.dtos.account.AccountResponseDto;
import com.monpays.entities.account.Account;
import com.monpays.persistence.repositories.account.IAccountHistoryRepository;
import com.monpays.persistence.repositories.account.IAccountPendingRepository;
import com.monpays.persistence.repositories.account.IAccountRepository;
import com.monpays.persistence.repositories.balance.IBalanceRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.logging.Logger;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AccountTests {
    @Autowired
    private AccountController accountController;
    @Autowired
    private IAccountRepository accountRepository;
    @Autowired
    private IAccountPendingRepository accountPendingRepository;
    @Autowired
    private IAccountHistoryRepository accountHistoryRepository;
    @Autowired
    private IBalanceRepository balancesRepository;
    private final Logger log = Logger.getLogger(AccountTests.class.getName());

//    @Test
//    void testFail() {
//        Assertions.fail();
//    }

    private AccountRequestDto createAccountRequestDto() {
        AccountRequestDto accountRequestDto = new AccountRequestDto();
        accountRequestDto.setOwner("a1");
        accountRequestDto.setCurrency("EUR");
        accountRequestDto.setTransactionLimit(170000L);
        accountRequestDto.setName("cont_1");

        return accountRequestDto;
    }

    @Test
    @Order(10)
    void testRepair1() {
        String accountNumber = internalTestCreate();
        internalTestNotApproveBySame(accountNumber);
        internalTestNotApproveByNoRightsUser(accountNumber);
        internalTestApproveByOther(accountNumber);

        internalTestAndClearResult(accountNumber);
    }

    @Test
    @Order(20)
    void testRepair2() {
        String accountNumber = internalTestCreate();
        internalTestNotRejectBySame(accountNumber);
        internalTestNotRejectByNoRightsUser(accountNumber);
        internalTestRejectByOther(accountNumber);
        internalTestRepair(accountNumber);
        internalTestApproveByOther(accountNumber);

        internalTestAndClearResult(accountNumber);
    }

    @Test
    @Order(30)
    void testRepair3() {
        String accountNumber = internalTestCreate();
        internalTestRejectByOther(accountNumber);
        internalTestRepair(accountNumber);
        internalTestRejectByOther(accountNumber);

        internalTestNotAndClearResult(accountNumber);
    }

    private String internalTestCreate() {
        // create
        AccountRequestDto accountRequestDto = this.createAccountRequestDto();

        ResponseEntity<?> responseEntity = accountController.create("a1", accountRequestDto);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        AccountResponseDto responseDto = (AccountResponseDto) responseEntity.getBody();
        assert responseDto != null;
        log.info("passed testCreate test");

        accountRepository.findByAccountNumber(responseDto.getAccountNumber()).ifPresent(account -> {
            Assertions.assertEquals(account.getAccountNumber(), responseDto.getAccountNumber());
            Assertions.assertEquals(account.getOwner().getUserName(), "a1");
            Assertions.assertEquals(account.getTransactionLimit(), 170000L);
            Assertions.assertEquals(account.getCurrency().getCode(), "EUR");
        });
        accountPendingRepository.findByOriginalAccountNumber(responseDto.getAccountNumber())
                .ifPresent(accountPending -> {
            Assertions.assertEquals(accountPending.getAccountNumber(), responseDto.getAccountNumber());
            Assertions.assertEquals(accountPending.getOwnerUserName(), "a1");
            Assertions.assertEquals(accountPending.getTransactionLimit(), 170000L);
            Assertions.assertEquals(accountPending.getCurrency(), "EUR");
            Assertions.assertEquals(accountPending.getActorUserName(), "a1");
        });

        return responseDto.getAccountNumber();
    }

    private void internalTestRepair(String accountNumber) {
        // repair
        AccountRequestDto accountRequestDto = this.createAccountRequestDto();
        accountRequestDto.setAccountNumber(accountNumber);

        ResponseEntity<?> responseEntity = accountController.create("a1", accountRequestDto);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testRepair test");
    }

    private void internalTestNotApproveBySame(String accountNumber) {
        // approve by same
        ResponseEntity<?> responseEntity = accountController.approve("a1", accountNumber);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotApproveBySame test");
    }

    private void internalTestNotApproveByNoRightsUser(String accountNumber) {
        // approve by no rights user
        ResponseEntity<?> responseEntity = accountController.approve("c1", accountNumber);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotApproveBySame test");
    }

    private void internalTestApproveByOther(String accountNumber) {
        // approve by other
        ResponseEntity<?> responseEntity = accountController.approve("a2", accountNumber);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testApproveByOther test");
    }

    private void internalTestNotRejectBySame(String accountNumber) {
        // reject by same
        ResponseEntity<?> responseEntity = accountController.reject("a1", accountNumber);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotRejectBySame test");
    }

    private void internalTestNotRejectByNoRightsUser(String accountNumber) {
        // reject by no rights user
        ResponseEntity<?> responseEntity = accountController.reject("c1", accountNumber);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotRejectByNoRightsUser test");
    }

    private void internalTestRejectByOther(String accountNumber) {
        // reject by other
        ResponseEntity<?> responseEntity = accountController.reject("a2", accountNumber);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testRejectByOther test");
    }

    private void internalTestAndClearResult(String accountNumber) {
        // check result
        ResponseEntity<?> responseEntity = accountController.getOne("a1", accountNumber, false, false, false);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        AccountResponseDto accountResponseDto = (AccountResponseDto) responseEntity.getBody();
        Assertions.assertNotNull(accountResponseDto);
        Assertions.assertEquals(accountResponseDto.getAccountNumber(), accountNumber);
        Assertions.assertEquals(accountResponseDto.getName(), "cont_1");
        Assertions.assertEquals(accountResponseDto.getTransactionLimit(), 170000L);
        Assertions.assertEquals(accountResponseDto.getOwner(), "a1");
        log.info("passed testAndClearResult test");

        // clear result
        Account account = accountRepository.findByAccountNumber(accountNumber).orElseThrow();

        accountHistoryRepository.deleteAll(accountHistoryRepository.findAllByAccount(account));

        if(accountPendingRepository.findByOriginalAccountNumber(accountNumber).isPresent()) {
            Assertions.fail("profile pending should not exist");
        }

        balancesRepository.deleteAll(balancesRepository.findAllByAccount(account));
        accountRepository.delete(account);
    }

    private void internalTestNotAndClearResult(String accountNumber) {
        // check result
        ResponseEntity<?> responseEntity = accountController.getOne("a1", accountNumber, false, false, false);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        AccountResponseDto accountResponseDto = (AccountResponseDto) responseEntity.getBody();
        Assertions.assertNull(accountResponseDto);

        // clear result
    }
}
