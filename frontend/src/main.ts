import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import './style.css';
import { getSocketService } from './services/socket.service';
import { useThemeStore } from './stores/theme';

const app = createApp(App);

// Setup Pinia and Router
const pinia = createPinia();
app.use(pinia);
app.use(router);

// Init theme (after Pinia)
const themeStore = useThemeStore();
themeStore.initTheme();

// Mount the app
app.mount('#app');

// Connect socket after mounting
const socketService = getSocketService();
socketService.connect();
