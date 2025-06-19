import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateRisk } from './create-risk';

describe('CreateRisk', () => {
  let component: CreateRisk;
  let fixture: ComponentFixture<CreateRisk>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateRisk]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CreateRisk);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
