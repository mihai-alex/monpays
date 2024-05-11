import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MaterialModule } from './material-module';
import { ReactiveFormsModule } from '@angular/forms';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ToastrModule } from 'ngx-toastr';
import { LoginComponent } from './components/login/login.component';
import { HomeComponent } from './components/home/home.component';
import { UserDashboardComponent } from './components/users/user-dashboard/user-dashboard.component';
import { HeaderComponent } from './components/header/header.component';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { RouterModule } from '@angular/router';
import { CommonModule, NgOptimizedImage } from '@angular/common';
import { UserService } from './services/user.service';
import { SidenavComponent } from './components/sidenav/sidenav.component';
import { UserCreateComponent } from './components/users/user-create/user-create.component';
import { UserListComponent } from './components/users/user-list/user-list.component';
import { ProfileListComponent } from './components/profiles/profile-list/profile-list.component';
import { ProfileCreateComponent } from './components/profiles/profile-create/profile-create.component';
import { UserModifyComponent } from './components/users/user-modify/user-modify.component';
import { ProfileModifyComponent } from './components/profiles/profile-modify/profile-modify.component';
import { UserDetailsComponent } from './components/users/user-details/user-details.component';
import { ProfileDetailsComponent } from './components/profiles/profile-details/profile-details.component';
import { GenericAuditComponent } from './components/generic-audit/generic-audit.component';
import { AccountCreateComponent } from './components/accounts/account-create/account-create.component';
import { AccountDetailsComponent } from './components/accounts/account-details/account-details.component';
import { AccountListComponent } from './components/accounts/account-list/account-list.component';
import { AccountModifyComponent } from './components/accounts/account-modify/account-modify.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { SidenavEntryComponent } from './components/sidenav-entry/sidenav-entry.component';
import { ProfileRepairComponent } from './components/profiles/profile-repair/profile-repair.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { UserRepairComponent } from './components/users/user-repair/user-repair.component';
import { AccountRepairComponent } from './components/accounts/account-repair/account-repair.component';
import { TokenExpirationInterceptor } from './auth/token-expirator-interceptor';
import { BalanceListComponent } from './components/balances/balance-list/balance-list.component';
import { BalanceDetailsComponent } from './components/balances/balance-details/balance-details.component';
import { PaymentDetailsComponent } from './components/payment/payment-details/payment-details.component';
import { PaymentListComponent } from './components/payment/payment-list/payment-list.component';
import { PaymentRepairComponent } from './components/payment/payment-repair/payment-repair.component';
import { PaymentCreateComponent } from './components/payment/payment-create/payment-create.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    HomeComponent,
    UserDashboardComponent,
    HeaderComponent,
    ForbiddenComponent,
    SidenavComponent,
    UserCreateComponent,
    UserListComponent,
    ProfileListComponent,
    ProfileCreateComponent,
    UserModifyComponent,
    ProfileModifyComponent,
    UserDetailsComponent,
    ProfileDetailsComponent,
    GenericAuditComponent,
    AccountCreateComponent,
    AccountDetailsComponent,
    AccountListComponent,
    AccountModifyComponent,
    ChangePasswordComponent,
    SidenavEntryComponent,
    ProfileRepairComponent,
    PageNotFoundComponent,
    UserRepairComponent,
    AccountRepairComponent,
    BalanceListComponent,
    BalanceDetailsComponent,
    PaymentDetailsComponent,
    PaymentListComponent,
    PaymentRepairComponent,
    PaymentCreateComponent,
  ],
  imports: [
    MaterialModule,
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    HttpClientModule,
    ToastrModule.forRoot(),
    FormsModule,
    HttpClientModule,
    RouterModule,
    CommonModule,
    ReactiveFormsModule,
    NgOptimizedImage,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenExpirationInterceptor,
      multi: true,
    },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
