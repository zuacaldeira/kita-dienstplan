import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatTableModule } from '@angular/material/table';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { ScheduleEntry } from '../../models/models';

interface StaffWeekRow {
  staffId: number;
  staffName: string;
  staffFirstName: string;
  staffLastName: string;
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
      const fullName = staffEntries[0].staffName;
      const nameParts = this.parseStaffName(fullName);

      const row: StaffWeekRow = {
        staffId: staffId,
        staffName: fullName,
        staffFirstName: nameParts.firstName,
        staffLastName: nameParts.lastName,
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
      // Show times in PDF format: "9:15 17:00" (space-separated, no leading zeros)
      const startTime = this.formatTime(entry.startTime);
      const endTime = this.formatTime(entry.endTime);
      return `${startTime} ${endTime}`;
    } else {
      // Show status: "FREI", "KRANK", etc.
      return entry.status;
    }
  }

  private formatTime(timeStr: string): string {
    // Input: "09:15:00" or "09:15"
    // Output: "9:15" (no leading zero on hour)
    const parts = timeStr.substring(0, 5).split(':');
    const hour = parseInt(parts[0], 10); // Remove leading zero
    const minute = parts[1];
    return `${hour}:${minute}`;
  }

  private parseStaffName(fullName: string): { firstName: string, lastName: string } {
    // Handle various name formats:
    // - "FirstName LastName" -> FirstName | LastName
    // - "LastName, FirstName" -> FirstName | LastName
    // - Single name -> empty | Name

    if (fullName.includes(',')) {
      // Format: "LastName, FirstName"
      const parts = fullName.split(',').map(p => p.trim());
      return {
        lastName: parts[0] || '',
        firstName: parts[1] || ''
      };
    } else {
      // Format: "FirstName LastName" or single name
      const parts = fullName.trim().split(/\s+/);
      if (parts.length >= 2) {
        return {
          firstName: parts[0],
          lastName: parts.slice(1).join(' ')
        };
      } else {
        // Single name - use as last name for consistency with PDFs
        return {
          firstName: '',
          lastName: fullName
        };
      }
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

  /**
   * Get initials from staff name for avatar
   */
  getInitials(firstName: string, lastName: string): string {
    const first = firstName ? firstName.charAt(0).toUpperCase() : '';
    const last = lastName ? lastName.charAt(0).toUpperCase() : '';

    if (first && last) {
      return first + last;
    } else if (last) {
      return last.substring(0, 2).toUpperCase();
    } else if (first) {
      return first.substring(0, 2).toUpperCase();
    }
    return '??';
  }

  /**
   * Get CSS class for day pill based on status
   */
  getDayPillClass(entry: ScheduleEntry | undefined): string {
    if (!entry) {
      return 'pill-empty-state';
    }
    return `pill-${entry.status.toLowerCase()}`;
  }

  /**
   * Get icon for status
   */
  getStatusIcon(status: string): string {
    const iconMap: { [key: string]: string } = {
      'NORMAL': 'schedule',
      'FREI': 'beach_access',
      'KRANK': 'local_hospital',
      'URLAUB': 'flight_takeoff',
      'FORTBILDUNG': 'school'
    };
    return iconMap[status] || 'event';
  }

  /**
   * Calculate work hours from start and end time
   */
  getWorkHours(entry: ScheduleEntry): string {
    if (entry.status !== 'NORMAL' || !entry.startTime || !entry.endTime) {
      return '';
    }

    try {
      const start = this.parseTimeToMinutes(entry.startTime);
      const end = this.parseTimeToMinutes(entry.endTime);
      const diffMinutes = end - start;

      if (diffMinutes <= 0) return '';

      const hours = Math.floor(diffMinutes / 60);
      const minutes = diffMinutes % 60;

      if (minutes === 0) {
        return `${hours}h`;
      } else {
        return `${hours}h ${minutes}min`;
      }
    } catch (e) {
      return '';
    }
  }

  private parseTimeToMinutes(timeStr: string): number {
    // Input: "09:15:00" or "09:15"
    const parts = timeStr.split(':');
    const hours = parseInt(parts[0], 10);
    const minutes = parseInt(parts[1], 10);
    return hours * 60 + minutes;
  }
}
