import { createApp } from 'vue';
import { createPinia } from 'pinia';
import { useThemeStore } from './stores/theme';
import App from './App.vue';
import router from './router';
import './style.css';

const app = createApp(App);
const pinia = createPinia();
const themeStore = useThemeStore(pinia);

// Initialize theme
themeStore.initTheme();

app.use(pinia);
app.use(router);
app.mount('#app');