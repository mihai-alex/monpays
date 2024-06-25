import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfileService } from 'src/app/services/profile.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Payment } from 'src/app/models/payment';
import { PaymentService } from 'src/app/services/payment.service';
import { AccountService } from 'src/app/services/account.service';
import { Observable, throwError } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map, catchError } from 'rxjs/operators';

@Component({
  selector: 'app-payment-create',
  templateUrl: './payment-create.component.html',
  styleUrls: ['./payment-create.component.scss'],
})
export class PaymentCreateComponent implements OnInit {
  paymentForm!: FormGroup;
  payment: Payment = new Payment();
  profileNames: string[] = [];
  operations!: Observable<EOperationType[]>;

  constructor(
    private paymentService: PaymentService,
    private profileService: ProfileService,
    private accountService: AccountService,
    private operationService: OperationService,
    private router: Router,
    private formBuilder: FormBuilder,
    private snackBar: MatSnackBar
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.operations = this.operationService.getOperations('payment');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.CREATE).subscribe((canCreate) => {
        if (!canList || !canCreate) {
          this.router.navigate(['/forbidden']);
        } else {
          this.fetchProfileNames();
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
    this.paymentForm = this.formBuilder.group({
      amount: ['', Validators.required],
      debitAccountNumber: ['', Validators.required],
      creditAccountNumber: ['', Validators.required],
      description: ['', Validators.required],
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
    if (this.paymentForm.valid) {
      const formValue = this.paymentForm.value;

      this.payment.amount = formValue.amount;
      this.payment.debitAccountNumber = formValue.debitAccountNumber;
      this.payment.creditAccountNumber = formValue.creditAccountNumber;
      this.payment.description = formValue.description;

      // Fetch the currency of the credit account before creating the payment
      this.accountService
        .getAccountByAccountNumber(this.payment.creditAccountNumber)
        .pipe(
          catchError((error) => {
            this.snackBar.open('Error fetching account currency!', 'Close', {
              duration: 4000,
            });
            return throwError(error);
          })
        )
        .subscribe(
          (account) => {
            this.payment.currency = account.currency; // Set the currency from the credit account
            this.createPayment();
          },
          (error) => {
            console.log(error);
          }
        );
    } else {
      this.snackBar.open('Please fill out the form correctly!', 'Close', {
        duration: 4000,
      });
    }
  }

  onCancel() {
    window.history.back();
  }

  createPayment() {
    this.paymentService.createPayment(this.payment).subscribe(
      (payment) => {
        this.goToPaymentList();
      },
      (error) => this.handlePaymentActionError(error)
    );
  }

  goToPaymentList() {
    this.router.navigate(['/payment-list']);
  }

  handlePaymentActionError(error: any) {
    this.snackBar.open('The payment could not be created!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }
}
