import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

/**
 * Service for displaying user notifications using Material Snackbar
 */
@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  private defaultConfig: MatSnackBarConfig = {
    duration: 3000,
    horizontalPosition: 'end',
    verticalPosition: 'bottom'
  };

  constructor(private snackBar: MatSnackBar) {}

  /**
   * Show success message
   */
  success(message: string, duration: number = 3000): void {
    this.snackBar.open(message, 'OK', {
      ...this.defaultConfig,
      duration,
      panelClass: ['success-snackbar']
    });
  }

  /**
   * Show error message
   */
  error(message: string, duration: number = 5000): void {
    this.snackBar.open(message, 'Schließen', {
      ...this.defaultConfig,
      duration,
      panelClass: ['error-snackbar']
    });
  }

  /**
   * Show warning message
   */
  warning(message: string, duration: number = 4000): void {
    this.snackBar.open(message, 'OK', {
      ...this.defaultConfig,
      duration,
      panelClass: ['warning-snackbar']
    });
  }

  /**
   * Show info message
   */
  info(message: string, duration: number = 3000): void {
    this.snackBar.open(message, 'OK', {
      ...this.defaultConfig,
      duration,
      panelClass: ['info-snackbar']
    });
  }

  /**
   * Show message with custom action
   */
  showWithAction(message: string, action: string, duration: number = 5000): void {
    this.snackBar.open(message, action, {
      ...this.defaultConfig,
      duration
    });
  }
}
