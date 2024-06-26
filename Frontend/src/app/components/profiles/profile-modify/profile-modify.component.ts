import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatCheckboxChange } from '@angular/material/checkbox';
import { EOperationType } from 'src/app/constants/enums/e-operation-type';
import { EProfileType } from 'src/app/constants/enums/e-profile-type';
import { Operation } from 'src/app/models/operation';
import { Profile } from 'src/app/models/profile';
import { ProfileService } from 'src/app/services/profile.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable, of, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { OperationService } from 'src/app/services/operation.service';

@Component({
  selector: 'app-profile-modify',
  templateUrl: './profile-modify.component.html',
  styleUrls: ['./profile-modify.component.scss'],
})
export class ProfileModifyComponent implements OnInit {
  profile: Profile = new Profile();
  profileName: string = '';

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
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar,
    private operationService: OperationService
  ) {}

  ngOnInit(): void {
    this.profileName = this.activatedRoute.snapshot.params['name'];
    this.operations = this.operationService.getOperations('profile');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.MODIFY).subscribe((canModify) => {
        if (!canList || !canModify) {
          this.router.navigate(['/forbidden']);
        } else {
          this.profileService
            .getProfileByName(this.profileName)
            .pipe(
              catchError((error) => {
                if (error.status === 400) {
                  return of(null); // Treat 400 error as if data is empty
                }
                return throwError(error);
              })
            )
            .subscribe(
              (data) => {
                if (!data) {
                  this.onRedirectToProfileList();
                  return;
                }

                this.profile = data;

                this.userOperations = this.profile.rights
                  .filter((op) => op?.groupName === 'User' && op?.operation)
                  .map((op) => op!.operation);

                this.profileOperations = this.profile.rights
                  .filter((op) => op?.groupName === 'Profile' && op?.operation)
                  .map((op) => op!.operation);

                this.accountOperations = this.profile.rights
                  .filter((op) => op?.groupName === 'Account' && op?.operation)
                  .map((op) => op!.operation);

                this.balanceOperations = this.profile.rights
                  .filter((op) => op?.groupName === 'Balance' && op?.operation)
                  .map((op) => op!.operation);

                this.paymentOperations = this.profile.rights
                  .filter((op) => op?.groupName === 'Payment' && op?.operation)
                  .map((op) => op!.operation);

                // Fetch available operations based on profile type
                this.fetchAvailableOperations(this.profile.type);
              },
              (error) => this.handleProfileActionError(error)
            );
        }
      });
    });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType)),
      catchError(() => of(false))
    );
  }

  onRedirectToProfileList() {
    this.router.navigate(['/profile-list']);
  }

  onSubmit() {
    const userOperations: Operation[] = this.userOperations.map(
      (op: EOperationType) => this.createOperation(op, 'User')
    );
    const profileOperations: Operation[] = this.profileOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Profile')
    );
    const accountOperations: Operation[] = this.accountOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Account')
    );
    const balanceOperations: Operation[] = this.balanceOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Balance')
    );
    const paymentOperations: Operation[] = this.paymentOperations.map(
      (op: EOperationType) => this.createOperation(op, 'Payment')
    );

    this.profile.rights = [
      ...userOperations,
      ...profileOperations,
      ...accountOperations,
      ...balanceOperations,
      ...paymentOperations,
    ];

    this.modifyProfile();
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

  modifyProfile() {
    this.profileService.modifyProfile(this.profileName, this.profile).subscribe(
      (data) => {
        this.goToProfileList();
      },
      (error) => this.handleProfileActionError(error)
    );
  }

  onCancel() {
    window.history.back();
  }

  goToProfileList() {
    this.router.navigate(['/profile-list']);
  }

  handleProfileActionError(error: any) {
    this.snackBar.open('The profile could not be modified!', 'Close', {
      duration: 4000,
    });
    // console.log(error);
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
          // console.error(`Error fetching ${profileType} operations:`, error);
        }
      );
  }

  onProfileTypeChange() {
    const selectedProfileType = this.profile.type;

    // Fetch available operations based on profile type
    this.fetchAvailableOperations(selectedProfileType);
  }
}
