import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ECurrencyType } from 'src/app/constants/enums/e-currency-type'; // Import ECurrencyType enum
import { Account } from 'src/app/models/account';
import { AccountService } from 'src/app/services/account.service';
import { UserAuthService } from 'src/app/services/user-auth.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-account-create',
  templateUrl: './account-create.component.html',
  styleUrls: ['./account-create.component.scss'],
})
export class AccountCreateComponent {
  accountForm!: FormGroup;

  owner: string = '';
  account: Account = new Account();
  currencyTypes = Object.values(ECurrencyType); // Use ECurrencyType enum values for dropdown

  constructor(
    private accountService: AccountService,
    private userAuthService: UserAuthService,
    private router: Router,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.owner = this.userAuthService.getUsername();
    this.initForm();
  }

  initForm() {
    this.accountForm = this.formBuilder.group({
      owner: this.owner,
      currency: ['', Validators.required],
      name: ['', Validators.required],
      // make transaction limit positive:
      transactionLimit: ['', [Validators.required, Validators.min(0)]],
    });
  }

  onSubmit() {
    // Get the form values
    const formValue = this.accountForm.value;

    // Set account properties from form values
    this.account.owner = formValue.owner;
    this.account.currency = formValue.currency;
    this.account.name = formValue.name;
    this.account.transactionLimit = formValue.transactionLimit;
    this.createAccount();
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
    //this.goToAccountList();
    window.history.back();
  }

  handleAccountActionError(error: any) {
    this.snackBar.open('The account could not be created', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }
}
