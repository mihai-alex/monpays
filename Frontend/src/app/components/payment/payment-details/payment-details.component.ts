import { Component, OnInit, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditEntry } from 'src/app/models/audit-entry';
import { Payment } from 'src/app/models/payment';
import { PaymentHistoryEntry } from 'src/app/models/payment-history-entry';
import { PaymentService } from 'src/app/services/payment.service';
import { Observable, throwError, of } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map, catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-payment-details',
  templateUrl: './payment-details.component.html',
  styleUrls: ['./payment-details.component.scss'],
})
export class PaymentDetailsComponent implements OnInit {
  operations!: Observable<EOperationType[]>;
  payment: Payment = new Payment();
  paymentNumber: string = '';
  paymentHistoryDataSource: MatTableDataSource<PaymentHistoryEntry> =
    new MatTableDataSource();
  paymentAuditDataSource: MatTableDataSource<AuditEntry> =
    new MatTableDataSource();

  @ViewChild('paymentHistorySort', { static: false })
  paymentHistorySort!: MatSort;
  @ViewChild('paymentAuditSort', { static: false }) paymentAuditSort!: MatSort;
  @ViewChild('paymentHistoryPaginator', { static: false })
  paymentHistoryPaginator!: MatPaginator;
  @ViewChild('paymentAuditPaginator', { static: false })
  paymentAuditPaginator!: MatPaginator;

  displayedColumnsHistory: string[] = [
    'number',
    'timestamp',
    'currency',
    'amount',
    'debitAccount',
    'creditAccount',
    'description',
    'type',
    'status',
  ];
  displayedColumnsAudit: string[] = [
    'userName',
    'executedOperation',
    'timestamp',
  ];

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

    this.paymentService
      .getPaymentByPaymentNumber(this.paymentNumber, true, true)
      .pipe(
        catchError((error) => {
          if (error.status === 400) {
            return of(null); // Treat 400 error as if data is empty
          }
          return throwError(error);
        })
      )
      .subscribe((data) => {
        if (!data) {
          this.onRedirectToPaymentList();
          return;
        }

        this.payment = data;

        // Populate payment history and audit from the fetched payment
        this.paymentHistoryDataSource.data = this.payment.history;
        this.paymentAuditDataSource.data = this.payment.audit;
      });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  ngAfterViewInit() {
    this.paymentHistoryDataSource.sort = this.paymentHistorySort;
    this.paymentHistoryDataSource.paginator = this.paymentHistoryPaginator;

    this.paymentAuditDataSource.sort = this.paymentAuditSort;
    this.paymentAuditDataSource.paginator = this.paymentAuditPaginator;
  }

  applyPaymentHistoryFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.paymentHistoryDataSource.filter = filterValue.trim().toLowerCase();
  }

  applyPaymentAuditFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.paymentAuditDataSource.filter = filterValue.trim().toLowerCase();
  }

  approve(paymentNumber: string) {
    this.paymentService.approvePayment(paymentNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handlePaymentActionError(error)
    );
  }

  verify(paymentNumber: string) {
    this.paymentService.verifyPayment(paymentNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handlePaymentActionError(error)
    );
  }

  authorize(paymentNumber: string) {
    this.paymentService.authorizePayment(paymentNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handlePaymentActionError(error)
    );
  }

  reject(paymentNumber: string) {
    this.paymentService.reject(paymentNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handlePaymentActionError(error)
    );
  }

  cancel(paymentNumber: string) {
    this.paymentService.cancel(paymentNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handlePaymentActionError(error)
    );
  }

  repair(paymentNumber: string) {
    this.router.navigate(['/payment-repair', paymentNumber]);
  }

  onRedirectToPaymentList() {
    this.router.navigate(['/payment-list']);
  }

  onCancel() {
    window.history.back();
  }

  formatTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  handlePaymentActionError(error: any) {
    this.snackBar.open(
      'The action could not be performed on the payment!',
      'Close',
      {
        duration: 4000,
      }
    );
    // console.log(error);
  }

  protected readonly EOperationType = EOperationType;
}
