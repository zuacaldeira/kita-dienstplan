import { Injectable, Renderer2, RendererFactory2, signal } from '@angular/core';

/**
 * Service to manage theme switching between light and dark modes
 * Persists theme preference in localStorage
 */
@Injectable({
  providedIn: 'root'
})
export class ThemeService {
  private renderer: Renderer2;
  private readonly STORAGE_KEY = 'kita-theme';

  // Signal for reactive theme state
  isDarkMode = signal<boolean>(false);

  constructor(rendererFactory: RendererFactory2) {
    this.renderer = rendererFactory.createRenderer(null, null);
    this.initializeTheme();
  }

  /**
   * Initialize theme from localStorage or system preference
   */
  private initializeTheme(): void {
    const savedTheme = localStorage.getItem(this.STORAGE_KEY);

    if (savedTheme === 'dark') {
      this.enableDarkMode();
    } else if (savedTheme === 'light') {
      this.enableLightMode();
    } else {
      // Check system preference
      const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
      if (prefersDark) {
        this.enableDarkMode();
      }
    }
  }

  /**
   * Toggle between light and dark themes
   */
  toggleTheme(): void {
    if (this.isDarkMode()) {
      this.enableLightMode();
    } else {
      this.enableDarkMode();
    }
  }

  /**
   * Enable dark mode
   */
  enableDarkMode(): void {
    const htmlElement = document.documentElement;
    this.renderer.addClass(htmlElement, 'dark-theme');
    this.isDarkMode.set(true);
    localStorage.setItem(this.STORAGE_KEY, 'dark');
  }

  /**
   * Enable light mode
   */
  enableLightMode(): void {
    const htmlElement = document.documentElement;
    this.renderer.removeClass(htmlElement, 'dark-theme');
    this.isDarkMode.set(false);
    localStorage.setItem(this.STORAGE_KEY, 'light');
  }

  /**
   * Set specific theme
   */
  setTheme(theme: 'light' | 'dark'): void {
    if (theme === 'dark') {
      this.enableDarkMode();
    } else {
      this.enableLightMode();
    }
  }

  /**
   * Get current theme
   */
  getCurrentTheme(): 'light' | 'dark' {
    return this.isDarkMode() ? 'dark' : 'light';
  }
}
