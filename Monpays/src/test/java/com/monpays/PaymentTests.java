//package com.monpays;
//
//import com.monpays.controllers.PaymentController;
//import com.monpays.dtos.payment.PaymentRequestDto;
//import com.monpays.dtos.payment.PaymentResponseDto;
//import com.monpays.entities.account.Account;
//import com.monpays.entities.balance.Balance;
//import com.monpays.entities.payment.Payment;
//import com.monpays.entities.payment.enums.EPaymentStatus;
//import com.monpays.persistence.repositories.payment.IPaymentHistoryRepository;
//import com.monpays.persistence.repositories.payment.IPaymentRepository;
//import com.monpays.services.interfaces.account.IAccountService;
//import com.monpays.services.interfaces.balance.IBalanceService;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.MethodOrderer;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.TestMethodOrder;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import java.util.List;
//import java.util.logging.Logger;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class PaymentTests {
//    @Autowired
//    private PaymentController paymentController;
//    @Autowired
//    private IPaymentRepository paymentRepository;
//    @Autowired
//    private IPaymentHistoryRepository paymentHistoryRepository;
//    @Autowired
//    private IBalanceService balanceService;
//    @Autowired
//    private IAccountService accountService;
//
//    private final Logger log = Logger.getLogger(PaymentTests.class.getName());
//    private static final Long c1_account1_initialBalance = 1000000L;
//    private static final Long account_limits = 100000L;
//
////    @Test
////    void testFail() {
////        Assertions.fail();
////    }
//
//    @Test
//    void testDirectApprove() {            // approve from first step
//        String debitAccountNumber = getAccountNumber("c1_account1");
//        String creditAccountNumber = getAccountNumber("c2_account1");
//        String paymentNumber = internalTestCreate(100L, "c1",
//                debitAccountNumber, creditAccountNumber);
//        internalTestNotApproveBySame(paymentNumber);
//        internalTestNotApproveByNoRightsUser(paymentNumber);
//        internalTestApproveByOther(paymentNumber);
//
//        assertStatus(paymentNumber, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance - 100L);
//        assertAccountBalance(creditAccountNumber, 100L);
//
//        // send back the money
//        String paymentNumber2 = internalTestCreate(100L, "c2",
//                creditAccountNumber, debitAccountNumber);
//        internalTestApproveByOther(paymentNumber2);
//
//        assertStatus(paymentNumber2, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance);
//        assertAccountBalance(creditAccountNumber, 0L);
//    }
//
//    @Test
//    void testRepairAndApprove() {
//        String debitAccountNumber = getAccountNumber("c1_account1");
//        String creditAccountNumber = getAccountNumber("c2_account1");
//        String paymentNumber = internalTestCreate(100L, "c1",
//                debitAccountNumber, creditAccountNumber);
//        internalTestReject(paymentNumber, "a1");            // reject
//
//        assertStatus(paymentNumber, EPaymentStatus.IN_REPAIR);
//
//        internalTestRepair(paymentNumber, 100L, "c1",
//                debitAccountNumber, creditAccountNumber);                   // repair
//
//        assertStatus(paymentNumber, EPaymentStatus.REPAIRED);
//
//        internalTestApproveByOther(paymentNumber);                          // approve
//
//        assertStatus(paymentNumber, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance - 100L);
//        assertAccountBalance(creditAccountNumber, 100L);
//
//        // send back the money                                              // send back the money
//        String paymentNumber2 = internalTestCreate(100L, "c2",
//                creditAccountNumber, debitAccountNumber);
//        internalTestApproveByOther(paymentNumber2);
//
//        assertStatus(paymentNumber2, EPaymentStatus.COMPLETED);             // last verification
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance);
//        assertAccountBalance(creditAccountNumber, 0L);
//    }
//
//    @Test
//    void testRepairAndReject() {
//        String debitAccountNumber = getAccountNumber("c1_account1");
//        String creditAccountNumber = getAccountNumber("c2_account1");
//        String paymentNumber = internalTestCreate(100L, "c1",
//                debitAccountNumber, creditAccountNumber);
//        internalTestReject(paymentNumber, "a1");            // reject
//
//        assertStatus(paymentNumber, EPaymentStatus.IN_REPAIR);
//
//        internalTestRepair(paymentNumber, 100L, "c1",
//                debitAccountNumber, creditAccountNumber);                   // repair
//
//        assertStatus(paymentNumber, EPaymentStatus.REPAIRED);
//
//        internalTestReject(paymentNumber, "a1");            // reject
//
//        assertStatus(paymentNumber, EPaymentStatus.CANCELLED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance);
//        assertAccountBalance(creditAccountNumber, 0L);
//    }
//
//    @Test
//    void testCancel() {
//        String debitAccountNumber = getAccountNumber("c1_account1");
//        String creditAccountNumber = getAccountNumber("c2_account1");
//        String paymentNumber = internalTestCreate(100L, "c1",
//                debitAccountNumber, creditAccountNumber);
//        internalTestCancel(paymentNumber);
//
//        assertStatus(paymentNumber, EPaymentStatus.CANCELLED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance);
//        assertAccountBalance(creditAccountNumber, 0L);
//    }
//
//    @Test
//    void testVerify() {
//        Long amount = account_limits + 100L;
//        String debitAccountNumber = getAccountNumber("c1_account1");
//        String creditAccountNumber = getAccountNumber("c2_account1");
//        String paymentNumber = internalTestCreate(amount, "c1",
//                debitAccountNumber, creditAccountNumber);
//        internalTestApproveByOther(paymentNumber);
//
//        assertStatus(paymentNumber, EPaymentStatus.WAITING_VERIFICATION);
//        internalTestVerify(paymentNumber, "a1");
//
//        assertStatus(paymentNumber, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance - amount);
//        assertAccountBalance(creditAccountNumber, amount);
//
//        // send back the money
//        String paymentNumber2 = internalTestCreate(amount, "c2",
//                creditAccountNumber, debitAccountNumber);
//        internalTestApproveByOther(paymentNumber2);
//
//        assertStatus(paymentNumber2, EPaymentStatus.WAITING_VERIFICATION);
//        internalTestVerify(paymentNumber2, "a1");
//
//        assertStatus(paymentNumber2, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance);
//        assertAccountBalance(creditAccountNumber, 0L);
//    }
//
//    @Test
//    void testVerifyAndAuthorize() {
//        Long amount = c1_account1_initialBalance + 100L;
//        String debitAccountNumber = getAccountNumber("c1_account1");
//        String creditAccountNumber = getAccountNumber("c2_account1");
//        String paymentNumber = internalTestCreate(amount, "c1",
//                debitAccountNumber, creditAccountNumber);
//        internalTestApproveByOther(paymentNumber);
//
//        assertStatus(paymentNumber, EPaymentStatus.WAITING_VERIFICATION);
//        internalTestVerify(paymentNumber, "a1");
//
//        assertStatus(paymentNumber, EPaymentStatus.WAITING_AUTHORIZATION);
//        internalTestAuthorize(paymentNumber, "a1");
//
//        assertStatus(paymentNumber, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance - amount);
//        assertAccountBalance(creditAccountNumber, amount);
//
//        // send back the money
//        String paymentNumber2 = internalTestCreate(amount, "c2",
//                creditAccountNumber, debitAccountNumber);
//        internalTestApproveByOther(paymentNumber2);
//
//        assertStatus(paymentNumber2, EPaymentStatus.WAITING_VERIFICATION);
//        internalTestVerify(paymentNumber2, "a1");
//
//        assertStatus(paymentNumber2, EPaymentStatus.COMPLETED);
//        assertAccountBalance(debitAccountNumber, c1_account1_initialBalance);
//        assertAccountBalance(creditAccountNumber, 0L);
//    }
//
//
//
//
//
//
//
//    private String getAccountNumber(String accountName) {
//        return accountService.getAll("a1")
//                .stream()
//                .filter(account -> account.getName().equals(accountName))
//                .findFirst()
//                .orElseThrow()
//                .getAccountNumber();
//    }
//
//    private void assertAccountBalance(String accountNumber, Long availableAmount) {
//        Account account = accountService.classifiedGetOne(accountNumber);
//        Balance balance = balanceService.classifiedGetCurrentBalance(account);
//        Assertions.assertEquals(balance.getAvailableAmount(), availableAmount);
//    }
//
//    private PaymentRequestDto internalCreatePaymentRequestDto(Long amount,
//                                                              String debitAccountNumber, String creditAccountNumber) {
//        PaymentRequestDto paymentRequestDto = new PaymentRequestDto();
//        paymentRequestDto.setCurrency("EUR");
//        paymentRequestDto.setAmount(amount);
//        paymentRequestDto.setDebitAccountNumber(debitAccountNumber);
//        paymentRequestDto.setCreditAccountNumber(creditAccountNumber);
//        paymentRequestDto.setDescription("test payment");
//        return paymentRequestDto;
//    }
//
//    private String internalTestCreate(Long amount, String debitOwnerUsername,
//                                      String debitAccountNumber, String creditAccountNumber) {
//        // create
//        PaymentRequestDto paymentRequestDto =
//                this.internalCreatePaymentRequestDto(amount, debitAccountNumber, creditAccountNumber);
//
//        ResponseEntity<?> responseEntity = paymentController.create(debitOwnerUsername, paymentRequestDto);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        PaymentResponseDto responseDto = (PaymentResponseDto) responseEntity.getBody();
//        assert responseDto != null;
//        log.info("passed testCreate test");
//
//        paymentRepository.findByNumber(responseDto.getNumber()).ifPresent(payment -> {
//            Assertions.assertEquals(payment.getNumber(), responseDto.getNumber());
//            Assertions.assertEquals(payment.getDebitAccount().getOwner().getUserName(), debitOwnerUsername);
//            Assertions.assertEquals(payment.getAmount(), amount);
//            Assertions.assertEquals(payment.getCurrency().getCode(), "EUR");
//            Assertions.assertEquals(payment.getStatus(), EPaymentStatus.CREATED);
//        });
//
//        assertStatus(responseDto.getNumber(), EPaymentStatus.CREATED);
//        return responseDto.getNumber();
//    }
//
//    private void internalTestRepair(String paymentNumber, Long amount, String debitOwnerUsername,
//                                    String debitAccountNumber, String creditAccountNumber) {
//        // repair
//        PaymentRequestDto paymentRequestDto = this.internalCreatePaymentRequestDto(amount, debitAccountNumber, creditAccountNumber);
//        paymentRequestDto.setNumber(paymentNumber);
//
//        ResponseEntity<?> responseEntity = paymentController.repair(debitOwnerUsername, paymentNumber, paymentRequestDto);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        log.info("passed testRepair test");
//
//        assertStatus(paymentNumber, EPaymentStatus.REPAIRED);
//    }
//
//    private void internalTestNotApproveBySame(String paymentNumber) {
//        // approve by same
//        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
//        // approve by debit owner
//        ResponseEntity<?> responseEntity = paymentController.approve(
//                payment.getDebitAccount().getOwner().getUserName(), paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
//        // approve by credit owner
//        ResponseEntity<?> responseEntity2 = paymentController.approve(
//                payment.getCreditAccount().getOwner().getUserName(), paymentNumber);
//        Assertions.assertSame(responseEntity2.getStatusCode(), HttpStatus.BAD_REQUEST);
//        log.info("passed testNotApproveBySame test");
//    }
//
//    private void internalTestNotApproveByNoRightsUser(String paymentNumber) {
//        // approve by no rights user
//        ResponseEntity<?> responseEntity = paymentController.approve("dumb1", paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
//        log.info("passed testNotApproveBySame test");
//    }
//
//    private void internalTestApproveByOther(String paymentNumber) {
//        // approve by other
//        ResponseEntity<?> responseEntity = paymentController.approve("a2", paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        log.info("passed testApproveByOther test");
//    }
//
//    private void internalTestNotRejectByNoRightsUser(String paymentNumber) {
//        // reject by no rights user
//        ResponseEntity<?> responseEntity = paymentController.reject("dumb1", paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
//        log.info("passed testNotRejectByNoRightsUser test");
//    }
//
//    private void internalTestReject(String paymentNumber, String rejectorUsername) {
//        // reject by other
//        ResponseEntity<?> responseEntity = paymentController.reject(rejectorUsername, paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//
//        assertStatus(paymentNumber, List.of(EPaymentStatus.CANCELLED, EPaymentStatus.IN_REPAIR));
//        log.info("passed testRejectByOther test");
//    }
//
//    private void internalTestCancel(String paymentNumber) {
//        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
//        ResponseEntity<?> responseEntity =
//                paymentController.cancel(payment.getDebitAccount().getOwner().getUserName(), paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//
//        assertStatus(paymentNumber, EPaymentStatus.CANCELLED);
//        log.info("passed testCancelByOther test");
//    }
//
//    private void assertStatus(String paymentNumber, EPaymentStatus status) {
//        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
//        Assertions.assertEquals(payment.getStatus(), status);
//    }
//
//    private void assertStatus(String paymentNumber, List<EPaymentStatus> statuses) {
//        Payment payment = paymentRepository.findByNumber(paymentNumber).orElseThrow();
//        Assertions.assertTrue(statuses.contains(payment.getStatus()));
//    }
//
//    private void internalTestVerify(String paymentNumber, String verifierUsername) {
//        // verify by other
//        ResponseEntity<?> responseEntity = paymentController.verify(verifierUsername, paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        log.info("passed testVerifyByOther test");
//    }
//
//    private void testNotVerifyByNoRightsUser(String paymentNumber) {
//        // verify by no rights user
//        ResponseEntity<?> responseEntity = paymentController.verify("dumb1", paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
//        log.info("passed testNotVerifyByNoRightsUser test");
//    }
//
//    private void internalTestAuthorize(String paymentNumber, String authorizerUsername) {
//        // authorize by other
//        ResponseEntity<?> responseEntity = paymentController.authorize(authorizerUsername, paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        log.info("passed testAuthorizeByOther test");
//    }
//
//    private void testNotAuthorizeByNoRightsUser(String paymentNumber) {
//        // authorize by no rights user
//        ResponseEntity<?> responseEntity = paymentController.authorize("dumb1", paymentNumber);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
//        log.info("passed testNotAuthorizeByNoRightsUser test");
//    }
//
//    private void internalTestNotAndClearResult(String paymentNumber) {
//        // check result
//        if(paymentRepository.findByNumber(paymentNumber).isPresent()) {
//            Assertions.fail("Payment should not exist.");
//        }
//        // clear result
//    }
//}
