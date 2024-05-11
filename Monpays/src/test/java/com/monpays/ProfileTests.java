package com.monpays;

import com.monpays.controllers.ProfileController;
import com.monpays.dtos.profile.ProfileRequestDto;
import com.monpays.dtos.profile.ProfileResponseDto;
import com.monpays.entities.profile.enums.EProfileType;
import com.monpays.entities.profile.Profile;
import com.monpays.persistence.repositories.profile.IProfileHistoryRepository;
import com.monpays.persistence.repositories.profile.IProfilePendingRepository;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.logging.Logger;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ProfileTests {
    @Autowired
    private ProfileController profileController;
    @Autowired
    private IProfileRepository profileRepository;
    @Autowired
    private IProfilePendingRepository profilePendingRepository;
    @Autowired
    private IProfileHistoryRepository profileHistoryRepository;
    private final Logger log = Logger.getLogger(ProfileTests.class.getName());

//    @Test
//    void testFail() {
//        Assertions.fail();
//    }

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
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setName("test_profile");
        profileRequestDto.setType(EProfileType.ADMINISTRATOR);
        profileRequestDto.setRights(new ArrayList<>());

        ResponseEntity<?> responseEntity = profileController.create("a1", profileRequestDto);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testCreate test");

        profileRepository.findByName("test_profile").ifPresent(profile -> {
            Assertions.assertEquals(profile.getName(), "test_profile");
            Assertions.assertEquals(profile.getType(), EProfileType.ADMINISTRATOR);
            Assertions.assertEquals(profile.getRights().size(), 0);
        });
        profilePendingRepository.findByOriginalProfileName("test_profile").ifPresent(profilePending -> {
            Assertions.assertEquals(profilePending.getOriginalProfile().getName(), "test_profile");
            Assertions.assertEquals(profilePending.getType(), EProfileType.ADMINISTRATOR);
            Assertions.assertEquals(profilePending.getRights().size(), 0);
            Assertions.assertEquals(profilePending.getUsername(), "a1");
        });
    }

    private void internalTestRepair() {
        // repair
        ProfileRequestDto profileRequestDto = new ProfileRequestDto();
        profileRequestDto.setName("test_profile");
        profileRequestDto.setType(EProfileType.ADMINISTRATOR);
        profileRequestDto.setRights(new ArrayList<>());

        ResponseEntity<?> responseEntity = profileController.create("a1", profileRequestDto);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testRepair test");
    }

    private void internalTestNotApproveBySame() {
        // approve by same
        ResponseEntity<?> responseEntity = profileController.approve("a1", "test_profile");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotApproveBySame test");
    }

    private void internalTestNotApproveByNoRightsUser() {
        // approve by no rights user
        ResponseEntity<?> responseEntity = profileController.approve("c1", "test_profile");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotApproveBySame test");
    }

    private void internalTestApproveByOther() {
        // approve by other
        ResponseEntity<?> responseEntity = profileController.approve("a2", "test_profile");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testApproveByOther test");
    }

    private void internalTestNotRejectBySame() {
        // reject by same
        ResponseEntity<?> responseEntity = profileController.reject("a1", "test_profile");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotRejectBySame test");
    }

    private void internalTestNotRejectByNoRightsUser() {
        // reject by no rights user
        ResponseEntity<?> responseEntity = profileController.reject("c1", "test_profile");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.BAD_REQUEST);
        log.info("passed testNotRejectByNoRightsUser test");
    }

    private void internalTestRejectByOther() {
        // reject by other
        ResponseEntity<?> responseEntity = profileController.reject("a2", "test_profile");
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        log.info("passed testRejectByOther test");
    }

    private void internalTestAndClearResult() {
        // check result
        ResponseEntity<?> responseEntity = profileController.getOne("a1", "test_profile", false, false, false);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        ProfileResponseDto profileResponseDto = (ProfileResponseDto) responseEntity.getBody();
        Assertions.assertNotNull(profileResponseDto);
        Assertions.assertEquals(profileResponseDto.getName(), "test_profile");
        Assertions.assertEquals(profileResponseDto.getType(), EProfileType.ADMINISTRATOR);
        Assertions.assertEquals(profileResponseDto.getRights().size(), 0);
        log.info("passed testAndClearResult test");

        // clear result
        Profile profile = profileRepository.findByName("test_profile").orElseThrow();

        profileHistoryRepository.deleteAll(profileHistoryRepository.findAllByProfile(profile));

        if(profilePendingRepository.findByOriginalProfileName("test_profile").isPresent()) {
            Assertions.fail("profile pending should not exist");
        }

        profileRepository.delete(profile);
    }

    private void internalTestNotAndClearResult() {
        // check result
        ResponseEntity<?> responseEntity = profileController.getOne("a1", "test_profile", false, false, false);
        Assertions.assertSame(responseEntity.getStatusCode(), HttpStatus.OK);
        ProfileResponseDto profileResponseDto = (ProfileResponseDto) responseEntity.getBody();
        Assertions.assertNull(profileResponseDto);

        // clear result
    }
}
