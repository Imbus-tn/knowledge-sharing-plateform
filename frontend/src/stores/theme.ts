// stores/theme.ts
import { defineStore } from 'pinia';

interface ThemeState {
  isDark: boolean;
}

export const useThemeStore = defineStore('theme', {
  state: (): ThemeState => ({
    isDark: false
  }),
  actions: {
    toggleTheme() {
      this.isDark = !this.isDark;
      this.applyTheme();
      localStorage.setItem('theme', this.isDark ? 'dark' : 'light');
    },
    initTheme() {
      const savedTheme = localStorage.getItem('theme');
      if (savedTheme) {
        this.isDark = savedTheme === 'dark';
      } else {
        const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
        this.isDark = prefersDark;
      }
    },
    applyTheme(isAuthenticatedPage: boolean = true) {
      const html = document.documentElement;
      if (!isAuthenticatedPage) {
        html.classList.remove('dark');
        return;
      }
      
      if (this.isDark) {
        html.classList.add('dark');
      } else {
        html.classList.remove('dark');
      }
    }
  }
});