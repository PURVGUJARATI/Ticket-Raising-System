<script setup>
import { NSpace, NNotificationProvider, darkTheme, NConfigProvider } from 'naive-ui';
import SignInForm from '../components/atomic-naive-ui/SignInForm.vue';
</script>

<template>
  <n-config-provider :theme="darkTheme">
    <div class="page-wrapper">
      <div class="auth-container">
        <!-- Left: Image Section -->
        <div class="image-section">
          <img src="../assets/login.jpg" alt="Tech Design" class="hero-image" />
          <div class="image-overlay">
            <h1 class="welcome-text">Welcome to TRS</h1>
            <p class="subtitle">Access your account to explore the future.</p>
          </div>
        </div>

        <!-- Right: Form Section -->
        <div class="form-section">
          <div class="form-wrapper">
            <!-- <img src="../assets/logo_ticketing.png" alt="Logo" class="logo" /> -->
            <n-notification-provider>
              <SignInForm />
            </n-notification-provider>
          </div>
        </div>
      </div>
    </div>
  </n-config-provider>
</template>

<style scoped>
/* Full-Screen Centering */
.page-wrapper {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  overflow: hidden;
}

/* Auth Container */
.auth-container {
  display: flex;
  width: 1000px;
  height: flex;
  background: #ffffff08;
  backdrop-filter: blur(10px);
  border-radius: 20px;
  overflow: hidden;
}

/* Left: Image Section */
.image-section {
  flex: 1.5;
  position: relative;
  overflow: hidden;
}

.hero-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 20px 0 0 20px;
  filter: brightness(0.7);
  transition: transform 0.5s ease;
}

.image-section:hover .hero-image {
  transform: scale(1.05);
}

.image-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  text-align: center;
  padding: 20px;
  background: rgba(0, 0, 0, 0.4);
}

.welcome-text {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 10px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.5);
}

.subtitle {
  font-size: 1.1rem;
  opacity: 0.8;
}

/* Right: Form Section */
.form-section {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 30px;
  background: #ffffff0a;
}

.form-wrapper {
  width: 100%;
  max-width: 400px;
  text-align: center;
}

.logo {
  width: 80px;
  margin-bottom: 20px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

.form-title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #ffffff;
  margin-bottom: 10px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

/* Animation for Form Inputs (Naive UI components) */
:deep(.n-input),
:deep(.n-button) {
  transition: all 0.3s ease;
}

:deep(.n-input:hover),
:deep(.n-button:hover) {
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

/* Responsive Design */
@media (max-width: 768px) {
  .auth-container {
    flex-direction: column;
    width: 90%;
    height: auto;
    min-height: 600px;
  }

  .image-section {
    flex: none;
    height: 200px;
    border-radius: 20px 20px 0 0;
  }

  .hero-image {
    border-radius: 20px 20px 0 0;
  }

  .form-section {
    flex: none;
    padding: 20px;
  }

  .welcome-text {
    font-size: 1.8rem;
  }

  .subtitle {
    font-size: 0.9rem;
  }
}
</style>