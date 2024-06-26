import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Observable } from 'rxjs';
import { EOperationType } from '../../../constants/enums/e-operation-type';
import { OperationService } from '../../../services/operation.service';
import { map } from 'rxjs/operators';

@Component({
  selector: 'app-user-modify',
  templateUrl: './user-modify.component.html',
  styleUrls: ['./user-modify.component.scss'],
})
export class UserModifyComponent implements OnInit {
  user: User = new User();
  userName: string = '';
  operations!: Observable<EOperationType[]>;

  constructor(
    private userService: UserService,
    private operationService: OperationService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.userName = this.activatedRoute.snapshot.params['userName'];
    this.operations = this.operationService.getOperations('user');

    this.isAllowed(EOperationType.LIST).subscribe((canList) => {
      this.isAllowed(EOperationType.MODIFY).subscribe((canModify) => {
        if (!canList || !canModify) {
          this.router.navigate(['/forbidden']);
        } else {
          this.userService.getUserByUserName(this.userName).subscribe(
            (data) => {
              this.user = data;
            },
            (error) => this.handleUserActionError(error)
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
    this.userService.modifyUser(this.userName, this.user).subscribe(
      (data) => {
        this.goToUserList();
      },
      (error) => this.handleUserActionError(error)
    );
  }

  goToUserList() {
    this.router.navigate(['/user-list']);
  }

  onCancel() {
    // Navigate the user to the profile-list page when cancel is clicked
    //this.goToUserList();
    window.history.back();
  }

  handleUserActionError(error: any) {
    this.snackBar.open('The user could not be modified!', 'Close', {
      duration: 4000,
    });
    // console.log(error);
  }
}
