import { Component, OnInit, ViewChild } from '@angular/core';
import { Router } from '@angular/router';
import { MatPaginator } from '@angular/material/paginator';
import { MatSort } from '@angular/material/sort';
import { MatTableDataSource } from '@angular/material/table';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-list',
  templateUrl: './user-list.component.html',
  styleUrls: ['./user-list.component.scss'],
})
export class UserListComponent implements OnInit {
  operations!: Observable<EOperationType[]>;
  displayedColumns: string[] = [
    'userName',
    'firstName',
    'lastName',
    'emailAddress',
    'phoneNumber',
    'address',
    'actions',
  ];
  dataSource!: MatTableDataSource<User>;

  @ViewChild(MatPaginator) paginator!: MatPaginator;
  @ViewChild(MatSort) sort!: MatSort;

  constructor(
    private userService: UserService,
    private operationService: OperationService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    // Get users from backend
    this.getUsers();
    this.operations = this.operationService.getOperations('profile');
  }

  isAllowed(operationType: EOperationType): Observable<boolean> {
    return this.operations.pipe(
      map((operationTypesArray) => operationTypesArray.includes(operationType))
    );
  }

  getUsers() {
    this.userService.getUsers().subscribe((data) => {
      this.dataSource = new MatTableDataSource<User>(data);
      this.dataSource.paginator = this.paginator;
      this.dataSource.sort = this.sort;
    });
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.dataSource.filter = filterValue.trim().toLowerCase();

    if (this.dataSource.paginator) {
      this.dataSource.paginator.firstPage();
    }
  }

  createUser(): void {
    this.router.navigate(['/user-create']);
  }

  modifyUser(userName: string) {
    this.router.navigate(['/user-modify', userName]);
  }

  removeUser(userName: string) {
    this.userService.removeUser(userName).subscribe(
      (data) => {
        this.getUsers();
      },
      (error) => {
        this.snackBar.open(
          'Sir, your operation could not be executed!',
          'Close',
          { duration: 4000 }
        );
        console.log(error);
        this.getUsers();
      }
    );
  }

  detailsUser(userName: string) {
    this.router.navigate(['/user-details', userName]);
  }

  protected readonly EOperationType = EOperationType;
}
