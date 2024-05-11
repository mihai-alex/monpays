import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AccountRepairComponent } from './account-repair.component';

describe('AccountRepairComponent', () => {
  let component: AccountRepairComponent;
  let fixture: ComponentFixture<AccountRepairComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [AccountRepairComponent]
    });
    fixture = TestBed.createComponent(AccountRepairComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
