import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Payment } from 'src/app/models/payment';
import { PaymentService } from 'src/app/services/payment.service';

@Component({
  selector: 'app-payment-repair',
  templateUrl: './payment-repair.component.html',
  styleUrls: ['./payment-repair.component.scss'],
})
export class PaymentRepairComponent {
  payment: Payment = new Payment();
  paymentNumber: string = '';

  constructor(
    private paymentService: PaymentService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.paymentNumber = this.activatedRoute.snapshot.params['paymentNumber'];
    this.paymentService
      .getPaymentByPaymentNumber(this.paymentNumber, false, false)
      .subscribe(
        (data) => {
          this.payment = data;
        },
        (error) => this.handlePaymentActionError(error)
      );
  }

  onSubmit() {
    this.paymentService
      .repairPayment(this.paymentNumber, this.payment)
      .subscribe(
        (data) => {
          this.goToPaymentList();
        },
        (error) => this.handlePaymentActionError(error)
      );
  }

  goToPaymentList() {
    this.router.navigate(['/payment-list']);
  }

  onCancel() {
    // Navigate the user to the payment-list page when cancel is clicked
    //this.goToPaymentList();
    window.history.back();
  }

  handlePaymentActionError(error: any) {
    this.snackBar.open('Sir, your operation could not be executed!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }
}
