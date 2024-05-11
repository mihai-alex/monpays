import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UserModifyComponent } from './user-modify.component';

describe('UserModifyComponent', () => {
  let component: UserModifyComponent;
  let fixture: ComponentFixture<UserModifyComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [UserModifyComponent]
    });
    fixture = TestBed.createComponent(UserModifyComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
