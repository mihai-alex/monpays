import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ECurrencyType } from 'src/app/constants/enums/e-currency-type';
import { Account } from 'src/app/models/account';
import { AccountService } from 'src/app/services/account.service';
import { UserAuthService } from 'src/app/services/user-auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { EOperationType } from 'src/app/constants/enums/e-operation-type';
import { OperationService } from 'src/app/services/operation.service';
import { map, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Component({
  selector: 'app-account-repair',
  templateUrl: './account-repair.component.html',
  styleUrls: ['./account-repair.component.scss'],
})
export class AccountRepairComponent implements OnInit {
  accountForm!: FormGroup;
  owner: string = '';
  account: Account = new Account();
  currencyTypes = Object.values(ECurrencyType);
  operations!: Observable<EOperationType[]>;

  constructor(
    private accountService: AccountService,
    private userAuthService: UserAuthService,
    private operationService: OperationService,
    private router: Router,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.owner = this.userAuthService.getUsername();
    this.operations = this.operationService.getOperations('account');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.REPAIR).subscribe((canRepair) => {
        if (!canList || !canRepair) {
          this.router.navigate(['/forbidden']);
        } else {
          this.initForm();
          this.loadAccount();
        }
      });
    });

    // Ensure the form is initialized before attempting to use it
    this.initForm();
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType)),
      catchError(() => of(false))
    );
  }

  initForm() {
    this.accountForm = this.formBuilder.group({
      owner: [this.owner],
      currency: ['', Validators.required],
      name: ['', Validators.required],
      // make transaction limit positive:
      transactionLimit: ['', [Validators.required, Validators.min(0)]],
    });
  }

  loadAccount() {
    const accountNumber = this.route.snapshot.params['accountNumber'];
    this.accountService.getAccountByAccountNumber(accountNumber).subscribe(
      (account) => {
        if (!account) {
          this.onRedirectToAccountList();
          return;
        }

        this.account = account;
        this.accountForm.patchValue({
          owner: this.account.owner,
          currency: this.account.currency,
          name: this.account.name,
          transactionLimit: this.account.transactionLimit,
        });
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  onRedirectToAccountList() {
    this.router.navigate(['/account-list']);
  }

  onSubmit() {
    if (this.accountForm.valid) {
      // Get the form values
      const formValue = this.accountForm.value;

      // Set account properties from form values
      this.account.owner = formValue.owner;
      this.account.currency = formValue.currency;
      this.account.name = formValue.name;
      this.account.transactionLimit = formValue.transactionLimit;
      this.createAccount();
    } else {
      this.snackBar.open('Please fill out the form correctly!', 'Close', {
        duration: 4000,
      });
    }
  }

  createAccount() {
    this.accountService.createAccount(this.account).subscribe(
      (account) => {
        this.goToAccountList();
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  goToAccountList() {
    this.router.navigate(['/account-list']);
  }

  onCancel() {
    window.history.back();
  }

  handleAccountActionError(error: any) {
    this.snackBar.open('The account could not be repaired', 'Close', {
      duration: 4000,
    });
    // console.log(error);
  }
}
