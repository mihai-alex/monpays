package com.monpays.services.interfaces._generic;

import com.monpays.dtos.user.*;
import com.monpays.services.exception_handling.FirstSignInException;

import javax.naming.AuthenticationException;

public interface IAuthenticationService {
    UserAuthenticationResponse signIn(UserSignInDto userSignInDto) throws AuthenticationException, FirstSignInException;
    public UserAuthenticationResponse getFirstSignInAuthenticationResponse(UserSignInDto userSignInDto);
    String signUp(UserSignUpDto userSignUpDto);
    void signOut(String username);
    String changePassword(String username, UserChangePasswordDto userChangePasswordDto) throws AuthenticationException;

    UserAuthenticationResponse verifyCode(UserVerificationRequest verificationRequest);
}
