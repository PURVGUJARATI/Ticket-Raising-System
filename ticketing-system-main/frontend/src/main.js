import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import router from './router';
import './axios';
import '../src/components/atomic-naive-ui/SidebarMenu.vue';
// import './assets/main.css';

const app = createApp(App);

app.use(router);
app.use(createPinia());

app.mount('#app');
