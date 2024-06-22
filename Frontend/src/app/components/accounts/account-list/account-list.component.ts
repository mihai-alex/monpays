import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { Account } from 'src/app/models/account'; // Update the import path
import { AccountService } from 'src/app/services/account.service';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar'; // Update the import path

@Component({
  selector: 'app-account-list', // Update the selector
  templateUrl: './account-list.component.html', // Update the template URL
  styleUrls: ['./account-list.component.scss'],
})
export class AccountListComponent implements OnInit {
  operations!: Observable<EOperationType[]>;
  displayedColumns: string[] = [
    'accountNumber',
    'owner',
    'currency',
    'name',
    'transactionLimit',
    'status',
    'accountLockStatus',
    'actions',
  ];
  dataSource!: MatTableDataSource<Account>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private accountService: AccountService,
    private operationService: OperationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.getAccounts();
    this.operations = this.operationService.getOperations('profile');
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  getAccounts() {
    this.accountService.getAccounts().subscribe((data) => {
      // Update the service method name
      this.dataSource = new MatTableDataSource<Account>(data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
      this.dataSource.filterPredicate = this.createFilter();
    });
  }

  createAccount(): void {
    this.router.navigate(['/account-create']);
  }

  modifyAccount(accountNumber: string) {
    this.router.navigate(['/account-modify', accountNumber]); // Update the route path
  }

  removeAccount(accountNumber: string) {
    this.accountService.removeAccount(accountNumber).subscribe(
      (data) => {
        // Update the service method name
        this.getAccounts();
      },
      (error) => {
        this.snackBar.open('The account could not be removed!', 'Close', {
          duration: 4000,
        });
        this.getAccounts();
        console.log(error);
      }
    );
  }

  // TODO: fix the following patch methods:

  closeAccount(accountNumber: string) {
    this.accountService.closeAccount(accountNumber).subscribe(
      (data) => {
        this.getAccounts();
      },
      (error) => {
        this.snackBar.open('The account could not be closed!', 'Close', {
          duration: 4000,
        });
        this.getAccounts();
        console.log(error);
      }
    );
  }

  detailsAccount(accountNumber: string) {
    this.router.navigate(['/account-details', accountNumber]); // Update the route path
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
        data.accountNumber.toLowerCase().indexOf(term) === -1 &&
        data.owner.toLowerCase().indexOf(term) === -1 &&
        data.currency.toLowerCase().indexOf(term) === -1 &&
        data.name.toLowerCase().indexOf(term) === -1 &&
        data.transactionLimit.toString().indexOf(term) === -1 &&
        data.status.toLowerCase().indexOf(term) === -1 &&
        data.accountLockStatus.toLowerCase().indexOf(term) === -1
      ) {
        return false;
      }
    }
    return true;
  }

  protected readonly EOperationType = EOperationType;
}
