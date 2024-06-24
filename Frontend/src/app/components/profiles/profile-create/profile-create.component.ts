import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { Router } from '@angular/router';
import { EOperationType } from 'src/app/constants/enums/e-operation-type';
import { EProfileType } from 'src/app/constants/enums/e-profile-type';
import { Operation } from 'src/app/models/operation';
import { Profile } from 'src/app/models/profile';
import { ProfileService } from 'src/app/services/profile.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { OperationService } from '../../../services/operation.service';

@Component({
  selector: 'app-profile-create',
  templateUrl: './profile-create.component.html',
  styleUrls: ['./profile-create.component.scss'],
})
export class ProfileCreateComponent implements OnInit {
  classProfileNames = ['User', 'Profile', 'Account'];

  profileForm!: FormGroup;
  profile: Profile = new Profile();
  profileTypes = Object.values(EProfileType);

  userAvailableOperations: EOperationType[] = [];
  profileAvailableOperations: EOperationType[] = [];
  accountAvailableOperations: EOperationType[] = [];
  balanceAvailableOperations: EOperationType[] = [];
  paymentAvailableOperations: EOperationType[] = [];

  userOperations: EOperationType[] = [];
  profileOperations: EOperationType[] = [];
  accountOperations: EOperationType[] = [];
  balanceOperations: EOperationType[] = [];
  paymentOperations: EOperationType[] = [];

  operations!: Observable<EOperationType[]>;

  constructor(
    private profileService: ProfileService,
    private operationService: OperationService,
    private router: Router,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.operations = this.operationService.getOperations('profile');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.CREATE).subscribe((canCreate) => {
        if (!canList || !canCreate) {
          this.router.navigate(['/forbidden']);
        } else {
          this.fetchProfileNames(); // Fetch profile names when component initializes
        }
      });
    });
  }

  initForm() {
    this.profileForm = this.formBuilder.group({
      name: ['', Validators.required],
      type: [null, Validators.required],
    });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType)),
      catchError(() => of(false))
    );
  }

  onSubmit() {
    // Get the form values
    const formValue = this.profileForm.value;

    // Map selected user operations to Operation entities
    const userOperations: Operation[] = this.userOperations.map(
      (op: EOperationType) => this.createOperation(op, 'User')
    );

    // Map selected profile operations to Operation entities
    const profileOperations: Operation[] = this.profileOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Profile')
    );

    // Map selected account operations to Operation entities
    const accountOperations: Operation[] = this.accountOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Account')
    );

    const balanceOperations: Operation[] = this.balanceOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Balance')
    );

    const paymentOperations: Operation[] = this.paymentOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Payment')
    );

    // Combine the arrays of Operation entities
    this.profile.rights = [
      ...userOperations,
      ...profileOperations,
      ...accountOperations,
      ...balanceOperations,
      ...paymentOperations,
    ];

    // Set profile name and type from form values
    this.profile.name = formValue.name;
    this.profile.type = formValue.type;

    this.createProfile();
  }

  createOperation(
    operation: EOperationType,
    classProfileName: string
  ): Operation {
    const newOperation: Operation = {
      operation,
      groupName: classProfileName,
    };
    return newOperation;
  }

  onUserCheckboxChange(event: MatCheckboxChange, operation: EOperationType) {
    const isChecked = event.checked;
    if (isChecked) {
      this.userOperations.push(operation);
    } else {
      const index = this.userOperations.indexOf(operation);
      if (index !== -1) {
        this.userOperations.splice(index, 1);
      }
    }
  }

  onProfileCheckboxChange(event: MatCheckboxChange, operation: EOperationType) {
    const isChecked = event.checked;
    if (isChecked) {
      this.profileOperations.push(operation);
    } else {
      const index = this.profileOperations.indexOf(operation);
      if (index !== -1) {
        this.profileOperations.splice(index, 1);
      }
    }
  }

  onAccountCheckboxChange(event: MatCheckboxChange, operation: EOperationType) {
    const isChecked = event.checked;
    if (isChecked) {
      this.accountOperations.push(operation);
    } else {
      const index = this.accountOperations.indexOf(operation);
      if (index !== -1) {
        this.accountOperations.splice(index, 1);
      }
    }
  }

  onBalanceCheckboxChange(event: MatCheckboxChange, operation: EOperationType) {
    const isChecked = event.checked;
    if (isChecked) {
      this.balanceOperations.push(operation);
    } else {
      const index = this.balanceOperations.indexOf(operation);
      if (index !== -1) {
        this.balanceOperations.splice(index, 1);
      }
    }
  }

  onPaymentCheckboxChange(event: MatCheckboxChange, operation: EOperationType) {
    const isChecked = event.checked;
    if (isChecked) {
      this.paymentOperations.push(operation);
    } else {
      const index = this.paymentOperations.indexOf(operation);
      if (index !== -1) {
        this.paymentOperations.splice(index, 1);
      }
    }
  }

  createProfile() {
    this.profileService.createProfile(this.profile).subscribe(
      (profile) => {
        this.goToProfileList();
      },
      (error: any) => this.handleProfileActionError(error)
    );
  }

  goToProfileList() {
    this.router.navigate(['/profile-list']);
  }

  onCancel() {
    window.history.back();
  }

  handleProfileActionError(error: any) {
    this.snackBar.open('The profile could not be created!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }

  fetchProfileNames() {
    // Fetch available operations based on the profile type
    const selectedProfileType = this.profileForm?.get('type')?.value;
    if (selectedProfileType) {
      this.fetchAvailableOperations(selectedProfileType);
    }
  }

  fetchAvailableOperations(profileType: EProfileType) {
    this.profileService
      .getAvailableOperationsForProfileType(profileType)
      .subscribe(
        (operations: Operation[]) => {
          this.userAvailableOperations = operations
            .filter((op) => op.groupName === 'User')
            .map((op) => op.operation);
          this.profileAvailableOperations = operations
            .filter((op) => op.groupName === 'Profile')
            .map((op) => op.operation);
          this.accountAvailableOperations = operations
            .filter((op) => op.groupName === 'Account')
            .map((op) => op.operation);
          this.balanceAvailableOperations = operations
            .filter((op) => op.groupName === 'Balance')
            .map((op) => op.operation);
          this.paymentAvailableOperations = operations
            .filter((op) => op.groupName === 'Payment')
            .map((op) => op.operation);
        },
        (error: any) => {
          console.error(`Error fetching ${profileType} operations:`, error);
        }
      );
  }

  onProfileTypeChange() {
    this.userOperations = [];
    this.profileOperations = [];
    this.accountOperations = [];
    this.balanceOperations = [];
    this.paymentOperations = [];

    if (this.profileForm === null) {
      this.userAvailableOperations = [];
      this.profileAvailableOperations = [];
      this.accountAvailableOperations = [];
      this.balanceAvailableOperations = [];
      this.paymentAvailableOperations = [];
      return;
    }

    const selectedProfileType = this.profileForm?.get('type')?.value;

    this.fetchAvailableOperations(selectedProfileType);
  }
}
