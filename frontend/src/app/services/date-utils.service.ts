import { Injectable } from '@angular/core';

/**
 * Date utility service to handle date calculations and formatting.
 * Replaces Date.prototype pollution anti-pattern.
 */
@Injectable({
  providedIn: 'root'
})
export class DateUtilsService {

  /**
   * Get ISO week number for a given date
   * ISO week starts on Monday, week 1 is the first week with Thursday
   */
  getWeekNumber(date: Date): number {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    const dayNum = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - dayNum);
    const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
    return Math.ceil((((d.getTime() - yearStart.getTime()) / 86400000) + 1) / 7);
  }

  /**
   * Get the year for ISO week calculation
   * Note: Week 1 might belong to previous year
   */
  getWeekYear(date: Date): number {
    const d = new Date(Date.UTC(date.getFullYear(), date.getMonth(), date.getDate()));
    const dayNum = d.getUTCDay() || 7;
    d.setUTCDate(d.getUTCDate() + 4 - dayNum);
    return d.getUTCFullYear();
  }

  /**
   * Get Monday of the week for a given year and ISO week number
   */
  getMondayOfWeek(year: number, week: number): Date {
    const simple = new Date(year, 0, 1 + (week - 1) * 7);
    const dayOfWeek = simple.getDay();
    const isoWeekStart = simple;

    if (dayOfWeek <= 4) {
      isoWeekStart.setDate(simple.getDate() - simple.getDay() + 1);
    } else {
      isoWeekStart.setDate(simple.getDate() + 8 - simple.getDay());
    }

    return isoWeekStart;
  }

  /**
   * Get all 7 days of the week (Monday to Sunday) for given year and week
   */
  getWeekDays(year: number, week: number): Date[] {
    const monday = this.getMondayOfWeek(year, week);
    const days: Date[] = [];

    for (let i = 0; i < 7; i++) {
      const day = new Date(monday);
      day.setDate(monday.getDate() + i);
      days.push(day);
    }

    return days;
  }

  /**
   * Format date as YYYY-MM-DD (ISO date format)
   */
  formatISODate(date: Date): string {
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  /**
   * Format date in German format (DD.MM.YYYY)
   */
  formatGermanDate(date: Date): string {
    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();
    return `${day}.${month}.${year}`;
  }

  /**
   * Get day name in German
   */
  getGermanDayName(date: Date, short: boolean = false): string {
    const dayNames = ['Sonntag', 'Montag', 'Dienstag', 'Mittwoch', 'Donnerstag', 'Freitag', 'Samstag'];
    const shortNames = ['So', 'Mo', 'Di', 'Mi', 'Do', 'Fr', 'Sa'];

    const dayIndex = date.getDay();
    return short ? shortNames[dayIndex] : dayNames[dayIndex];
  }

  /**
   * Get month name in German
   */
  getGermanMonthName(date: Date, short: boolean = false): string {
    const monthNames = [
      'Januar', 'Februar', 'März', 'April', 'Mai', 'Juni',
      'Juli', 'August', 'September', 'Oktober', 'November', 'Dezember'
    ];
    const shortNames = [
      'Jan', 'Feb', 'Mär', 'Apr', 'Mai', 'Jun',
      'Jul', 'Aug', 'Sep', 'Okt', 'Nov', 'Dez'
    ];

    const monthIndex = date.getMonth();
    return short ? shortNames[monthIndex] : monthNames[monthIndex];
  }

  /**
   * Parse ISO date string (YYYY-MM-DD) to Date object
   */
  parseISODate(dateString: string): Date {
    const parts = dateString.split('-');
    if (parts.length !== 3) {
      throw new Error('Invalid date format. Expected YYYY-MM-DD');
    }
    return new Date(parseInt(parts[0]), parseInt(parts[1]) - 1, parseInt(parts[2]));
  }

  /**
   * Get current week number and year
   */
  getCurrentWeek(): { year: number, week: number } {
    const now = new Date();
    return {
      year: this.getWeekYear(now),
      week: this.getWeekNumber(now)
    };
  }

  /**
   * Navigate to next week
   */
  getNextWeek(year: number, week: number): { year: number, week: number } {
    const monday = this.getMondayOfWeek(year, week);
    monday.setDate(monday.getDate() + 7);
    return {
      year: this.getWeekYear(monday),
      week: this.getWeekNumber(monday)
    };
  }

  /**
   * Navigate to previous week
   */
  getPreviousWeek(year: number, week: number): { year: number, week: number } {
    const monday = this.getMondayOfWeek(year, week);
    monday.setDate(monday.getDate() - 7);
    return {
      year: this.getWeekYear(monday),
      week: this.getWeekNumber(monday)
    };
  }

  /**
   * Check if two dates are the same day
   */
  isSameDay(date1: Date, date2: Date): boolean {
    return date1.getFullYear() === date2.getFullYear() &&
           date1.getMonth() === date2.getMonth() &&
           date1.getDate() === date2.getDate();
  }

  /**
   * Get day of week (1 = Monday, 7 = Sunday) - ISO standard
   */
  getISODayOfWeek(date: Date): number {
    const day = date.getDay();
    return day === 0 ? 7 : day;
  }

  /**
   * Format time from HH:mm:ss to HH:mm
   */
  formatTime(time: string): string {
    if (!time) return '';
    return time.substring(0, 5);
  }

  /**
   * Calculate duration in hours between two time strings (HH:mm)
   */
  calculateDuration(startTime: string, endTime: string): number {
    const start = this.timeToMinutes(startTime);
    const end = this.timeToMinutes(endTime);
    return (end - start) / 60;
  }

  /**
   * Convert time string (HH:mm) to minutes since midnight
   */
  private timeToMinutes(time: string): number {
    const parts = time.split(':');
    return parseInt(parts[0]) * 60 + parseInt(parts[1]);
  }

  /**
   * Check if two time ranges overlap
   */
  timesOverlap(start1: string, end1: string, start2: string, end2: string): boolean {
    const s1 = this.timeToMinutes(start1);
    const e1 = this.timeToMinutes(end1);
    const s2 = this.timeToMinutes(start2);
    const e2 = this.timeToMinutes(end2);

    return s1 < e2 && s2 < e1;
  }

  /**
   * Get start and end dates for a given week
   */
  getWeekDates(year: number, week: number): { startDate: string, endDate: string } {
    const monday = this.getMondayOfWeek(year, week);
    const sunday = new Date(monday);
    sunday.setDate(monday.getDate() + 6);

    return {
      startDate: this.formatISODate(monday),
      endDate: this.formatISODate(sunday)
    };
  }

  /**
   * Get date from year, week number, and day of week (1-7, Monday-Sunday)
   */
  getDateFromWeekAndDay(year: number, week: number, dayOfWeek: number): string {
    const monday = this.getMondayOfWeek(year, week);
    const targetDate = new Date(monday);
    targetDate.setDate(monday.getDate() + (dayOfWeek - 1));
    return this.formatISODate(targetDate);
  }
}
