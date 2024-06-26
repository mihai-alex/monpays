import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Payment } from 'src/app/models/payment';
import { PaymentService } from 'src/app/services/payment.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-payment-repair',
  templateUrl: './payment-repair.component.html',
  styleUrls: ['./payment-repair.component.scss'],
})
export class PaymentRepairComponent implements OnInit {
  payment: Payment = new Payment();
  paymentNumber: string = '';
  operations!: Observable<EOperationType[]>;

  constructor(
    private paymentService: PaymentService,
    private operationService: OperationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.paymentNumber = this.activatedRoute.snapshot.params['paymentNumber'];
    this.operations = this.operationService.getOperations('payment');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.REPAIR).subscribe((canRepair) => {
        // console.log('canList: ' + canList + ' canRepair: ' + canRepair);

        if (!canList || !canRepair) {
          this.router.navigate(['/forbidden']);
        } else {
          this.paymentService
            .getPaymentByPaymentNumber(this.paymentNumber, false, false)
            .subscribe(
              (data) => {
                this.payment = data;
              },
              (error) => this.handlePaymentActionError(error)
            );
        }
      });
    });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
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
    window.history.back();
  }

  handlePaymentActionError(error: any) {
    this.snackBar.open('The payment could not be repaired!', 'Close', {
      duration: 4000,
    });
    // console.log(error);
  }
}
