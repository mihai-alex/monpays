import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { UserAuthService } from '../../services/user-auth.service';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserAuthenticationResponse } from 'src/app/models/user-authentication-response';
import { UserVerificationRequest } from 'src/app/models/user-verification-request';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {
  authResponse: UserAuthenticationResponse = {};
  validationCode: string = '';
  isFirstLogin: boolean = false;

  constructor(
    private userService: UserService,
    private userAuthService: UserAuthService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  login(loginForm: NgForm) {
    this.userService.login(loginForm.value).subscribe(
      (response: any) => {
        this.authResponse = response;
        if (response.accessToken) {
          // entering this code block also means that the MFA is NOT enabled
          this.userAuthService.setToken(response.accessToken);
          this.router.navigate(['/user']);
          this.snackBar.open('Login successful!', 'Close', { duration: 7000 });
        } else {
          this.snackBar.open(
            'Your account requires Two Factor Authentication (2FA). ' +
              'Please enter the verification code from the authenticator app.',
            'Close',
            { duration: 7000 }
          );
        }
      },
      (error: any) => {
        if (error.status === 417) {
          this.isFirstLogin = true;

          // Handle 417 status code for first sign-in scenario
          this.authResponse = error.error;

          if (this.authResponse.mfaEnabled) {
            this.snackBar.open(
              'Your account requires Two Factor Authentication (2FA). ' +
                'Please scan the QR code and enter the code from the authenticator app.',
              'Close',
              { duration: 10000 }
            );
          } else {
            this.snackBar.open(
              'Your account was just created. ' +
                'You will also need to change your password.',
              'Close',
              { duration: 7000 }
            );
            this.userAuthService.setToken(
              this.authResponse.accessToken as string
            );
            const username = this.userAuthService.getUsername();
            this.router.navigate([`/change-password/${username}`]); // Redirect to change-password page
          }
        } else if (error.status === 403) {
          this.snackBar.open(
            'The account is either BLOCKED or REMOVED',
            'Close',
            {
              duration: 7000,
            }
          );
        } else {
          this.snackBar.open('Sir, invalid username or password', 'Close', {
            duration: 7000,
          });

          console.log(error);
        }
      }
    );
  }

  verifyTfaFirstLogin(loginForm: NgForm) {
    const username = loginForm.value.userName;
    const verifyRequest: UserVerificationRequest = {
      userName: username,
      code: this.validationCode,
    };

    this.userService.verifyTfaCode(verifyRequest).subscribe({
      next: (response: UserAuthenticationResponse): void => {
        this.snackBar.open(
          'Your account was just created. ' +
            'You will also need to change your password.',
          'Close',
          { duration: 7000 }
        );
        this.userAuthService.setToken(response.accessToken as string);
        this.router.navigate([`/change-password/${username}`]); // Redirect to change-password page
      },
      error: (error: any): void => {
        this.snackBar.open('Sir, invalid verification code', 'Close', {
          duration: 7000,
        });
        console.log(error);
      },
    });
  }

  verifyTfaRegularLogin(loginForm: NgForm) {
    const username = loginForm.value.userName;
    const verifyRequest: UserVerificationRequest = {
      userName: username,
      code: this.validationCode,
    };

    this.userService.verifyTfaCode(verifyRequest).subscribe({
      next: (response: UserAuthenticationResponse): void => {
        this.snackBar.open('Login successful', 'Close', { duration: 5000 });
        this.userAuthService.setToken(response.accessToken as string);
        this.router.navigate(['/user']);
      },
      error: (error: any): void => {
        this.snackBar.open('Sir, invalid verification code', 'Close', {
          duration: 7000,
        });
        console.log(error);
      },
    });
  }
}
