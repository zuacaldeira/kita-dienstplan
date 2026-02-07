import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Skeleton loader for schedule cards
 * Shows shimmer cards matching the schedule table layout
 */
@Component({
  selector: 'app-skeleton-schedule',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="schedule-skeleton-container">
      <div class="schedule-skeleton-card" *ngFor="let i of skeletonCards">
        <div class="skeleton-staff-header">
          <div class="skeleton-avatar"></div>
          <div class="skeleton-staff-details">
            <div class="skeleton-line skeleton-name"></div>
            <div class="skeleton-line skeleton-role"></div>
          </div>
        </div>
        <div class="skeleton-week-pills">
          <div class="skeleton-pill" *ngFor="let j of [1,2,3,4,5]"></div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .schedule-skeleton-container {
      width: 100%;
      padding: 16px 0;
    }

    .schedule-skeleton-card {
      background: var(--surface-card);
      border-radius: 16px;
      padding: 24px;
      margin-bottom: 24px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      border: 1px solid var(--border-color-light);
    }

    .skeleton-staff-header {
      display: flex;
      align-items: center;
      gap: 16px;
      margin-bottom: 20px;
      padding-bottom: 16px;
      border-bottom: 2px solid var(--border-color-light);
    }

    .skeleton-avatar {
      width: 56px;
      height: 56px;
      border-radius: 50%;
      flex-shrink: 0;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    .skeleton-staff-details {
      flex: 1;
      display: flex;
      flex-direction: column;
      gap: 8px;
    }

    .skeleton-line {
      height: 16px;
      border-radius: 4px;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    .skeleton-name {
      width: 150px;
      height: 18px;
    }

    .skeleton-role {
      width: 100px;
      height: 14px;
    }

    .skeleton-week-pills {
      display: grid;
      grid-template-columns: repeat(5, 1fr);
      gap: 12px;
    }

    .skeleton-pill {
      height: 90px;
      border-radius: 12px;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    html.dark-theme .skeleton-avatar,
    html.dark-theme .skeleton-line,
    html.dark-theme .skeleton-pill {
      background: linear-gradient(90deg, #2a2a2a 25%, #333 50%, #2a2a2a 75%);
      background-size: 200% 100%;
    }

    @keyframes shimmer {
      0% {
        background-position: -200% 0;
      }
      100% {
        background-position: 200% 0;
      }
    }

    @media (max-width: 959px) {
      .skeleton-week-pills {
        grid-template-columns: repeat(3, 1fr);
      }
    }

    @media (max-width: 599px) {
      .skeleton-week-pills {
        grid-template-columns: 1fr;
      }

      .skeleton-pill {
        height: 80px;
      }
    }
  `]
})
export class SkeletonScheduleComponent {
  @Input() count: number = 5;

  get skeletonCards(): number[] {
    return Array(this.count).fill(0);
  }
}
