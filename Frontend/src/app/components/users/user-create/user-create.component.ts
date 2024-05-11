import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { ProfileService } from 'src/app/services/profile.service'; // Import the profile service
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-create',
  templateUrl: './user-create.component.html',
  styleUrls: ['./user-create.component.scss'],
})
export class UserCreateComponent implements OnInit {
  userForm!: FormGroup; // Use FormGroup for the form
  user: User = new User(); // Initialize a new user
  passwordConfirmation: string = ''; // Password confirmation property
  profileNames: string[] = []; // Initialize an array to store profile names

  constructor(
    private userService: UserService,
    private profileService: ProfileService, // Inject the profile service
    private router: Router,
    private formBuilder: FormBuilder, // Inject FormBuilder
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.fetchProfileNames(); // Fetch profile names when component initializes
  }

  initForm() {
    this.userForm = this.formBuilder.group({
      userName: ['', Validators.required],
      password: ['', Validators.required],
      passwordConfirmation: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      emailAddress: ['', Validators.required],
      phoneNumber: ['', Validators.required],
      address: ['', Validators.required],
      profileName: ['', Validators.required],
      mfaEnabled: [false], // Add the mfaEnabled form control here
    });
  }

  fetchProfileNames() {
    this.profileService.getProfileNames().subscribe(
      (profileNames: string[]) => {
        this.profileNames = profileNames;
      },
      (error: any) => {
        console.error('Error fetching profile names:', error);
      }
    );
  }

  onSubmit() {
    if (this.userForm.valid && this.passwordsMatch()) {
      //this.account.currency = formValue.currency;
      this.user.userName = this.userForm.value.userName;
      this.user.password = this.userForm.value.password;
      this.user.firstName = this.userForm.value.firstName;
      this.user.lastName = this.userForm.value.lastName;
      this.user.emailAddress = this.userForm.value.emailAddress;
      this.user.phoneNumber = this.userForm.value.phoneNumber;
      this.user.address = this.userForm.value.address;
      this.user.profileName = this.userForm.value.profileName; // Set the profile name
      this.user.mfaEnabled = this.userForm.value.mfaEnabled;

      this.createUser();
    } else {
      this.snackBar.open(
        'Sir, the entered data is incorrect or the passwords do not match!',
        'Close',
        { duration: 4000 }
      );
    }
  }

  passwordsMatch(): boolean {
    return (
      this.userForm.value.password === this.userForm.value.passwordConfirmation
    );
  }

  onCancel() {
    window.history.back();
  }

  createUser() {
    // TODO: Call the backend to create the user
    this.userService.createUser(this.user).subscribe(
      (user) => {
        this.goToUserList();
      },
      (error) => this.handleUserActionError(error)
    );
  }

  goToUserList() {
    this.router.navigate(['/user-list']);
  }

  handleUserActionError(error: any) {
    this.snackBar.open('Sir, your operation could not be executed!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }
}
