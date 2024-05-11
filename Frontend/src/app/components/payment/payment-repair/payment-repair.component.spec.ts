import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PaymentRepairComponent } from './payment-repair.component';

describe('PaymentRepairComponent', () => {
  let component: PaymentRepairComponent;
  let fixture: ComponentFixture<PaymentRepairComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [PaymentRepairComponent]
    });
    fixture = TestBed.createComponent(PaymentRepairComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
