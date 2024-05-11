package com.monpays.services.implementations._generic;

import com.monpays.dtos.user.*;
import com.monpays.entities.profile.Profile;
import com.monpays.entities.user.User;
import com.monpays.entities.user.enums.EUserStatus;
import com.monpays.mappers.user.UserMapper;
import com.monpays.persistence.repositories.profile.IProfileRepository;
import com.monpays.persistence.repositories.user.IUserRepository;
import com.monpays.services.exception_handling.FirstSignInException;
import com.monpays.services.exception_handling.UserBlockedException;
import com.monpays.services.implementations._generic.stateful_services.AuthenticationRecord;
import com.monpays.services.implementations.tfa.TwoFactorAuthentication;
import com.monpays.services.interfaces._generic.IAuthenticationService;
import com.monpays.services.interfaces._generic.IUserActivityService;
import com.monpays.services.interfaces.user.IUserService;
import com.monpays.utils.JwtUtils;
import com.monpays.utils.PasswordUtils;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.util.Optional;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Autowired
    IUserRepository userRepository;
    @Autowired
    IUserService userService;
    @Autowired
    IProfileRepository profileRepository;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    private AuthenticationRecord authenticationRecord;

    @Autowired
    private TwoFactorAuthentication tfaService;

    @Autowired
    private IUserActivityService userActivityService;

    @Override
    public UserAuthenticationResponse signIn(UserSignInDto userSignInDto) throws AuthenticationException, FirstSignInException {
        User user = userRepository.findByUserName(userSignInDto.getUserName()).orElseThrow();

        userActivityService.add(user, "sign_in", "AuthenticationController");

        if (user.getStatus() == EUserStatus.REMOVED) {
            throw new UserBlockedException("Your account is removed!");
        }

        if (user.getStatus() == EUserStatus.BLOCKED) {
            throw new UserBlockedException("Your account is blocked!");
        }

        if (!bCryptPasswordEncoder.matches(userSignInDto.getPassword(), user.getPassword())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
            if (user.getFailedLoginAttempts() >= PasswordUtils.MAX_FAILED_LOGIN_ATTEMPTS) {
                // TODO: if exists pending, delete it and block the user (actor - system)
                userService.blockBySystem("system", user.getUserName());
                user = userRepository.findByUserName(userSignInDto.getUserName()).orElseThrow();
            }

            try {
                userRepository.save(user);
            } catch (OptimisticLockingFailureException e) {
                // Handle optimistic locking failure - versioning
                throw new RuntimeException("Concurrent update detected. Please try again.");
            }

            throw new AuthenticationException("Wrong password, sir!");
        }

        // Reset failed login attempts on successful login
        user.setFailedLoginAttempts(0);
        try {
            userRepository.save(user);
        } catch (OptimisticLockingFailureException e) {
            // Handle optimistic locking failure - versioning
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        if(user.getIsFirstLogin()) {
            throw new FirstSignInException("This is the first sign in!");
        }

        // MFA:
        if (user.isMfaEnabled()) {
            return UserAuthenticationResponse.builder()
                    .accessToken("")
                    .refreshToken("")
                    .mfaEnabled(true)
                    .build();
        }

        return UserAuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(user))
                .refreshToken("")
                .mfaEnabled(false)
                .build();

    }

    @Override
    public UserAuthenticationResponse getFirstSignInAuthenticationResponse(UserSignInDto userSignInDto) {
        User user = userRepository.findByUserName(userSignInDto.getUserName()).orElseThrow();

        userActivityService.add(user, "sign_in_first_authentication", "AuthenticationController");

        if (user.isMfaEnabled()) {
            return UserAuthenticationResponse.builder()
                    .secretImageUri(tfaService.generateQrCodeImageUri(user.getMfaSecret(), user.getUserName()))
                    .accessToken("")
                    .refreshToken("")
                    .mfaEnabled(true)
                    .build();
        }

        return UserAuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(user))
                .refreshToken("")
                .mfaEnabled(false)
                .build();
    }

    @Override
    public String signUp(UserSignUpDto userSignUpDto) {     // little hack to pass approval
        Profile profile = profileRepository.findByName(userSignUpDto.getProfileName()).orElseThrow();
        Optional<User> optionalUser = userRepository.findByUserName(userSignUpDto.getUserName());

        userActivityService.add(null, "sign_up", "AuthenticationController");

        if(optionalUser.isPresent()) {
            throw new ServiceException("The username is already used.");
        }

        if (userSignUpDto.getPassword() == null || !PasswordUtils.validatePassword(userSignUpDto.getPassword())) {
            throw new ServiceException("Invalid password!");
        }

        String hashedPassword = bCryptPasswordEncoder.encode(userSignUpDto.getPassword());

        User user = userMapper.fromUserSignUpDto(userSignUpDto, hashedPassword, profile);
        user.setStatus(EUserStatus.ACTIVE);                 // little hack to pass approval
        user.setIsFirstLogin(true);

        // MFA:
        if (user.isMfaEnabled()) {
            user.setMfaSecret(tfaService.generateNewSecret());
        }

        try {
            userRepository.save(user);
        } catch (OptimisticLockingFailureException e) {
            // Handle optimistic locking failure - versioning
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }

        return jwtUtils.generateToken(user);
    }

    @Override
    public void signOut(String username) {
        User user = userRepository.findByUserName(username).orElse(null);
        userActivityService.add(user, "sign_out", "AuthenticationController");

        authenticationRecord.setNewAction(username); // TODO: LEAVE THIS UNCOMMENTED AFTER BEING DONE TESTING WITH OWASP ZAP
    }

    @Override
    public String changePassword(String username, UserChangePasswordDto userChangePasswordDto) throws AuthenticationException {
        User user = userRepository.findByUserName(username).orElseThrow();

        userActivityService.add(user, "change_password", "AuthenticationController");

        if(user.getStatus() != EUserStatus.ACTIVE) {
            throw new ServiceException("Your account is not active, sir!");
        }

        if (userChangePasswordDto.getNewPassword() == null ||
                !bCryptPasswordEncoder.matches(userChangePasswordDto.getOldPassword(), user.getPassword())) {
            throw new ServiceException("Invalid password!");
        }

        String oldPassword = userChangePasswordDto.getOldPassword();
        if (!bCryptPasswordEncoder.matches(oldPassword, user.getPassword())) {
            throw new AuthenticationException("Wrong password, sir!");
        }

        String new_hashed_password = bCryptPasswordEncoder.encode(userChangePasswordDto.getNewPassword());

        try {
            user.setPassword(new_hashed_password);
            user.setIsFirstLogin(false);
            userRepository.save(user);
        } catch (OptimisticLockingFailureException e) {
            // Handle optimistic locking failure - versioning
            throw new RuntimeException("Concurrent update detected. Please try again.");
        }
        return jwtUtils.generateToken(user);
    }

    @Override
    public UserAuthenticationResponse verifyCode(UserVerificationRequest verificationRequest) {
        User user = userRepository.findByUserName(verificationRequest.getUserName()).orElseThrow();

        userActivityService.add(user, "verify_code", "AuthenticationController");

        if (!tfaService.isOtpValid(user.getMfaSecret(), verificationRequest.getCode())) {
            throw new ServiceException("Code is not correct!");
        }

        return UserAuthenticationResponse.builder()
                .accessToken(jwtUtils.generateToken(user))
                .refreshToken("")
                .mfaEnabled(user.isMfaEnabled())
                .build();
    }
}
