import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransactionChartsComponent } from './transaction-charts.component';

describe('TransactionChartsComponent', () => {
  let component: TransactionChartsComponent;
  let fixture: ComponentFixture<TransactionChartsComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [TransactionChartsComponent]
    });
    fixture = TestBed.createComponent(TransactionChartsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
