import { defineStore } from 'pinia';

interface ThemeState {
  isDark: boolean;
}

export const useThemeStore = defineStore('theme', {
  state: (): ThemeState => ({
    isDark: false // Default to light mode
  }),
  actions: {
    toggleTheme() {
      this.isDark = !this.isDark;
      this.applyTheme();
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
    },
    initTheme() {
      // Check localStorage first
      const savedTheme = localStorage.getItem('theme');
      if (savedTheme) {
        this.isDark = savedTheme === 'dark';
      } else {
        // Fallback to system preference
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        this.isDark = prefersDark;
      }
      this.applyTheme();
    },
    applyTheme() {
      const html = document.documentElement;
      if (this.isDark) {
        html.classList.add('dark');
      } else {
        html.classList.remove('dark');
      }
    }
  }
});