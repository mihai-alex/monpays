import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ECurrencyType } from 'src/app/constants/enums/e-currency-type';
import { Account } from 'src/app/models/account';
import { AccountService } from 'src/app/services/account.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-account-repair',
  templateUrl: './account-repair.component.html',
  styleUrls: ['./account-repair.component.scss'],
})
export class AccountRepairComponent {
  accountForm!: FormGroup;
  account: Account = new Account();
  currencyTypes = Object.values(ECurrencyType);

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private formBuilder: FormBuilder,
    private accountService: AccountService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.initForm();
    this.loadAccount();
  }

  initForm() {
    this.accountForm = this.formBuilder.group({
      owner: [''], // Add owner field to the form
      currency: [''],
      name: [''],
      transactionLimit: [''],
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
    const formValue = this.accountForm.value;

    // Update the account properties
    this.account.owner = formValue.owner;
    this.account.currency = formValue.currency;
    this.account.name = formValue.name;
    this.account.transactionLimit = formValue.transactionLimit;

    this.accountService.createAccount(this.account).subscribe(
      () => {
        this.goToAccountList();
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  goToAccountList() {
    this.router.navigate(['/account-list']);
  }

  onCancel() {
    //this.goToAccountList();
    window.history.back();
  }

  handleAccountActionError(error: any) {
    this.snackBar.open('The account could not be repaired!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }
}
