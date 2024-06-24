import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Profile } from 'src/app/models/profile';
import { ProfileService } from 'src/app/services/profile.service';
import { OperationService } from '../../../services/operation.service';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { PaymentService } from 'src/app/services/payment.service';
import { Payment } from 'src/app/models/payment';

@Component({
  selector: 'app-payment-list',
  templateUrl: './payment-list.component.html',
  styleUrls: ['./payment-list.component.scss'],
})
export class PaymentListComponent {
  operations!: Observable<EOperationType[]>;
  displayedColumns: string[] = [
    'number',
    'timestamp',
    'currency',
    'amount',
    'debitAccountNumber',
    'creditAccountNumber',
    'actions',
    // TODO: add more if needed
  ];
  dataSource!: MatTableDataSource<Payment>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private paymentService: PaymentService,
    private operationService: OperationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.getPayments();
    this.operations = this.operationService.getOperations('payment');
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  getPayments() {
    this.paymentService.getPayments().subscribe((data) => {
      this.dataSource = new MatTableDataSource<Payment>(data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.dataSource.filterPredicate = this.createFilter();
    });
  }

  createPayment(): void {
    this.router.navigate(['/payment-create']);
  }

  detailsPayment(paymentNumber: string) {
    this.router.navigate(['/payment-details', paymentNumber]);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  createFilter(): (data: any, filter: string) => boolean {
    const filterFunction = (data: any, filter: string): boolean => {
      const searchTerms = filter.toLowerCase().split(' ');
      return this.checkSearchTermsInData(searchTerms, data);
    };
    return filterFunction;
  }

  checkSearchTermsInData(searchTerms: string[], data: any): boolean {
    for (const term of searchTerms) {
      if (
        data.number.toLowerCase().indexOf(term) === -1 &&
        data.timestamp?.toLowerCase().indexOf(term) === -1 &&
        data.currency.toLowerCase().indexOf(term) === -1 &&
        data.amount.toString().toLowerCase().indexOf(term) === -1 &&
        data.debitAccountNumber.toLowerCase().indexOf(term) === -1 &&
        data.creditAccountNumber.toLowerCase().indexOf(term) === -1
        // TODO: add more if needed
      ) {
        return false;
      }
    }
    return true;
  }

  protected readonly EOperationType = EOperationType;
}
