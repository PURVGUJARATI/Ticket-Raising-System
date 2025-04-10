<script setup>
import { NSpace, NNotificationProvider, darkTheme, NConfigProvider } from 'naive-ui';
import SignInForm from '../components/atomic-naive-ui/SignInForm.vue';
</script>

<template>
  <n-config-provider :theme="darkTheme">
    <router-view />
  </n-config-provider>
  <!-- Full-Screen Centered Wrapper -->
  <div class="page-wrapper">
    <div class="auth-container">
      <!-- Left: Image -->
      <div class="image-section">
        <img src="../assets/newdesign.jpg" alt="Design Image" />
      </div>

      <!-- Right: Form -->
      <div class="form-section">
        <div class="form-wrapper">
          <n-notification-provider>
            <SignInForm />
          </n-notification-provider>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* Main2 - Full-Screen Centering */
.page-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background-color: #f8f8f8; /* Light background for contrast */
}

/* Combined Container */
.auth-container {
  display: flex;
  width: 1200px;
  height: 550px; /* ✅ Fixed height */
  /* box-shadow: 0 0 20px rgba(0, 0, 0, 0.1); */
  border-radius: 15px 0 0 15px;
  overflow: hidden;
  /* background-color: white; */
}


/* Left: Image */
.image-section {
  flex: 2.0; /* Slightly more space */
  /* background-color: #eaeaea; */
  display: flex;
  justify-content: center;
  align-items: center;
}

.image-section img {
  max-width: 100%;
  height: 100%;           /* ✅ Make image fill the height */
  object-fit: fill;      /* Or "fill" if you want it stretched */
  border-radius: 15px 0 0 15px;
}


/* Right: Form */
.form-section {
  flex: 1;
  /* background-color: white; */
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 40px;
}

.form-wrapper {
  width: 100%;
  max-width: 350px;
}

.logo {
  width: 100px;
  margin-bottom: 20px;
}
</style>

