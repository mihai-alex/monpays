import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenericAuditComponent } from './generic-audit.component';

describe('GenericAuditComponent', () => {
  let component: GenericAuditComponent;
  let fixture: ComponentFixture<GenericAuditComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [GenericAuditComponent]
    });
    fixture = TestBed.createComponent(GenericAuditComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
