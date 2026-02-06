import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { Staff } from '../../../models/models';

export interface StaffDialogData {
  mode: 'create' | 'edit';
  staff?: Staff;
}

@Component({
  selector: 'app-staff-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatCheckboxModule,
    MatIconModule
  ],
  templateUrl: './staff-dialog.component.html',
  styleUrls: ['./staff-dialog.component.css']
})
export class StaffDialogComponent implements OnInit {
  staffForm: FormGroup;
  isEditMode: boolean;

  roles = [
    { value: 'Erzieher/in', label: 'Erzieher/in' },
    { value: 'Kinderpfleger/in', label: 'Kinderpfleger/in' },
    { value: 'Praktikant/in', label: 'Praktikant/in' },
    { value: 'Aushilfe', label: 'Aushilfe' },
    { value: 'Leitung', label: 'Leitung' }
  ];

  employmentTypes = [
    { value: 'Vollzeit', label: 'Vollzeit' },
    { value: 'Teilzeit', label: 'Teilzeit' },
    { value: 'Geringfügig', label: 'Geringfügig' },
    { value: 'Honorar', label: 'Honorar' }
  ];

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<StaffDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: StaffDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.staffForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.staff) {
      this.staffForm.patchValue({
        firstName: this.data.staff.firstName,
        lastName: this.data.staff.lastName,
        email: this.data.staff.email,
        phone: this.data.staff.phone,
        role: this.data.staff.role,
        employmentType: this.data.staff.employmentType,
        weeklyHours: this.data.staff.weeklyHours,
        isActive: this.data.staff.isActive
      });
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.pattern(/^[\d\s\-\+\(\)]+$/)]],
      role: ['', Validators.required],
      employmentType: ['', Validators.required],
      weeklyHours: [40, [Validators.required, Validators.min(1), Validators.max(60)]],
      isActive: [true]
    });
  }

  getTitle(): string {
    return this.isEditMode ? 'Mitarbeiter bearbeiten' : 'Neuer Mitarbeiter';
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.staffForm.valid) {
      const formValue = this.staffForm.value;
      const result: Partial<Staff> = {
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        phone: formValue.phone,
        role: formValue.role,
        employmentType: formValue.employmentType,
        weeklyHours: formValue.weeklyHours,
        isActive: formValue.isActive
      };
      this.dialogRef.close(result);
    }
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.staffForm.get(controlName);
    return !!(control && control.hasError(errorName) && control.touched);
  }

  getErrorMessage(controlName: string): string {
    const control = this.staffForm.get(controlName);
    if (!control) return '';

    if (control.hasError('required')) {
      return 'Dieses Feld ist erforderlich';
    }
    if (control.hasError('minlength')) {
      const minLength = control.getError('minlength').requiredLength;
      return `Mindestens ${minLength} Zeichen erforderlich`;
    }
    if (control.hasError('email')) {
      return 'Bitte geben Sie eine gültige E-Mail-Adresse ein';
    }
    if (control.hasError('pattern')) {
      return 'Ungültiges Format';
    }
    if (control.hasError('min')) {
      return 'Wert ist zu niedrig';
    }
    if (control.hasError('max')) {
      return 'Wert ist zu hoch';
    }
    return '';
  }
}
