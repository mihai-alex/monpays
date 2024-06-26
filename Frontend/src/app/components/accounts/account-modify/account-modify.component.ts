import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ECurrencyType } from 'src/app/constants/enums/e-currency-type';
import { Account } from 'src/app/models/account';
import { AccountService } from 'src/app/services/account.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-account-modify',
  templateUrl: './account-modify.component.html',
  styleUrls: ['./account-modify.component.scss'],
})
export class AccountModifyComponent implements OnInit {
  accountForm!: FormGroup;
  account: Account = new Account();
  currencyTypes = Object.values(ECurrencyType);
  operations!: Observable<EOperationType[]>;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private accountService: AccountService,
    private snackBar: MatSnackBar,
    private operationService: OperationService
  ) {}

  ngOnInit(): void {
    this.initForm(); // Initialize the form first

    this.operations = this.operationService.getOperations('account');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.MODIFY).subscribe((canModify) => {
        if (!canList || !canModify) {
          this.router.navigate(['/forbidden']);
        } else {
          this.loadAccount(); // Load the account data after the form initialization
        }
      });
    });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  initForm() {
    this.accountForm = this.formBuilder.group({
      owner: ['', Validators.required],
      currency: ['', Validators.required],
      name: ['', Validators.required],
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
      const formValue = this.accountForm.value;

      // Update the account properties
      this.account.owner = formValue.owner;
      this.account.currency = formValue.currency;
      this.account.name = formValue.name;
      this.account.transactionLimit = formValue.transactionLimit;

      this.accountService
        .modifyAccount(this.account.accountNumber, this.account)
        .subscribe(
          () => {
            this.goToAccountList();
          },
          (error) => this.handleAccountActionError(error)
        );
    } else {
      this.snackBar.open('Please fill out the form correctly!', 'Close', {
        duration: 4000,
      });
    }
  }

  goToAccountList() {
    this.router.navigate(['/account-list']);
  }

  onCancel() {
    window.history.back();
  }

  handleAccountActionError(error: any) {
    this.snackBar.open('The account could not be modified!', 'Close', {
      duration: 4000,
    });
    // console.log(error);
  }
}
