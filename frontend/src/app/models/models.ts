// Authentication models
export interface LoginRequest {
  username: string;
  password: string;
}

export interface AuthResponse {
  token: string;
  username: string;
  fullName: string;
  message: string;
}

export interface User {
  id?: number;
  username: string;
  fullName: string;
  email?: string;
}

// Staff models
export interface Staff {
  id: number;
  firstName: string;
  lastName: string;
  fullName: string;
  role: string;
  group?: Group;
  employmentType: string;
  weeklyHours?: number;
  email?: string;
  phone?: string;
  isPraktikant: boolean;
  isActive: boolean;
  hireDate?: string;
  terminationDate?: string;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

// Group models
export interface Group {
  id: number;
  name: string;
  description?: string;
  isActive: boolean;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

// Schedule models
export interface WeeklySchedule {
  id: number;
  weekNumber: number;
  year: number;
  startDate: string;
  endDate: string;
  notes?: string;
  createdBy?: string;
  updatedBy?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface ScheduleEntry {
  id: number;
  weeklyScheduleId: number;
  staffId: number;
  staffName: string;
  staffRole: string;
  groupName?: string;
  dayOfWeek: number;
  workDate: string;
  startTime: string;
  endTime: string;
  status: string;
  workingHoursMinutes: number;
  breakMinutes: number;
  workingHoursFormatted: string;
  breakTimeFormatted: string;
  notes?: string;
  createdBy?: string;
  updatedBy?: string;
}

export interface CreateScheduleEntryRequest {
  weeklyScheduleId: number;
  staffId: number;
  dayOfWeek: number;
  workDate: string;
  startTime: string;
  endTime: string;
  status: string;
  notes?: string;
}

export interface UpdateScheduleEntryRequest {
  startTime?: string;
  endTime?: string;
  status?: string;
  notes?: string;
}

export interface DailyTotal {
  dayOfWeek: number;
  workDate: string;
  dayName: string;
  totalMinutesWithoutPraktikanten: number;
  totalMinutesWithPraktikanten: number;
  hoursWithoutPraktikanten: string;
  hoursWithPraktikanten: string;
  staffCountWithoutPraktikanten: number;
  totalStaffCount: number;
}
