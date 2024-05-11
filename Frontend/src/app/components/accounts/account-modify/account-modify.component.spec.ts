import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountModifyComponent } from './account-modify.component';

describe('AccountModifyComponent', () => {
  let component: AccountModifyComponent;
  let fixture: ComponentFixture<AccountModifyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccountModifyComponent]
    });
    fixture = TestBed.createComponent(AccountModifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
