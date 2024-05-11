import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditEntry } from 'src/app/models/audit-entry';
import { UserService } from 'src/app/services/user.service';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserPending } from 'src/app/models/user-pending';
import { Payment } from 'src/app/models/payment';
import { PaymentHistoryEntry } from 'src/app/models/payment-history-entry';
import { PaymentService } from 'src/app/services/payment.service';
@Component({
  selector: 'app-payment-details',
  templateUrl: './payment-details.component.html',
  styleUrls: ['./payment-details.component.scss'],
})
export class PaymentDetailsComponent {
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
    this.operations = this.operationService.getOperations('profile');

    this.paymentService
      .getPaymentByPaymentNumber(this.paymentNumber, true, true)
      .subscribe(
        (data) => {
          if (!data) {
            this.onRedirectToPaymentList();
            return;
          }

          this.payment = data;

          // Populate account history and audit from the fetched account
          this.paymentHistoryDataSource.data = this.payment.history;
          this.paymentAuditDataSource.data = this.payment.audit;
        },
        (error: any) => {
          this.handlePaymentActionError(error);
        }
      );
  }

  onRedirectToPaymentList() {
    this.router.navigate(['/payment-list']);
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

  onCancel() {
    window.history.back();
  }

  formatTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  handlePaymentActionError(error: any) {
    this.snackBar.open('Sir, your operation could not be executed!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }

  repair(paymentNumber: string) {
    // redirect to repair page:
    this.router.navigate(['/payment-repair', paymentNumber]);
  }

  protected readonly EOperationType = EOperationType;
}
