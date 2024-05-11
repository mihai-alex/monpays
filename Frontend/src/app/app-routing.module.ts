import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { UserDashboardComponent } from './components/users/user-dashboard/user-dashboard.component';
import { ForbiddenComponent } from './components/forbidden/forbidden.component';
import { UserListComponent } from './components/users/user-list/user-list.component';
import { UserCreateComponent } from './components/users/user-create/user-create.component';
import { ProfileListComponent } from './components/profiles/profile-list/profile-list.component';
import { ProfileCreateComponent } from './components/profiles/profile-create/profile-create.component';
import { UserModifyComponent } from './components/users/user-modify/user-modify.component';
import { ProfileModifyComponent } from './components/profiles/profile-modify/profile-modify.component';
import { UserDetailsComponent } from './components/users/user-details/user-details.component';
import { ProfileDetailsComponent } from './components/profiles/profile-details/profile-details.component';
import { AccountListComponent } from './components/accounts/account-list/account-list.component';
import { AccountCreateComponent } from './components/accounts/account-create/account-create.component';
import { AccountModifyComponent } from './components/accounts/account-modify/account-modify.component';
import { AccountDetailsComponent } from './components/accounts/account-details/account-details.component';
import { ChangePasswordComponent } from './components/change-password/change-password.component';
import { authGuard } from './auth/auth.guard';
import { ProfileRepairComponent } from './components/profiles/profile-repair/profile-repair.component';
import { PageNotFoundComponent } from './components/page-not-found/page-not-found.component';
import { UserRepairComponent } from './components/users/user-repair/user-repair.component';
import { AccountRepairComponent } from './components/accounts/account-repair/account-repair.component';
import { BalanceListComponent } from './components/balances/balance-list/balance-list.component';
import { PaymentListComponent } from './components/payment/payment-list/payment-list.component';
import { PaymentCreateComponent } from './components/payment/payment-create/payment-create.component';
import { PaymentDetailsComponent } from './components/payment/payment-details/payment-details.component';
import { PaymentRepairComponent } from './components/payment/payment-repair/payment-repair.component';

const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'home', component: HomeComponent },
  {
    path: 'forbidden',
    component: ForbiddenComponent,
  },
  {
    path: 'page-not-found',
    component: PageNotFoundComponent,
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'change-password/:username',
    component: ChangePasswordComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user',
    component: UserDashboardComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user-list',
    component: UserListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user-create',
    component: UserCreateComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user-modify/:userName',
    component: UserModifyComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user-details/:userName',
    component: UserDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'user-repair/:userName',
    component: UserRepairComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile-list',
    component: ProfileListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile-create',
    component: ProfileCreateComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile-modify/:name',
    component: ProfileModifyComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile-details/:name',
    component: ProfileDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'profile-repair/:name',
    component: ProfileRepairComponent,
    canActivate: [authGuard],
  },
  {
    path: 'account-list',
    component: AccountListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'account-create',
    component: AccountCreateComponent,
    canActivate: [authGuard],
  },
  {
    path: 'account-modify/:accountNumber',
    component: AccountModifyComponent,
    canActivate: [authGuard],
  },
  {
    path: 'account-details/:accountNumber',
    component: AccountDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'account-repair/:accountNumber',
    component: AccountRepairComponent,
    canActivate: [authGuard],
  },
  {
    path: 'balance-list',
    component: BalanceListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'payment-list',
    component: PaymentListComponent,
    canActivate: [authGuard],
  },
  {
    path: 'payment-create',
    component: PaymentCreateComponent,
    canActivate: [authGuard],
  },
  {
    path: 'payment-details/:paymentNumber',
    component: PaymentDetailsComponent,
    canActivate: [authGuard],
  },
  {
    path: 'payment-repair/:paymentNumber',
    component: PaymentRepairComponent,
    canActivate: [authGuard],
  },
  { path: '**', component: PageNotFoundComponent }, // Wildcard route for unmatched paths - MUST be the last route defined
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
