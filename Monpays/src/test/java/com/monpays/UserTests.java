package com.monpays;

import com.monpays.controllers.AuthenticationController;
import com.monpays.controllers.UserController;
import com.monpays.dtos.profile.ProfileResponseDto;
import com.monpays.dtos.user.UserResponseDto;
import com.monpays.dtos.user.UserSignUpDto;
import com.monpays.entities.user.User;
import com.monpays.persistence.repositories.user.IUserHistoryRepository;
import com.monpays.persistence.repositories.user.IUserPendingRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.logging.Logger;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserTests {
    @Autowired
    private AuthenticationController authenticationController;
    @Autowired
    private UserController userController;
    @Autowired
    private IUserRepository userRepository;
    @Autowired
    private IUserPendingRepository userPendingRepository;
    @Autowired
    private IUserHistoryRepository userHistoryRepository;
    private final Logger log = Logger.getLogger(UserTests.class.getName());

//    @Test
//    void testFail() {
//        Assertions.fail();
//    }

    private UserSignUpDto createUserSignUpDto(String newUsername, String newPassword, String newProfileName) {
        UserSignUpDto userSignUpDto = new UserSignUpDto();
        userSignUpDto.setUserName(newUsername);
        userSignUpDto.setPassword(newPassword);
        userSignUpDto.setProfileName(newProfileName);
        userSignUpDto.setFirstName(newUsername);
        userSignUpDto.setLastName(newUsername);
        userSignUpDto.setEmailAddress(newUsername);
        userSignUpDto.setAddress(newUsername);
        userSignUpDto.setPhoneNumber("0123456789");

        return userSignUpDto;
    }

    @Test
    @Order(10)
    void testRepair1() {
        internalTestCreate();
        internalTestNotApproveBySame();
        internalTestNotApproveByNoRightsUser();
        internalTestApproveByOther();

        internalTestAndClearResult();
    }

    @Test
    @Order(20)
    void testRepair2() {
        internalTestCreate();
        internalTestNotRejectBySame();
        internalTestNotRejectByNoRightsUser();
        internalTestRejectByOther();
        internalTestRepair();
        internalTestApproveByOther();

        internalTestAndClearResult();
    }

    @Test
    @Order(30)
    void testRepair3() {
        internalTestCreate();
        internalTestRejectByOther();
        internalTestRepair();
        internalTestRejectByOther();

        internalTestNotAndClearResult();
    }

    private void internalTestCreate() {
        // create
        UserSignUpDto userSignUpDto = this.createUserSignUpDto("test_cu_1",
                "P@ssw0rd!", "dumb customer");

        ResponseEntity<?> responseEntity = userController.create("a1", userSignUpDto);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testCreate test");

        userRepository.findByUserName("test_cu_1").ifPresent(user -> {
            Assertions.assertEquals(user.getUserName(), "test_cu_1");
            Assertions.assertEquals(user.getAddress(), "test_cu_1");
            Assertions.assertEquals(user.getFirstName(), "test_cu_1");
            Assertions.assertEquals(user.getProfile().getName(), "dumb customer");
        });
        userPendingRepository.findByOriginalUserName("test_cu_1").ifPresent(userPending -> {
            Assertions.assertEquals(userPending.getOriginalUser().getUserName(), "test_cu_1");
            Assertions.assertEquals(userPending.getOriginalUser().getUserName(), "test_cu_1");
            Assertions.assertEquals(userPending.getAddress(), "test_cu_1");
            Assertions.assertEquals(userPending.getFirstName(), "test_cu_1");
            Assertions.assertEquals(userPending.getProfileName(), "dumb customer");
            Assertions.assertEquals(userPending.getActorUserName(), "a1");
        });
    }

    private void internalTestRepair() {
        // repair
        UserSignUpDto userRepairDto = this.createUserSignUpDto("test_cu_1",
                null, "dumb customer");

        ResponseEntity<?> responseEntity = userController.create("a1", userRepairDto);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testRepair test");
    }

    private void internalTestNotApproveBySame() {
        // approve by same
        ResponseEntity<?> responseEntity = userController.approve("a1", "test_cu_1");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotApproveBySame test");
    }

    private void internalTestNotApproveByNoRightsUser() {
        // approve by no rights user
        ResponseEntity<?> responseEntity = userController.approve("c1", "test_cu_1");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotApproveBySame test");
    }

    private void internalTestApproveByOther() {
        // approve by other
        ResponseEntity<?> responseEntity = userController.approve("a2", "test_cu_1");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testApproveByOther test");
    }

    private void internalTestNotRejectBySame() {
        // reject by same
        ResponseEntity<?> responseEntity = userController.reject("a1", "test_cu_1");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotRejectBySame test");
    }

    private void internalTestNotRejectByNoRightsUser() {
        // reject by no rights user
        ResponseEntity<?> responseEntity = userController.reject("c1", "test_cu_1");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotRejectByNoRightsUser test");
    }

    private void internalTestRejectByOther() {
        // reject by other
        ResponseEntity<?> responseEntity = userController.reject("a2", "test_cu_1");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testRejectByOther test");
    }

    private void internalTestAndClearResult() {
        // check result
        ResponseEntity<?> responseEntity = userController.getOne("a1", "test_cu_1", false, false, false);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        UserResponseDto userResponseDto = (UserResponseDto) responseEntity.getBody();
        Assertions.assertNotNull(userResponseDto);
        Assertions.assertEquals(userResponseDto.getUserName(), "test_cu_1");
        Assertions.assertEquals(userResponseDto.getFirstName(), "test_cu_1");
        Assertions.assertEquals(userResponseDto.getAddress(), "test_cu_1");
        Assertions.assertEquals(userResponseDto.getProfileName(), "dumb customer");
        log.info("passed testAndClearResult test");

        // clear result
        User user = userRepository.findByUserName("test_cu_1").orElseThrow();

        userHistoryRepository.deleteAll(userHistoryRepository.findAllByUser(user));

        if(userPendingRepository.findByOriginalUserName("test_cu_1").isPresent()) {
            Assertions.fail("profile pending should not exist");
        }

        userRepository.delete(user);
    }

    private void internalTestNotAndClearResult() {
        // check result
        ResponseEntity<?> responseEntity = userController.getOne("a1", "test_cu_1", false, false, false);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        ProfileResponseDto profileResponseDto = (ProfileResponseDto) responseEntity.getBody();
        Assertions.assertNull(profileResponseDto);

        // clear result
    }
}
