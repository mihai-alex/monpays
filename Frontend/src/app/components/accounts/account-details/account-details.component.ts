import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditEntry } from 'src/app/models/audit-entry';
import { Account } from 'src/app/models/account';
import { AccountHistoryEntry } from 'src/app/models/account-history-entry';
import { AccountService } from 'src/app/services/account.service';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AccountPending } from 'src/app/models/account-pending';

@Component({
  selector: 'app-account-details',
  templateUrl: './account-details.component.html',
  styleUrls: ['./account-details.component.scss'],
})
export class AccountDetailsComponent {
  operations!: Observable<EOperationType[]>;
  account: Account = new Account();
  pendingAccount: AccountPending = new AccountPending();
  accountNumber: string = '';
  accountHistoryDataSource: MatTableDataSource<AccountHistoryEntry> =
    new MatTableDataSource();
  accountAuditDataSource: MatTableDataSource<AuditEntry> =
    new MatTableDataSource();

  @ViewChild('accountHistorySort', { static: false })
  accountHistorySort!: MatSort;
  @ViewChild('accountAuditSort', { static: false }) accountAuditSort!: MatSort;
  @ViewChild('accountHistoryPaginator', { static: false })
  accountHistoryPaginator!: MatPaginator;
  @ViewChild('accountAuditPaginator', { static: false })
  accountAuditPaginator!: MatPaginator;

  displayedColumnsHistory: string[] = [
    'accountNumber',
    'owner',
    'currency',
    'name',
    'transactionLimit',
    'status',
    'accountLockStatus',
  ];
  displayedColumnsAudit: string[] = [
    'userName',
    'executedOperation',
    'classProfileName',
    'timestamp',
  ];

  constructor(
    private accountService: AccountService,
    private operationService: OperationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.accountNumber = this.activatedRoute.snapshot.params['accountNumber'];
    this.operations = this.operationService.getOperations('profile');

    this.accountService
      .getAccountByAccountNumber(this.accountNumber, true, true, true)
      .subscribe(
        (data) => {
          if (!data) {
            this.onRedirectToAccountList();
            return;
          }

          this.account = data;
          this.pendingAccount =
            this.account.pendingEntity || new AccountPending();

          // Populate account history and audit from the fetched account
          this.accountHistoryDataSource.data = this.account.history;
          this.accountAuditDataSource.data = this.account.audit;
        },
        (error: any) => {
          this.handleAccountActionError(error);
        }
      );
  }

  onRedirectToAccountList() {
    this.router.navigate(['/account-list']);
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  ngAfterViewInit() {
    this.accountHistoryDataSource.sort = this.accountHistorySort;
    this.accountHistoryDataSource.paginator = this.accountHistoryPaginator;

    this.accountAuditDataSource.sort = this.accountAuditSort;
    this.accountAuditDataSource.paginator = this.accountAuditPaginator;
  }

  applyAccountHistoryFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.accountHistoryDataSource.filter = filterValue.trim().toLowerCase();
  }

  applyAccountAuditFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.accountAuditDataSource.filter = filterValue.trim().toLowerCase();
  }

  blockAccount(accountNumber: string) {
    this.accountService.blockAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit(); // TODO
      },
      this.handleAccountActionError // TODO: do it like this for all similar stuff
    );
  }

  unblockAccount(accountNumber: string) {
    this.accountService.unblockAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  blockCreditAccount(accountNumber: string) {
    this.accountService.blockCreditAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  blockDebitAccount(accountNumber: string) {
    this.accountService.blockDebitAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  unblockCreditAccount(accountNumber: string) {
    this.accountService.unblockCreditAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error) => this.handleAccountActionError(error)
    );
  }

  unblockDebitAccount(accountNumber: string) {
    this.accountService.unblockDebitAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleAccountActionError(error)
    );
  }

  closeAccount(accountNumber: string) {
    this.accountService.closeAccount(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleAccountActionError(error)
    );
  }

  approveChanges(accountNumber: string) {
    this.accountService.approveChanges(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleAccountActionError(error)
    );
  }

  rejectChanges(accountNumber: string) {
    this.accountService.rejectChanges(accountNumber).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleAccountActionError(error)
    );
  }

  // TODO: implement REPAIR

  onCancel() {
    window.history.back();
  }

  formatTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  handleAccountActionError(error: any) {
    this.snackBar.open('Sir, your operation could not be executed!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }

  repairAccount(accountNumber: string) {
    // redirect to repair page:
    this.router.navigate(['/account-repair', accountNumber]);
  }

  protected readonly EOperationType = EOperationType;
}
