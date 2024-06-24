import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { ProfileService } from 'src/app/services/profile.service'; // Import the profile service
import { MatSnackBar } from '@angular/material/snack-bar';
import { Payment } from 'src/app/models/payment';
import { ECurrencyType } from 'src/app/constants/enums/e-currency-type';
import { PaymentService } from 'src/app/services/payment.service';

@Component({
  selector: 'app-payment-create',
  templateUrl: './payment-create.component.html',
  styleUrls: ['./payment-create.component.scss'],
})
export class PaymentCreateComponent {
  paymentForm!: FormGroup; // Use FormGroup for the form
  payment: Payment = new Payment(); // Initialize a new user
  profileNames: string[] = []; // Initialize an array to store profile names
  currencyTypes = Object.values(ECurrencyType); // Use ECurrencyType enum values for dropdown

  constructor(
    private paymentService: PaymentService,
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
    this.paymentForm = this.formBuilder.group({
      currency: ['', Validators.required],
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
    const formValue = this.paymentForm.value;

    this.payment.currency = formValue.currency;
    this.payment.amount = formValue.amount;
    this.payment.debitAccountNumber = formValue.debitAccountNumber;
    this.payment.creditAccountNumber = formValue.creditAccountNumber;
    this.payment.description = formValue.description;

    this.createPayment();
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
