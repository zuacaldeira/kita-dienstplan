import { Component, Inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatIconModule } from '@angular/material/icon';
import { Group } from '../../../models/models';

export interface GroupDialogData {
  mode: 'create' | 'edit';
  group?: Group;
}

@Component({
  selector: 'app-group-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatIconModule
  ],
  templateUrl: './group-dialog.component.html',
  styleUrls: ['./group-dialog.component.css']
})
export class GroupDialogComponent implements OnInit {
  groupForm: FormGroup;
  isEditMode: boolean;

  constructor(
    private fb: FormBuilder,
    public dialogRef: MatDialogRef<GroupDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: GroupDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.groupForm = this.createForm();
  }

  ngOnInit(): void {
    if (this.isEditMode && this.data.group) {
      this.groupForm.patchValue({
        name: this.data.group.name,
        description: this.data.group.description,
        isActive: this.data.group.isActive
      });
    }
  }

  private createForm(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      description: [''],
      isActive: [true]
    });
  }

  getTitle(): string {
    return this.isEditMode ? 'Gruppe bearbeiten' : 'Neue Gruppe';
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.groupForm.valid) {
      const formValue = this.groupForm.value;
      const result: Partial<Group> = {
        name: formValue.name,
        description: formValue.description,
        isActive: formValue.isActive
      };
      this.dialogRef.close(result);
    }
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.groupForm.get(controlName);
    return !!(control && control.hasError(errorName) && control.touched);
  }

  getErrorMessage(controlName: string): string {
    const control = this.groupForm.get(controlName);
    if (!control) return '';

    if (control.hasError('required')) {
      return 'Dieses Feld ist erforderlich';
    }
    if (control.hasError('minlength')) {
      const minLength = control.getError('minlength').requiredLength;
      return `Mindestens ${minLength} Zeichen erforderlich`;
    }
    return '';
  }
}
