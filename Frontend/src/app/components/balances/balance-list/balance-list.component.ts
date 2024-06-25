import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { OperationService } from '../../../services/operation.service';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Balance } from 'src/app/models/balance';
import { BalanceService } from 'src/app/services/balance.service';

@Component({
  selector: 'app-balance-list',
  templateUrl: './balance-list.component.html',
  styleUrls: ['./balance-list.component.scss'],
})
export class BalanceListComponent implements OnInit {
  operations!: Observable<EOperationType[]>;
  displayedColumns: string[] = [
    'timestamp',
    'accountNumber',
    'availableAmount',
    'pendingAmount',
    'projectedAmount',
    'availableCreditAmount',
    'availableCreditCount',
    'availableDebitAmount',
    'availableDebitCount',
    'pendingCreditAmount',
    'pendingCreditCount',
    'pendingDebitAmount',
    'pendingDebitCount',
  ];
  dataSource!: MatTableDataSource<Balance>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private balanceService: BalanceService,
    private operationService: OperationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.operations = this.operationService.getOperations('balance');

    this.isAllowed(EOperationType.LIST).subscribe((allowed) => {
      if (!allowed) {
        this.router.navigate(['/forbidden']);
      } else {
        this.getBalances();
      }
    });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType)),
      catchError(() => of(false))
    );
  }

  getBalances() {
    this.balanceService.getBalances().subscribe((data) => {
      this.dataSource = new MatTableDataSource<Balance>(data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.dataSource.filterPredicate = this.createFilter();

      // console log these:
      console.log('Balances:', data);
    });
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
        data.timestamp.toLowerCase().indexOf(term) === -1 &&
        data.accountNumber.toLowerCase().indexOf(term) === -1
        // add more if needed
      ) {
        return false;
      }
    }
    return true;
  }

  formatTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  protected readonly EOperationType = EOperationType;
}
