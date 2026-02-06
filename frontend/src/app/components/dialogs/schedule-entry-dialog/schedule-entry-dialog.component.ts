import { Component, Inject, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, AbstractControl, ValidationErrors } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { Subject, takeUntil } from 'rxjs';
import { ApiService } from '../../../services/api.service';
import { DateUtilsService } from '../../../services/date-utils.service';
import { ScheduleEntry, Staff, CreateScheduleEntryRequest, UpdateScheduleEntryRequest, WeeklySchedule } from '../../../models/models';

export interface ScheduleEntryDialogData {
  mode: 'create' | 'edit';
  entry?: ScheduleEntry;
  weekNumber: number;
  year: number;
}

@Component({
  selector: 'app-schedule-entry-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatIconModule,
    MatProgressSpinnerModule
  ],
  templateUrl: './schedule-entry-dialog.component.html',
  styleUrls: ['./schedule-entry-dialog.component.css']
})
export class ScheduleEntryDialogComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  scheduleForm: FormGroup;
  isEditMode: boolean;
  isLoading = false;
  isSaving = false;

  staffList: Staff[] = [];
  workingHours = '0:00';

  daysOfWeek = [
    { value: 1, label: 'Montag' },
    { value: 2, label: 'Dienstag' },
    { value: 3, label: 'Mittwoch' },
    { value: 4, label: 'Donnerstag' },
    { value: 5, label: 'Freitag' }
  ];

  statusOptions = [
    { value: 'NORMAL', label: 'Normal' },
    { value: 'FREI', label: 'Frei' },
    { value: 'URLAUB', label: 'Urlaub' },
    { value: 'KRANK', label: 'Krank' },
    { value: 'FORTBILDUNG', label: 'Fortbildung' }
  ];

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService,
    private dateUtils: DateUtilsService,
    public dialogRef: MatDialogRef<ScheduleEntryDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: ScheduleEntryDialogData
  ) {
    this.isEditMode = data.mode === 'edit';
    this.scheduleForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadStaff();

    // Subscribe to time changes to calculate working hours
    this.scheduleForm.get('startTime')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.updateWorkingHours());

    this.scheduleForm.get('endTime')?.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.updateWorkingHours());

    if (this.isEditMode && this.data.entry) {
      // Patch form with existing entry data
      this.scheduleForm.patchValue({
        staffId: this.data.entry.staffId,
        dayOfWeek: this.data.entry.dayOfWeek,
        startTime: this.data.entry.startTime,
        endTime: this.data.entry.endTime,
        status: this.data.entry.status || 'NORMAL',
        notes: this.data.entry.notes || ''
      });
      this.updateWorkingHours();
    } else {
      // Set default values for create mode
      this.scheduleForm.patchValue({
        dayOfWeek: 1, // Monday
        startTime: '08:00',
        endTime: '16:00',
        status: 'NORMAL'
      });
      this.updateWorkingHours();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      staffId: ['', Validators.required],
      dayOfWeek: ['', Validators.required],
      startTime: ['', [Validators.required, Validators.pattern(/^([0-1][0-9]|2[0-3]):[0-5][0-9]$/)]],
      endTime: ['', [Validators.required, Validators.pattern(/^([0-1][0-9]|2[0-3]):[0-5][0-9]$/)]],
      status: ['NORMAL', Validators.required],
      notes: ['']
    }, { validators: this.timeRangeValidator });
  }

  // Custom validator to ensure end time is after start time
  private timeRangeValidator(control: AbstractControl): ValidationErrors | null {
    const startTime = control.get('startTime')?.value;
    const endTime = control.get('endTime')?.value;

    if (startTime && endTime) {
      const [startHour, startMin] = startTime.split(':').map(Number);
      const [endHour, endMin] = endTime.split(':').map(Number);

      const startMinutes = startHour * 60 + startMin;
      const endMinutes = endHour * 60 + endMin;

      if (endMinutes <= startMinutes) {
        return { timeRange: true };
      }
    }
    return null;
  }

  private loadStaff(): void {
    this.isLoading = true;
    this.apiService.getActiveStaff()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.staffList = data;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading staff:', error);
          this.isLoading = false;
        }
      });
  }

  private updateWorkingHours(): void {
    const startTime = this.scheduleForm.get('startTime')?.value;
    const endTime = this.scheduleForm.get('endTime')?.value;

    if (startTime && endTime) {
      this.workingHours = this.calculateWorkingHours(startTime, endTime);
    }
  }

  private calculateWorkingHours(start: string, end: string): string {
    const [startHour, startMin] = start.split(':').map(Number);
    const [endHour, endMin] = end.split(':').map(Number);

    const totalMinutes = (endHour * 60 + endMin) - (startHour * 60 + startMin);

    if (totalMinutes < 0) {
      return '0:00';
    }

    const hours = Math.floor(totalMinutes / 60);
    const minutes = totalMinutes % 60;
    return `${hours}:${minutes.toString().padStart(2, '0')}`;
  }

  getTitle(): string {
    return this.isEditMode ? 'Dienstplaneintrag bearbeiten' : 'Neuen Dienstplaneintrag erstellen';
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSubmit(): void {
    if (this.scheduleForm.valid && !this.isSaving) {
      this.isSaving = true;
      const formValue = this.scheduleForm.value;

      if (this.isEditMode && this.data.entry) {
        // Edit mode - use UpdateScheduleEntryRequest
        const updateRequest: UpdateScheduleEntryRequest = {
          startTime: formValue.startTime,
          endTime: formValue.endTime,
          status: formValue.status,
          notes: formValue.notes || undefined
        };
        this.dialogRef.close({ mode: 'edit', data: updateRequest, entryId: this.data.entry.id });
      } else {
        // Create mode - need to get or create weekly schedule first
        this.prepareCreateRequest(formValue);
      }
    }
  }

  private prepareCreateRequest(formValue: any): void {
    // Get or create weekly schedule for the current week
    this.apiService.getWeeklyScheduleByWeek(this.data.year, this.data.weekNumber)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (weeklySchedule: WeeklySchedule) => {
          this.createScheduleEntry(formValue, weeklySchedule.id);
        },
        error: () => {
          // Weekly schedule doesn't exist, create it
          const { startDate, endDate } = this.dateUtils.getWeekDates(this.data.year, this.data.weekNumber);

          this.apiService.createWeeklySchedule({
            weekNumber: this.data.weekNumber,
            year: this.data.year,
            startDate: startDate,
            endDate: endDate
          }).pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (weeklySchedule: WeeklySchedule) => {
              this.createScheduleEntry(formValue, weeklySchedule.id);
            },
            error: (error) => {
              console.error('Error creating weekly schedule:', error);
              this.isSaving = false;
              this.dialogRef.close({ mode: 'error', message: 'Fehler beim Erstellen des Wochenplans' });
            }
          });
        }
      });
  }

  private createScheduleEntry(formValue: any, weeklyScheduleId: number): void {
    // Calculate work date from year, week, and day of week
    const workDate = this.dateUtils.getDateFromWeekAndDay(
      this.data.year,
      this.data.weekNumber,
      formValue.dayOfWeek
    );

    const createRequest: CreateScheduleEntryRequest = {
      weeklyScheduleId: weeklyScheduleId,
      staffId: formValue.staffId,
      dayOfWeek: formValue.dayOfWeek,
      workDate: workDate,
      startTime: formValue.startTime,
      endTime: formValue.endTime,
      status: formValue.status,
      notes: formValue.notes || undefined
    };

    this.dialogRef.close({ mode: 'create', data: createRequest });
  }

  hasError(controlName: string, errorName: string): boolean {
    const control = this.scheduleForm.get(controlName);
    return !!(control && control.hasError(errorName) && control.touched);
  }

  hasFormError(errorName: string): boolean {
    return !!(this.scheduleForm.hasError(errorName) &&
             this.scheduleForm.get('startTime')?.touched &&
             this.scheduleForm.get('endTime')?.touched);
  }

  getErrorMessage(controlName: string): string {
    const control = this.scheduleForm.get(controlName);
    if (!control) return '';

    if (control.hasError('required')) {
      return 'Dieses Feld ist erforderlich';
    }
    if (control.hasError('pattern')) {
      return 'Bitte geben Sie eine gültige Zeit ein (HH:MM)';
    }
    return '';
  }

  getStaffName(staffId: number): string {
    const staff = this.staffList.find(s => s.id === staffId);
    return staff ? staff.fullName : '';
  }
}
