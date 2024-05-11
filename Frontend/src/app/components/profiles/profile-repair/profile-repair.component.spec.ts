import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProfileRepairComponent } from './profile-repair.component';

describe('ProfileRepairComponent', () => {
  let component: ProfileRepairComponent;
  let fixture: ComponentFixture<ProfileRepairComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ProfileRepairComponent]
    });
    fixture = TestBed.createComponent(ProfileRepairComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
