import { Component, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';

/**
 * Base component with automatic subscription cleanup.
 * Extend this component to automatically unsubscribe from observables
 * using the takeUntil pattern with the destroy$ subject.
 *
 * Usage:
 * ```
 * export class MyComponent extends BaseComponent implements OnInit {
 *   ngOnInit() {
 *     this.someService.getData()
 *       .pipe(takeUntil(this.destroy$))
 *       .subscribe(data => { ... });
 *   }
 * }
 * ```
 */
@Component({
  template: ''
})
export abstract class BaseComponent implements OnDestroy {
  protected destroy$ = new Subject<void>();

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }
}
