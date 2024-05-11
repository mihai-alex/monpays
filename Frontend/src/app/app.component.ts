import {Component, NgModule} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { UserCreateComponent } from './components/users/user-create/user-create.component';
import { UserAuthService } from './services/user-auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
})
export class AppComponent {
  title = 'Monpays';
  isUserLoggedIn: boolean | undefined;

  constructor(
    private userAuthService: UserAuthService,
    private _dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.isUserLoggedIn = this.userAuthService.isAuthenticated();

    // Subscribe to changes in user login status
    this.userAuthService.getAuthenticatedStatus().subscribe((loggedIn) => {
      this.isUserLoggedIn = loggedIn;
    });
  }

  openCreateUserForm() {
    this._dialog.open(UserCreateComponent);
  }
}
