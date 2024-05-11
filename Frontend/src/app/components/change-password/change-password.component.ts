import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { UserAuthService } from '../../services/user-auth.service';
import { UserService } from '../../services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SidenavComponent } from '../sidenav/sidenav.component';

@Component({
  selector: 'app-change-password',
  templateUrl: './change-password.component.html',
  styleUrls: ['./change-password.component.scss'],
})
export class ChangePasswordComponent {
  constructor(
    private http: HttpClient,
    private router: Router,
    private authService: UserAuthService,
    private userService: UserService,
    private snackBar: MatSnackBar
  ) {}

  changePassword(changePasswordForm: NgForm) {
    const formData = changePasswordForm.value;

    // Validate the new password and its confirmation
    if (formData.newPassword !== formData.confirmNewPassword) {
      console.error('New passwords do not match');
      return;
    }

    // Retrieve the username if needed
    const username = this.authService.getUsername(); // Replace with the correct value

    // Retrieve the token from the local storage
    const token = this.authService.getToken();

    // Create the request body
    const body = {
      oldPassword: formData.oldPassword,
      newPassword: formData.newPassword,
    };

    this.userService.changePassword(body, username).subscribe(
      (response: any) => {
        const newToken = response;
        this.authService.setToken(newToken);
        this.snackBar.open('Password changed successfully.', 'Close', {
          duration: 4000,
        });
        this.router.navigateByUrl('/user').then(() => {
          window.location.reload();
        });
      },
      (error: any) => {
        this.snackBar.open('Sir, your input was not valid.', 'Close', {
          duration: 7000,
        });
        console.error(
          'Error changing password',
          error.message ? error.message : error
        );
      }
    );
  }
}
