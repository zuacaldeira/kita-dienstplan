import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  Staff,
  Group,
  WeeklySchedule,
  ScheduleEntry,
  CreateScheduleEntryRequest,
  UpdateScheduleEntryRequest,
  DailyTotal
} from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  // Staff endpoints
  getAllStaff(): Observable<Staff[]> {
    return this.http.get<Staff[]>(`${this.baseUrl}/staff`);
  }

  getActiveStaff(): Observable<Staff[]> {
    return this.http.get<Staff[]>(`${this.baseUrl}/staff/active`);
  }

  getStaffById(id: number): Observable<Staff> {
    return this.http.get<Staff>(`${this.baseUrl}/staff/${id}`);
  }

  getStaffByGroup(groupId: number): Observable<Staff[]> {
    return this.http.get<Staff[]>(`${this.baseUrl}/staff/group/${groupId}`);
  }

  createStaff(staff: Partial<Staff>): Observable<Staff> {
    return this.http.post<Staff>(`${this.baseUrl}/staff`, staff);
  }

  updateStaff(id: number, staff: Partial<Staff>): Observable<Staff> {
    return this.http.put<Staff>(`${this.baseUrl}/staff/${id}`, staff);
  }

  deleteStaff(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/staff/${id}`);
  }

  // Group endpoints
  getAllGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.baseUrl}/age-groups`);
  }

  getActiveGroups(): Observable<Group[]> {
    return this.http.get<Group[]>(`${this.baseUrl}/age-groups/active`);
  }

  getGroupById(id: number): Observable<Group> {
    return this.http.get<Group>(`${this.baseUrl}/age-groups/${id}`);
  }

  createGroup(group: Partial<Group>): Observable<Group> {
    return this.http.post<Group>(`${this.baseUrl}/age-groups`, group);
  }

  updateGroup(id: number, group: Partial<Group>): Observable<Group> {
    return this.http.put<Group>(`${this.baseUrl}/age-groups/${id}`, group);
  }

  deleteGroup(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/age-groups/${id}`);
  }

  // Weekly Schedule endpoints
  getAllWeeklySchedules(): Observable<WeeklySchedule[]> {
    return this.http.get<WeeklySchedule[]>(`${this.baseUrl}/weekly-schedules`);
  }

  getWeeklyScheduleById(id: number): Observable<WeeklySchedule> {
    return this.http.get<WeeklySchedule>(`${this.baseUrl}/weekly-schedules/${id}`);
  }

  getWeeklyScheduleByWeek(year: number, week: number): Observable<WeeklySchedule> {
    return this.http.get<WeeklySchedule>(`${this.baseUrl}/weekly-schedules/week/${year}/${week}`);
  }

  createWeeklySchedule(schedule: Partial<WeeklySchedule>): Observable<WeeklySchedule> {
    return this.http.post<WeeklySchedule>(`${this.baseUrl}/weekly-schedules`, schedule);
  }

  updateWeeklySchedule(id: number, schedule: Partial<WeeklySchedule>): Observable<WeeklySchedule> {
    return this.http.put<WeeklySchedule>(`${this.baseUrl}/weekly-schedules/${id}`, schedule);
  }

  deleteWeeklySchedule(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/weekly-schedules/${id}`);
  }

  // Schedule Entry endpoints
  getWeekSchedule(year: number, week: number): Observable<ScheduleEntry[]> {
    return this.http.get<ScheduleEntry[]>(`${this.baseUrl}/schedules/week/${year}/${week}`);
  }

  getStaffWeekSchedule(staffId: number, year: number, week: number): Observable<ScheduleEntry[]> {
    return this.http.get<ScheduleEntry[]>(`${this.baseUrl}/schedules/staff/${staffId}/week/${year}/${week}`);
  }

  getDateSchedule(date: string): Observable<ScheduleEntry[]> {
    return this.http.get<ScheduleEntry[]>(`${this.baseUrl}/schedules/date/${date}`);
  }

  getWhoIsWorking(date: string, time: string): Observable<ScheduleEntry[]> {
    const params = new HttpParams()
      .set('date', date)
      .set('time', time);
    return this.http.get<ScheduleEntry[]>(`${this.baseUrl}/schedules/on-duty`, { params });
  }

  getDailyTotals(year: number, week: number): Observable<DailyTotal[]> {
    return this.http.get<DailyTotal[]>(`${this.baseUrl}/schedules/daily-totals/${year}/${week}`);
  }

  createScheduleEntry(entry: CreateScheduleEntryRequest): Observable<ScheduleEntry> {
    return this.http.post<ScheduleEntry>(`${this.baseUrl}/schedules/entries`, entry);
  }

  updateScheduleEntry(id: number, entry: UpdateScheduleEntryRequest): Observable<ScheduleEntry> {
    return this.http.put<ScheduleEntry>(`${this.baseUrl}/schedules/entries/${id}`, entry);
  }

  deleteScheduleEntry(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/schedules/entries/${id}`);
  }
}
