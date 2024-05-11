import {Component, NgModule} from '@angular/core';
import { UserAuthService } from 'src/app/services/user-auth.service';

@Component({
  selector: 'app-user-dashboard',
  templateUrl: './user-dashboard.component.html',
  styleUrls: ['./user-dashboard.component.scss'],
})
export class UserDashboardComponent {
  username: string = ''; // Variable to store the username

  constructor(private userAuthService: UserAuthService) {}

  ngOnInit(): void {
    this.username = this.userAuthService.getUsername();
  }
}
