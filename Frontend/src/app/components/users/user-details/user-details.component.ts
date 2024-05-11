import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditEntry } from 'src/app/models/audit-entry';
import { User } from 'src/app/models/user';
import { UserHistoryEntry } from 'src/app/models/user-history-entry';
import { UserService } from 'src/app/services/user.service';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserPending } from 'src/app/models/user-pending';

@Component({
  selector: 'app-user-details',
  templateUrl: './user-details.component.html',
  styleUrls: ['./user-details.component.scss'],
})
export class UserDetailsComponent {
  operations!: Observable<EOperationType[]>;
  user: User = new User();
  pendingUser: UserPending = new UserPending();
  userName: string = '';
  userHistoryDataSource: MatTableDataSource<UserHistoryEntry> =
    new MatTableDataSource();
  userAuditDataSource: MatTableDataSource<AuditEntry> =
    new MatTableDataSource();

  @ViewChild('userHistorySort', { static: false })
  userHistorySort!: MatSort;
  @ViewChild('userAuditSort', { static: false }) userAuditSort!: MatSort;
  @ViewChild('userHistoryPaginator', { static: false })
  userHistoryPaginator!: MatPaginator;
  @ViewChild('userAuditPaginator', { static: false })
  userAuditPaginator!: MatPaginator;

  displayedColumnsHistory: string[] = [
    'userName',
    'firstName',
    'lastName',
    'emailAddress',
    'phoneNumber',
    'address',
    'profileName',
    'status',
  ];
  displayedColumnsAudit: string[] = [
    'userName',
    'executedOperation',
    'timestamp',
  ];

  constructor(
    private userService: UserService,
    private operationService: OperationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.userName = this.activatedRoute.snapshot.params['userName'];
    this.operations = this.operationService.getOperations('profile');

    this.userService
      .getUserByUserName(this.userName, true, true, true)
      .subscribe((data) => {
        if (!data) {
          this.onRedirectToUserList();
          return;
        }

        this.user = data;
        this.pendingUser = this.user.pendingEntity || new UserPending();

        // Populate user history and audit from the fetched user
        this.userHistoryDataSource.data = this.user.history;
        this.userAuditDataSource.data = this.user.audit;
      });
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  ngAfterViewInit() {
    this.userHistoryDataSource.sort = this.userHistorySort;
    this.userHistoryDataSource.paginator = this.userHistoryPaginator;

    this.userAuditDataSource.sort = this.userAuditSort;
    this.userAuditDataSource.paginator = this.userAuditPaginator;
  }

  applyUserHistoryFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.userHistoryDataSource.filter = filterValue.trim().toLowerCase();
  }

  applyUserAuditFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.userAuditDataSource.filter = filterValue.trim().toLowerCase();
  }

  blockUser(userName: string) {
    this.userService.blockUser(userName).subscribe(
      (data) => {
        this.ngOnInit(); // TODO
      },
      (error) => this.handleUserActionError(error)
      //this.handleUserActionError // TODO: do it like this for all similar stuff
    );
  }

  unblockUser(userName: string) {
    this.userService.unblockUser(userName).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error) => this.handleUserActionError(error)
    );
  }

  approveChanges(userName: string) {
    this.userService.approveChanges(userName).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleUserActionError(error)
    );
  }

  rejectChanges(userName: string) {
    this.userService.rejectChanges(userName).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleUserActionError(error)
    );
  }

  // TODO: implement REPAIR
  repairUser(userName: string) {
    // redirect to repair page:
    this.router.navigate(['/user-repair', userName]);
  }

  onRedirectToUserList() {
    // this redirects the user to the list of users
    this.router.navigate(['/user-list']);
  }

  onCancel() {
    // this redirects the user to the list of users
    window.history.back();
  }

  formatTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  handleUserActionError(error: any) {
    this.snackBar.open('Sir, your operation could not be executed!', 'Close', {
      duration: 4000,
    });
    console.log(error);
  }

  protected readonly EOperationType = EOperationType;
}
