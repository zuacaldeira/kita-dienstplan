import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';

// Material imports
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTabsModule } from '@angular/material/tabs';
import { MatCardModule } from '@angular/material/card';
import { MatListModule } from '@angular/material/list';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';

import { AuthService } from '../../services/auth.service';
import { ApiService } from '../../services/api.service';
import { DateUtilsService } from '../../services/date-utils.service';
import { NotificationService } from '../../services/notification.service';
import { Staff, Group, ScheduleEntry, User } from '../../models/models';
import { StaffDialogComponent, StaffDialogData } from '../dialogs/staff-dialog/staff-dialog.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../dialogs/confirm-dialog/confirm-dialog.component';
import { GroupDialogComponent, GroupDialogData } from '../dialogs/group-dialog/group-dialog.component';
import { ScheduleEntryDialogComponent, ScheduleEntryDialogData } from '../dialogs/schedule-entry-dialog/schedule-entry-dialog.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [
    CommonModule,
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatTabsModule,
    MatCardModule,
    MatListModule,
    MatProgressSpinnerModule,
    MatMenuModule,
    MatDividerModule,
    MatDialogModule
  ],
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit, OnDestroy {
  private destroy$ = new Subject<void>();
  activeTab = 'overview';
  currentUser: User | null = null;
  loading = true;

  // Overview stats
  stats = {
    totalStaff: 0,
    activeStaff: 0,
    groups: 0,
    currentWeek: 0
  };

  // Data
  staff: Staff[] = [];
  groups: Group[] = [];
  scheduleEntries: ScheduleEntry[] = [];

  // Schedule week navigation
  currentYear = new Date().getFullYear();
  currentWeek = 1;

  constructor(
    private authService: AuthService,
    private apiService: ApiService,
    private router: Router,
    private dateUtils: DateUtilsService,
    private notificationService: NotificationService,
    private dialog: MatDialog
  ) {
    // Initialize current week using DateUtilsService
    const { year, week } = this.dateUtils.getCurrentWeek();
    this.currentYear = year;
    this.currentWeek = week;
    this.stats.currentWeek = week;
  }

  ngOnInit(): void {
    this.currentUser = this.authService.currentUserValue;
    this.loadInitialData();
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  loadInitialData(): void {
    // Load stats for overview
    this.apiService.getAllStaff()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (staffData) => {
          this.stats.totalStaff = staffData.length;
          this.stats.activeStaff = staffData.filter(s => s.isActive).length;
        },
        error: (error) => {
          console.error('Error loading staff stats:', error);
        }
      });

    this.apiService.getAllGroups()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (groupsData) => {
          this.stats.groups = groupsData.filter(g => g.isActive).length;
          this.loading = false;
        },
        error: (error) => {
          console.error('Error loading groups stats:', error);
          this.loading = false;
        }
      });
  }

  onTabChange(index: number): void {
    // Load data based on tab index
    // 0 = Overview, 1 = Schedule, 2 = Staff, 3 = Groups
    switch (index) {
      case 1: // Schedule
        if (this.scheduleEntries.length === 0) {
          this.loadSchedule();
        }
        break;
      case 2: // Staff
        if (this.staff.length === 0) {
          this.loadStaff();
        }
        break;
      case 3: // Groups
        if (this.groups.length === 0) {
          this.loadGroups();
        }
        break;
    }
  }

  loadStaff(): void {
    this.apiService.getActiveStaff()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.staff = data;
        },
        error: (error) => {
          console.error('Error loading staff:', error);
        }
      });
  }

  loadGroups(): void {
    this.apiService.getActiveGroups()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.groups = data;
        },
        error: (error) => {
          console.error('Error loading groups:', error);
        }
      });
  }

  loadSchedule(): void {
    this.apiService.getWeekSchedule(this.currentYear, this.currentWeek)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (data) => {
          this.scheduleEntries = data;
        },
        error: (error) => {
          console.error('Error loading schedule:', error);
        }
      });
  }

  previousWeek(): void {
    const prev = this.dateUtils.getPreviousWeek(this.currentYear, this.currentWeek);
    this.currentYear = prev.year;
    this.currentWeek = prev.week;
    this.loadSchedule();
  }

  nextWeek(): void {
    const next = this.dateUtils.getNextWeek(this.currentYear, this.currentWeek);
    this.currentYear = next.year;
    this.currentWeek = next.week;
    this.loadSchedule();
  }

  getDayName(dayOfWeek: number): string {
    // dayOfWeek from backend is 1-7 (Monday-Sunday)
    const days = ['Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag', 'Sonntag'];
    return days[dayOfWeek - 1] || '';
  }

  // Staff CRUD operations
  openStaffDialog(staff?: Staff): void {
    const dialogData: StaffDialogData = {
      mode: staff ? 'edit' : 'create',
      staff: staff
    };

    const dialogRef = this.dialog.open(StaffDialogComponent, {
      width: '600px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((result: Partial<Staff> | undefined) => {
      if (result) {
        if (staff) {
          // Update existing staff
          this.apiService.updateStaff(staff.id, result)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.notificationService.success('Mitarbeiter erfolgreich aktualisiert');
                this.loadStaff();
                this.loadInitialData(); // Refresh stats
              },
              error: (error) => {
                console.error('Error updating staff:', error);
                this.notificationService.error('Fehler beim Aktualisieren des Mitarbeiters');
              }
            });
        } else {
          // Create new staff
          this.apiService.createStaff(result)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.notificationService.success('Mitarbeiter erfolgreich erstellt');
                this.loadStaff();
                this.loadInitialData(); // Refresh stats
              },
              error: (error) => {
                console.error('Error creating staff:', error);
                this.notificationService.error('Fehler beim Erstellen des Mitarbeiters');
              }
            });
        }
      }
    });
  }

  openDeleteStaffConfirm(staff: Staff): void {
    const dialogData: ConfirmDialogData = {
      title: 'Mitarbeiter löschen',
      message: `Möchten Sie ${staff.fullName} wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.`,
      confirmText: 'Löschen',
      cancelText: 'Abbrechen',
      type: 'danger'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.apiService.deleteStaff(staff.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.notificationService.success('Mitarbeiter erfolgreich gelöscht');
              this.loadStaff();
              this.loadInitialData(); // Refresh stats
            },
            error: (error) => {
              console.error('Error deleting staff:', error);
              this.notificationService.error('Fehler beim Löschen des Mitarbeiters');
            }
          });
      }
    });
  }

  // Group CRUD operations
  openGroupDialog(group?: Group): void {
    const dialogData: GroupDialogData = {
      mode: group ? 'edit' : 'create',
      group: group
    };

    const dialogRef = this.dialog.open(GroupDialogComponent, {
      width: '600px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((result: Partial<Group> | undefined) => {
      if (result) {
        if (group) {
          // Update existing group
          this.apiService.updateGroup(group.id, result)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.notificationService.success('Gruppe erfolgreich aktualisiert');
                this.loadGroups();
                this.loadInitialData(); // Refresh stats
              },
              error: (error) => {
                console.error('Error updating group:', error);
                this.notificationService.error('Fehler beim Aktualisieren der Gruppe');
              }
            });
        } else {
          // Create new group
          this.apiService.createGroup(result)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.notificationService.success('Gruppe erfolgreich erstellt');
                this.loadGroups();
                this.loadInitialData(); // Refresh stats
              },
              error: (error) => {
                console.error('Error creating group:', error);
                this.notificationService.error('Fehler beim Erstellen der Gruppe');
              }
            });
        }
      }
    });
  }

  openDeleteGroupConfirm(group: Group): void {
    const dialogData: ConfirmDialogData = {
      title: 'Gruppe löschen',
      message: `Möchten Sie die Gruppe "${group.name}" wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.`,
      confirmText: 'Löschen',
      cancelText: 'Abbrechen',
      type: 'danger'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.apiService.deleteGroup(group.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.notificationService.success('Gruppe erfolgreich gelöscht');
              this.loadGroups();
              this.loadInitialData(); // Refresh stats
            },
            error: (error) => {
              console.error('Error deleting group:', error);
              this.notificationService.error('Fehler beim Löschen der Gruppe');
            }
          });
      }
    });
  }

  // Schedule Entry CRUD operations
  openScheduleEntryDialog(entry?: ScheduleEntry): void {
    const dialogData: ScheduleEntryDialogData = {
      mode: entry ? 'edit' : 'create',
      entry: entry,
      weekNumber: this.currentWeek,
      year: this.currentYear
    };

    const dialogRef = this.dialog.open(ScheduleEntryDialogComponent, {
      width: '600px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((result: any) => {
      if (result) {
        if (result.mode === 'error') {
          this.notificationService.error(result.message);
        } else if (result.mode === 'edit') {
          // Update existing schedule entry
          this.apiService.updateScheduleEntry(result.entryId, result.data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.notificationService.success('Dienstplaneintrag erfolgreich aktualisiert');
                this.loadSchedule();
              },
              error: (error) => {
                console.error('Error updating schedule entry:', error);
                this.notificationService.error('Fehler beim Aktualisieren des Dienstplaneintrags');
              }
            });
        } else if (result.mode === 'create') {
          // Create new schedule entry
          this.apiService.createScheduleEntry(result.data)
            .pipe(takeUntil(this.destroy$))
            .subscribe({
              next: () => {
                this.notificationService.success('Dienstplaneintrag erfolgreich erstellt');
                this.loadSchedule();
              },
              error: (error) => {
                console.error('Error creating schedule entry:', error);
                this.notificationService.error('Fehler beim Erstellen des Dienstplaneintrags');
              }
            });
        }
      }
    });
  }

  openDeleteScheduleEntryConfirm(entry: ScheduleEntry): void {
    const dialogData: ConfirmDialogData = {
      title: 'Dienstplaneintrag löschen',
      message: `Möchten Sie den Dienstplaneintrag für ${entry.staffName} am ${this.getDayName(entry.dayOfWeek)} wirklich löschen? Diese Aktion kann nicht rückgängig gemacht werden.`,
      confirmText: 'Löschen',
      cancelText: 'Abbrechen',
      type: 'danger'
    };

    const dialogRef = this.dialog.open(ConfirmDialogComponent, {
      width: '400px',
      data: dialogData
    });

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.apiService.deleteScheduleEntry(entry.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              this.notificationService.success('Dienstplaneintrag erfolgreich gelöscht');
              this.loadSchedule();
            },
            error: (error) => {
              console.error('Error deleting schedule entry:', error);
              this.notificationService.error('Fehler beim Löschen des Dienstplaneintrags');
            }
          });
      }
    });
  }

  logout(): void {
    this.authService.logout();
    this.notificationService.info('Sie wurden abgemeldet');
    this.router.navigate(['/login']);
  }
}
