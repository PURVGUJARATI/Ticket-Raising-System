<script setup>
import { ref, onMounted } from 'vue';
import { RouterView } from 'vue-router';

const isDark = ref(false);

const toggleTheme = () => {
  isDark.value = !isDark.value;
  document.documentElement.classList.toggle('dark-theme', isDark.value);
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light');
};

onMounted(() => {
  const saved = localStorage.getItem('theme');
  if (saved === 'dark') {
    isDark.value = true;
    document.documentElement.classList.add('dark-theme');
  }
});
</script>

<template>
  <div class="app-wrapper">
    <RouterView />
  </div>
</template>

<style>
:root {
  --bg-color: #ffffff;
  --text-color: #000000;
  --card-bg: #f5f5f5;
  --sidebar-bg: linear-gradient(180deg, #4f46e5, #22c1c3);
  --chart-bg: #ffffff;
  --primary-color: #4f46e5;
}

.dark-theme {
  --bg-color: #121212;
  --text-color: #e0e0e0;
  --card-bg: #1e1e1e;
  --sidebar-bg: linear-gradient(180deg, #2c2c2c, #121212);
  --chart-bg: #2c2c2c;
  --primary-color: #6366f1;
}

body {
  margin: 0;
  padding: 0;
  background-color: var(--bg-color);
  color: var(--text-color);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  transition: background-color 0.3s, color 0.3s;
}

.app-wrapper {
  background-color: var(--bg-color);
  color: var(--text-color);
  min-height: 100vh;
}
::v-deep(.n-menu-item-content:hover) {
  background-color: transparent !important;
}


</style>
