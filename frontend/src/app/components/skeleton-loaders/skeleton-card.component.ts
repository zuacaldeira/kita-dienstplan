import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

/**
 * Skeleton loader for card components
 * Shows a shimmer animation while content is loading
 */
@Component({
  selector: 'app-skeleton-card',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="skeleton-card" [style.height.px]="height">
      <div class="skeleton-header">
        <div class="skeleton-circle" *ngIf="showAvatar"></div>
        <div class="skeleton-lines">
          <div class="skeleton-line skeleton-line-title"></div>
          <div class="skeleton-line skeleton-line-subtitle" *ngIf="showSubtitle"></div>
        </div>
      </div>
      <div class="skeleton-content" *ngIf="showContent">
        <div class="skeleton-line" *ngFor="let line of contentLines"></div>
      </div>
    </div>
  `,
  styles: [`
    .skeleton-card {
      background: var(--surface-card);
      border-radius: 12px;
      padding: 20px;
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
      border: 1px solid var(--border-color-light);
    }

    .skeleton-header {
      display: flex;
      gap: 12px;
      align-items: center;
      margin-bottom: 16px;
    }

    .skeleton-circle {
      width: 48px;
      height: 48px;
      border-radius: 50%;
      flex-shrink: 0;
    }

    .skeleton-lines {
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

    html.dark-theme .skeleton-line {
      background: linear-gradient(90deg, #2a2a2a 25%, #333 50%, #2a2a2a 75%);
      background-size: 200% 100%;
    }

    .skeleton-circle {
      background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
      background-size: 200% 100%;
      animation: shimmer 1.5s infinite;
    }

    html.dark-theme .skeleton-circle {
      background: linear-gradient(90deg, #2a2a2a 25%, #333 50%, #2a2a2a 75%);
      background-size: 200% 100%;
    }

    .skeleton-line-title {
      width: 60%;
      height: 20px;
    }

    .skeleton-line-subtitle {
      width: 40%;
      height: 14px;
    }

    .skeleton-content {
      display: flex;
      flex-direction: column;
      gap: 12px;
    }

    .skeleton-content .skeleton-line {
      width: 100%;
    }

    .skeleton-content .skeleton-line:last-child {
      width: 70%;
    }

    @keyframes shimmer {
      0% {
        background-position: -200% 0;
      }
      100% {
        background-position: 200% 0;
      }
    }
  `]
})
export class SkeletonCardComponent {
  @Input() height: number = 120;
  @Input() showAvatar: boolean = false;
  @Input() showSubtitle: boolean = true;
  @Input() showContent: boolean = true;
  @Input() contentLinesCount: number = 3;

  get contentLines(): number[] {
    return Array(this.contentLinesCount).fill(0);
  }
}
