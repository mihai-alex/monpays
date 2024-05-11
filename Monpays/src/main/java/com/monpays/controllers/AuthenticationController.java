package com.monpays.controllers;

import com.monpays.dtos.user.*;
import com.monpays.services.exception_handling.FirstSignInException;
import com.monpays.services.exception_handling.UserBlockedException;
import com.monpays.services.interfaces._generic.IAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthenticationController {
    @Autowired
    private IAuthenticationService authenticationService;

    @PostMapping("/sign_in")
    public @ResponseBody ResponseEntity<?> signIn(@RequestBody UserSignInDto userSignInDto) {
        try {
            return new ResponseEntity<>(authenticationService.signIn(userSignInDto), HttpStatus.OK);
        }
        catch (UserBlockedException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
        }
        catch (FirstSignInException e) {
            UserAuthenticationResponse userAuthenticationResponse =
                    this.authenticationService.getFirstSignInAuthenticationResponse(userSignInDto);
            return new ResponseEntity<>(userAuthenticationResponse, HttpStatus.EXPECTATION_FAILED);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyCode(@RequestBody UserVerificationRequest verificationRequest) {
        return ResponseEntity.ok(authenticationService.verifyCode(verificationRequest));
    }


    @PostMapping("/sign_up")
    public @ResponseBody ResponseEntity<?> signUp(@RequestBody UserSignUpDto userSignUpDto) {
        try {
            String jwt = authenticationService.signUp(userSignUpDto);
            return new ResponseEntity<>(jwt, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/sign_out")
    public @ResponseBody ResponseEntity<?> signOut(@RequestAttribute("username") String username) {
        try {
            authenticationService.signOut(username);
            return new ResponseEntity<>(HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{username}/password")
    public @ResponseBody ResponseEntity<?> changePassword(@RequestAttribute("username") String username, @RequestBody UserChangePasswordDto userChangePasswordDto) {
        try {
            return new ResponseEntity<>(authenticationService.changePassword(username, userChangePasswordDto), HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
