import { Component, ViewChild } from '@angular/core';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { ActivatedRoute, Router } from '@angular/router';
import { AuditEntry } from 'src/app/models/audit-entry';
import { Profile } from 'src/app/models/profile';
import { ProfileHistoryEntry } from 'src/app/models/profile-history-entry';
import { ProfileService } from 'src/app/services/profile.service';
import { OperationService } from '../../../services/operation.service';
import { Observable, of, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { MatSnackBar } from '@angular/material/snack-bar';
import { ProfilePending } from 'src/app/models/profile-pending';
import { EProfileStatus } from 'src/app/constants/enums/e-profile-status';

@Component({
  selector: 'app-profile-details',
  templateUrl: './profile-details.component.html',
  styleUrls: ['./profile-details.component.scss'],
})
export class ProfileDetailsComponent {
  operations!: Observable<EOperationType[]>;
  profile: Profile = new Profile();
  pendingProfile: ProfilePending = new ProfilePending();
  name: string = '';
  profileHistoryDataSource: MatTableDataSource<ProfileHistoryEntry> =
    new MatTableDataSource();
  profileAuditDataSource: MatTableDataSource<AuditEntry> =
    new MatTableDataSource();

  @ViewChild('profileHistorySort', { static: false })
  profileHistorySort!: MatSort;
  @ViewChild('profileAuditSort', { static: false }) profileAuditSort!: MatSort;
  @ViewChild('profileHistoryPaginator', { static: false })
  profileHistoryPaginator!: MatPaginator;
  @ViewChild('profileAuditPaginator', { static: false })
  profileAuditPaginator!: MatPaginator;

  // add 'rights' to the list of displayed columns if necessary
  displayedColumnsHistory: string[] = ['type', 'name', 'status'];
  displayedColumnsAudit: string[] = [
    'userName',
    'executedOperation',
    'classProfileName',
    'timestamp',
  ];

  constructor(
    private profileService: ProfileService,
    private operationService: OperationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.name = this.activatedRoute.snapshot.params['name'];

    this.profileService
      .getProfileByName(this.name, true, true, true)
      .pipe(
        catchError((error) => {
          if (error.status === 400) {
            this.router.navigate(['/page-not-found']);
            return of(null); // Treat 400 error as if data is empty
          }
          return throwError(error);
        })
      )
      .subscribe((data) => {
        if (!data) {
          this.onRedirectToProfileList();
          return;
        }

        this.profile = data;
        this.pendingProfile =
          this.profile.pendingEntity || new ProfilePending();

        // Populate profile history and audit from the fetched profile
        this.profileHistoryDataSource.data = this.profile.history;
        this.profileAuditDataSource.data = this.profile.audit;
      });

    this.operations = this.operationService.getOperations('profile');
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  ngAfterViewInit() {
    this.profileHistoryDataSource.sort = this.profileHistorySort;
    this.profileAuditDataSource.sort = this.profileAuditSort;
    this.profileHistoryDataSource.paginator = this.profileHistoryPaginator;
    this.profileAuditDataSource.paginator = this.profileAuditPaginator;
  }

  applyProfileHistoryFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.profileHistoryDataSource.filter = filterValue.trim().toLowerCase();
  }

  applyProfileAuditFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.profileAuditDataSource.filter = filterValue.trim().toLowerCase();
  }

  repairProfile(name: string) {
    // redirect to repair page:
    this.router.navigate(['/profile-repair', name]);
  }

  approveChanges(name: string) {
    this.profileService.approveChanges(name).subscribe(
      (data) => {
        this.ngOnInit();
      },
      (error: any) => this.handleProfileActionError(error)
    );
  }

  rejectChanges(name: string) {
    this.profileService.rejectChanges(name).subscribe(
      (data) => {
        if (this.profile.status === EProfileStatus.REPAIRED) {
          this.onRedirectToProfileList();
        }

        this.ngOnInit();
      },
      (error: any) => this.handleProfileActionError(error)
    );
  }

  // TODO: implement REPAIR

  onCancel() {
    window.history.back();
  }

  onRedirectToProfileList() {
    this.router.navigate(['/profile-list']);
  }

  formatTimestamp(timestamp: string): string {
    return new Date(timestamp).toLocaleString();
  }

  handleProfileActionError(error: any) {
    this.snackBar.open(
      'The action could not be performed on the profile!',
      'Close',
      {
        duration: 4000,
      }
    );
    console.log(error);
  }

  protected readonly EOperationType = EOperationType;
}
