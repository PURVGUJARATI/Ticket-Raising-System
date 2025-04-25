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

.dark-theme {
  --bg-color: #008bfd;
  --text-color: #05cdffe8;
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
  transition: background-color 0.3s, color 0.3s;
}
</style>