import { Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/models/user';
import { UserService } from 'src/app/services/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-user-modify',
  templateUrl: './user-modify.component.html',
  styleUrls: ['./user-modify.component.scss'],
})
export class UserModifyComponent {
  user: User = new User();
  userName: string = '';

  constructor(
    private userService: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.userName = this.activatedRoute.snapshot.params['userName'];
    this.userService.getUserByUserName(this.userName).subscribe(
      (data) => {
        this.user = data;
      },
      (error) => this.handleUserActionError(error)
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
    console.log(error);
  }
}
