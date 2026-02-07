import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ScheduleEntry } from '../../models/models';

interface StaffWeekRow {
  staffId: number;
  staffName: string;
  staffRole: string;
  monday?: ScheduleEntry;
  tuesday?: ScheduleEntry;
  wednesday?: ScheduleEntry;
  thursday?: ScheduleEntry;
  friday?: ScheduleEntry;
}

@Component({
  selector: 'app-schedule-table',
  standalone: true,
  imports: [CommonModule, MatTableModule, MatIconModule, MatButtonModule],
  templateUrl: './schedule-table.html',
  styleUrls: ['./schedule-table.css']
})
export class ScheduleTableComponent implements OnChanges {
  @Input() scheduleEntries: ScheduleEntry[] = [];
  @Input() currentWeek: number = 1;
  @Input() currentYear: number = new Date().getFullYear();

  @Output() editEntry = new EventEmitter<ScheduleEntry>();
  @Output() deleteEntry = new EventEmitter<ScheduleEntry>();
  @Output() createEntry = new EventEmitter<{ staffId: number, dayOfWeek: number }>();

  displayedColumns: string[] = ['staff', 'monday', 'tuesday', 'wednesday', 'thursday', 'friday'];
  tableData: StaffWeekRow[] = [];

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['scheduleEntries']) {
      this.transformToTableData();
    }
  }

  transformToTableData(): void {
    // Step 1: Group entries by staffId
    const staffMap = new Map<number, ScheduleEntry[]>();
    this.scheduleEntries.forEach(entry => {
      if (!staffMap.has(entry.staffId)) {
        staffMap.set(entry.staffId, []);
      }
      staffMap.get(entry.staffId)!.push(entry);
    });

    // Step 2: Transform to row objects
    const rows: StaffWeekRow[] = [];
    staffMap.forEach((staffEntries, staffId) => {
      const row: StaffWeekRow = {
        staffId: staffId,
        staffName: staffEntries[0].staffName,
        staffRole: staffEntries[0].staffRole || '',
      };

      // Map entries to day columns (0=Monday, 4=Friday in ISO 8601)
      staffEntries.forEach(entry => {
        const dayIndex = entry.dayOfWeek; // 0-6 (0=Monday, 6=Sunday)
        const dayKeys = ['monday', 'tuesday', 'wednesday', 'thursday', 'friday'];
        const dayKey = dayKeys[dayIndex];
        if (dayKey && dayIndex >= 0 && dayIndex <= 4) {
          (row as any)[dayKey] = entry;
        }
      });

      rows.push(row);
    });

    // Step 3: Sort by staff name
    this.tableData = rows.sort((a, b) => a.staffName.localeCompare(b.staffName));
  }

  formatCellContent(entry: ScheduleEntry | undefined): string {
    if (!entry) return '';

    if (entry.status === 'NORMAL') {
      // Show times: "08:00 - 16:00"
      return `${entry.startTime} - ${entry.endTime}`;
    } else {
      // Show status: "FREI", "KRANK", etc.
      return entry.status;
    }
  }

  getCellClass(entry: ScheduleEntry | undefined): string {
    if (!entry) return '';
    return `cell-${entry.status.toLowerCase()}`;
  }

  onCellClick(row: StaffWeekRow, day: string): void {
    const entry = row[day as keyof StaffWeekRow] as ScheduleEntry | undefined;

    if (entry) {
      // Edit existing entry
      this.editEntry.emit(entry);
    } else {
      // Create new entry for this staff/day combination
      const dayIndex = this.getDayIndex(day);
      this.createEntry.emit({ staffId: row.staffId, dayOfWeek: dayIndex });
    }
  }

  onDeleteClick(entry: ScheduleEntry, event: Event): void {
    event.stopPropagation(); // Prevent cell click event
    this.deleteEntry.emit(entry);
  }

  private getDayIndex(day: string): number {
    const dayMap: { [key: string]: number } = {
      'monday': 0,
      'tuesday': 1,
      'wednesday': 2,
      'thursday': 3,
      'friday': 4
    };
    return dayMap[day] ?? 0;
  }

  getDayName(day: string): string {
    const dayNames: { [key: string]: string } = {
      'monday': 'Montag',
      'tuesday': 'Dienstag',
      'wednesday': 'Mittwoch',
      'thursday': 'Donnerstag',
      'friday': 'Freitag'
    };
    return dayNames[day] ?? '';
  }
}
