import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SidenavEntryComponent } from './sidenav-entry.component';

describe('SidenavEntryComponent', () => {
  let component: SidenavEntryComponent;
  let fixture: ComponentFixture<SidenavEntryComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [SidenavEntryComponent]
    });
    fixture = TestBed.createComponent(SidenavEntryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
