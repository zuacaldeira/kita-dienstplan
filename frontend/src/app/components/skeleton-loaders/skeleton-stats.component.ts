import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Skeleton loader for dashboard stats cards
 * Shows 3 shimmer cards in a grid
 */
@Component({
  selector: 'app-skeleton-stats',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="stats-skeleton-grid">
      <div class="stat-skeleton-card" *ngFor="let i of [1, 2, 3]">
        <div class="skeleton-icon"></div>
        <div class="skeleton-value"></div>
        <div class="skeleton-label"></div>
      </div>
    </div>
  `,
  styles: [`
    .stats-skeleton-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
      gap: 24px;
      margin-bottom: 32px;
    }

    .stat-skeleton-card {
      background: var(--surface-card);
      border-radius: 12px;
      padding: 32px 24px;
      text-align: center;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      border: 1px solid var(--border-color-light);
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 16px;
    }

    .skeleton-icon {
      width: 64px;
      height: 64px;
      border-radius: 50%;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    .skeleton-value {
      width: 80px;
      height: 48px;
      border-radius: 8px;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    .skeleton-label {
      width: 140px;
      height: 20px;
      border-radius: 4px;
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    html.dark-theme .skeleton-icon,
    html.dark-theme .skeleton-value,
    html.dark-theme .skeleton-label {
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

    @media (max-width: 768px) {
      .stats-skeleton-grid {
        grid-template-columns: 1fr;
      }
    }
  `]
})
export class SkeletonStatsComponent {}
