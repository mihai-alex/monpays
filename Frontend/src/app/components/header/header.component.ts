import { Component, DoCheck } from '@angular/core';
import { UserAuthService } from '../../services/user-auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss'],
})
export class HeaderComponent implements DoCheck {
  username: string = '';

  constructor(
    private userAuthService: UserAuthService,
    private router: Router
  ) {}

  public isAuthenticated(): boolean {
    return this.userAuthService.isAuthenticated();
  }

  public logout() {
    this.userAuthService.clear(); //TODO: LEAVE THIS LINE UNCOMMENTED WHEN DONE TESTING WITH OWASP ZAP!!!
    this.router.navigate(['/home']);
  }

  ngDoCheck(): void {
    // TODO: COMMENT THIS BLOCK OF CODE OUT WHEN DONE TESTING WITH OWASP ZAP!!!
    // this.userAuthService.setToken(
    //   'eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhMSIsInRpbWVzdGFtcCI6MTY5Mjc2NDI0ODM0OCwiaWF0IjoxNjkyNzY0MjQ4LCJleHAiOjE2OTI4NTA2NDh9.hXcGp7iAyUkGZFLHU3510TEqpIUQf18l1rRqxCVp78U'
    // );
    //console.log(this.userAuthService.getToken());

    // Update the username whenever there is a change in authentication status
    if (this.isAuthenticated()) {
      this.username = this.userAuthService.getUsername();
    } else {
      this.username = '';
    }
  }
}
