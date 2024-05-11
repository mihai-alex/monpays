import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserRepairComponent } from './user-repair.component';

describe('UserRepairComponent', () => {
  let component: UserRepairComponent;
  let fixture: ComponentFixture<UserRepairComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserRepairComponent]
    });
    fixture = TestBed.createComponent(UserRepairComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
