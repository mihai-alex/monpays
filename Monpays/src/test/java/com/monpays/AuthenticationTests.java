//package com.monpays;
//
//import com.monpays.controllers.AuthenticationController;
//import com.monpays.dtos.user.UserChangePasswordDto;
//import com.monpays.dtos.user.UserSignInDto;
//import com.monpays.dtos.user.UserSignUpDto;
//import com.monpays.utils.JwtUtils;
//import org.junit.jupiter.api.*;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//
//import javax.naming.AuthenticationException;
//import java.sql.Timestamp;
//import java.time.Instant;
//import java.util.Objects;
//import java.util.logging.Logger;
//
//@SpringBootTest
//@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
//public class AuthenticationTests {
//    @Autowired
//    private AuthenticationController authenticationController;
//    @Autowired
//    private JwtUtils jwtUtils;
//    private final Logger log = Logger.getLogger(AuthenticationTests.class.getName());
//
////    @Test
////    void testFail() {
////        Assertions.fail();
////    }
//
//    // creates a token for a username
//    private String createJwt(String userName) {
//        return jwtUtils.generateToken(userName, Timestamp.from(Instant.now()));
//    }
//
//    // tests the token and returns the username
//    private String testJwt(String token) {
//        try {
//            return jwtUtils.validateToken(token);
//        } catch (AuthenticationException e) {
//            Assertions.fail(e.getMessage());
//            return null;
//        }
//    }
//
//    // returns a token
//    private String internalTestSignIn(String username, String password) {
//        UserSignInDto userSignInDto = new UserSignInDto();
//        userSignInDto.setUserName(username);
//        userSignInDto.setPassword(password);
//        ResponseEntity<?> responseEntity = authenticationController.signIn(userSignInDto);
//
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        String token = Objects.requireNonNull(responseEntity.getBody()).toString();
//        String usernameFromJwt = testJwt(token);
//        Assertions.assertEquals(usernameFromJwt, username);
//
//        return token;
//    }
//
//    @Test
//    @Order(10)
//    void testSignUp() {
//        UserSignUpDto userSignUpDto = new UserSignUpDto();
//        userSignUpDto.setUserName("super_admin");
//        userSignUpDto.setPassword("P@ssw0rd!");
//        userSignUpDto.setFirstName("super_admin");
//        userSignUpDto.setLastName("super_admin");
//        userSignUpDto.setEmailAddress("super_admin");
//        userSignUpDto.setAddress("super_admin");
//        userSignUpDto.setPhoneNumber("0123456789");
//        userSignUpDto.setProfileName("super administrator");
//        ResponseEntity<?> responseEntity = authenticationController.signUp(userSignUpDto);
//
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        String userName = testJwt(Objects.requireNonNull(responseEntity.getBody()).toString());
//        Assertions.assertEquals(userName, "super_admin");
//
//        log.info("passed testSignUp test");
//    }
//
//    @Test
//    @Order(20)
//    void testSignIn() {
//        UserSignInDto userSignInDto = new UserSignInDto();
//        userSignInDto.setUserName("super_admin");
//        userSignInDto.setPassword("P@ssw0rd!");
//        ResponseEntity<?> responseEntity = authenticationController.signIn(userSignInDto);
//
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.EXPECTATION_FAILED);
//        String userName = testJwt(Objects.requireNonNull(responseEntity.getBody()).toString());
//        Assertions.assertEquals(userName, "super_admin");
//
//        log.info("passed testSignIn test");
//    }
//
//    @Test
//    @Order(30)
//    void testSignInWrongPassword() {
//        UserSignInDto userSignInDto = new UserSignInDto();
//        userSignInDto.setUserName("super_admin");
//        userSignInDto.setPassword("super_admin1");
//        ResponseEntity<?> responseEntity = authenticationController.signIn(userSignInDto);
//
//        Assertions.assertNotSame(responseEntity.getStatusCode(), HttpStatus.OK);
//
//        log.info("passed testSignInWrongPassword test");
//    }
//
//    @Test
//    @Order(40)
//    void testChangePassword() {
//        final String username = "super_admin";
//        final String oldPassword = "P@ssw0rd!";
//        final String newPassword = "P@ssw0rd!";
//
//        String token = this.createJwt(username);
//
//        UserChangePasswordDto userChangePasswordDto = new UserChangePasswordDto();
//        userChangePasswordDto.setOldPassword(oldPassword);
//        userChangePasswordDto.setNewPassword(newPassword);
//
//        ResponseEntity<?> responseEntity = authenticationController.changePassword(username, userChangePasswordDto);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//
//        token = this.internalTestSignIn(username, newPassword);
//        this.testJwt(token);
//
//        userChangePasswordDto = new UserChangePasswordDto();
//        userChangePasswordDto.setOldPassword(newPassword);
//        userChangePasswordDto.setNewPassword(oldPassword);
//
//        responseEntity = authenticationController.changePassword(username, userChangePasswordDto);
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//
//        token = this.internalTestSignIn(username, oldPassword);
//        this.testJwt(token);
//
//        log.info("passed testChangePassword test");
//    }
//
//    @Test
//    @Order(50)
//    void testSignInAfterFirstChangePassword() {
//        UserSignInDto userSignInDto = new UserSignInDto();
//        userSignInDto.setUserName("super_admin");
//        userSignInDto.setPassword("P@ssw0rd!");
//        ResponseEntity<?> responseEntity = authenticationController.signIn(userSignInDto);
//
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        String userName = testJwt(Objects.requireNonNull(responseEntity.getBody()).toString());
//        Assertions.assertEquals(userName, "super_admin");
//
//        log.info("passed testSignIn test");
//    }
//
//    public String getJwtFullOps() {
//        UserSignUpDto userSignUpDto = new UserSignUpDto();
//        userSignUpDto.setUserName("super_admin");
//        userSignUpDto.setPassword("P@ssw0rd!");
//        userSignUpDto.setFirstName("super_admin");
//        userSignUpDto.setLastName("super_admin");
//        userSignUpDto.setEmailAddress("super_admin");
//        userSignUpDto.setAddress("super_admin");
//        userSignUpDto.setPhoneNumber("0");
//        userSignUpDto.setProfileName("super_admin");
//        ResponseEntity<?> responseEntity = authenticationController.signUp(userSignUpDto);
//
//        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
//        return responseEntity.toString();
//    }
//}
