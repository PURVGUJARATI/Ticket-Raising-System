<script setup>
import { useRouter } from 'vue-router';
import { NCard, NTabs, NTabPane, NForm, NFormItemRow, NInput, NButton, NIcon, useNotification } from 'naive-ui';
import { GlassesOutline, Glasses } from '@vicons/ionicons5';
import { ref } from 'vue';
import axios from 'axios';
import { useSessionStore } from '../../stores/session';

const router = useRouter();
const notificationAgent = useNotification();
const sessionStore = useSessionStore();
const signInFormRef = ref(null);
const signUpFormRef = ref(null);

const signUpFormValue = ref({
  userPostData: {
    name: '',
    email: '',
    password: ''
  },
  reenteredPassword: ''
});
const signInFormValue = ref({
  credentials: {
    email: '',
    password: ''
  }
});

const signInRules = {
  credentials: {
    email: {
      required: true,
      trigger: ['blur', 'input'],
      message: 'Please input email'
    },
    password: {
      required: true,
      trigger: ['blur', 'input'],
      message: 'Please input password'
    }
  }
};
const signUpRules = {
  userPostData: {
    name: {
      required: true,
      trigger: ['blur', 'input'],
      message: 'Please input name'
    },
    email: {
      required: true,
      trigger: ['blur', 'input'],
      message: 'Please input email'
    },
    password: {
      required: true,
      trigger: ['blur', 'input'],
      message: 'Please input password'
    }
  },
  reenteredPassword: [
    {
      required: true,
      trigger: ['blur', 'input'],
      message: 'Please input password'
    },
    {
      validator: validatePasswordSame,
      message: 'Password is not same as re-entered password!',
      trigger: ['blur', 'password-input']
    }
  ]
};

function validatePasswordSame(rule, value) {
  return value === signUpFormValue.value.userPostData.password;
}

function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content,
    duration: 3000,
  });
}

async function handleSignInClick() {
  signInFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        const loginResult = await sessionStore.login(
          signInFormValue.value.credentials.email,
          signInFormValue.value.credentials.password
        );
        if (loginResult.isLoginSuccessful) {
          router.push('/Analysis');
        } else {
          sendNotification(
            'Error',
            `Password is incorrect for E-Mail:\n${signInFormValue.value.credentials.email}\n\n${loginResult.message}`
          );
        }
      } catch (error) {
        console.error(error);
        sendNotification('Error', error.response?.data || 'An error occurred');
      }
    }
  });
}

async function handleSignUpClick() {
  signUpFormRef.value?.validate(async (errors) => {
    if (!errors) {
      try {
        const postUserResponse = await axios.post('/users', signUpFormValue.value.userPostData);
        sendNotification(
          'Success',
          `Created your new account with E-Mail:\n${postUserResponse.data.email}\n\nYou will now be redirected to Sign In ...`
        );
        setTimeout(() => {
          router.go();
        }, 5000);
      } catch (error) {
        console.error(error);
        sendNotification('Error', error.response?.data || 'An error occurred');
      }
    }
  });
}
</script>

<template>
  <n-card class="auth-card">
    <div class="logo-container">
      <img src="../../assets/logo_ticketing.png" alt="Logo" class="logo" />
    </div>
    <n-tabs
      class="auth-tabs"
      default-value="signin"
      size="large"
      animated
      pane-style="padding: 20px 0;"
    >
      <n-tab-pane name="signin" tab="Sign In">
        <n-form
          ref="signInFormRef"
          :model="signInFormValue"
          :rules="signInRules"
          class="auth-form"
        >
          <n-form-item-row label="Email" path="credentials.email">
            <n-input
              v-model:value="signInFormValue.credentials.email"
              placeholder="Enter your email"
              clearable
            />
          </n-form-item-row>
          <n-form-item-row label="Password" path="credentials.password">
            <n-input
              type="password"
              v-model:value="signInFormValue.credentials.password"
              show-password-on="click"
              placeholder="Enter your password"
              :maxlength="32"
            >
              <template #password-visible-icon>
                <n-icon :size="16" :component="GlassesOutline" />
              </template>
              <template #password-invisible-icon>
                <n-icon :size="16" :component="Glasses" />
              </template>
            </n-input>
          </n-form-item-row>
          <n-button
            type="primary"
            block
            strong
            @click="handleSignInClick"
            class="auth-button"
          >
            Sign In
          </n-button>
        </n-form>
      </n-tab-pane>
      <n-tab-pane name="signup" tab="Sign Up">
        <n-form
          ref="signUpFormRef"
          :model="signUpFormValue"
          :rules="signUpRules"
          class="auth-form"
        >
          <n-form-item-row label="Name" path="userPostData.name">
            <n-input
              v-model:value="signUpFormValue.userPostData.name"
              placeholder="Enter your name"
              clearable
            />
          </n-form-item-row>
          <n-form-item-row label="Email" path="userPostData.email">
            <n-input
              v-model:value="signUpFormValue.userPostData.email"
              placeholder="Enter your email"
              clearable
            />
          </n-form-item-row>
          <n-form-item-row label="Password" path="userPostData.password">
            <n-input
              type="password"
              v-model:value="signUpFormValue.userPostData.password"
              show-password-on="click"
              placeholder="Enter your password"
              :maxlength="32"
            >
              <template #password-visible-icon>
                <n-icon :size="16" :component="GlassesOutline" />
              </template>
              <template #password-invisible-icon>
                <n-icon :size="16" :component="Glasses" />
              </template>
            </n-input>
          </n-form-item-row>
          <n-form-item-row label="Confirm Password" path="reenteredPassword">
            <n-input
              type="password"
              v-model:value="signUpFormValue.reenteredPassword"
              show-password-on="click"
              placeholder="Re-enter your password"
              :maxlength="32"
            >
              <template #password-visible-icon>
                <n-icon :size="16" :component="GlassesOutline" />
              </template>
              <template #password-invisible-icon>
                <n-icon :size="16" :component="Glasses" />
              </template>
            </n-input>
          </n-form-item-row>
          <n-button
            type="primary"
            block
            strong
            @click="handleSignUpClick"
            class="auth-button"
          >
            Sign Up
          </n-button>
        </n-form>
      </n-tab-pane>
    </n-tabs>
  </n-card>
</template>

<style scoped>
/* Card Styling */
.auth-card {
  background: #ffffff08;
  border: transparent;
  /* backdrop-filter: blur(10px); */
  /* border-radius: 15px; */
  /* border: 1px solid rgba(255, 255, 255, 0.1); */
  /* box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2); */
  padding: 20px; /* Reduced padding to minimize gap */
  width: 100%;
  max-width: 400px;
}

/* Logo */
.logo-container {
  text-align: center;
  margin-bottom: 20px;
}

.logo {
  width: 250px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.3));
}

/* Tabs */
.auth-tabs {
  margin: 0;
}

:deep(.n-tabs .n-tabs-nav) {
  background: transparent;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
}

:deep(.n-tabs .n-tabs-tab) {
  color: #ffffff;
  font-weight: 500;
  transition: color 0.3s ease;
}

:deep(.n-tabs .n-tabs-tab:hover) {
  color: #409eff;
}

:deep(.n-tabs .n-tabs-bar) {
  background: #409eff;
}

/* Form */
.auth-form {
  margin-bottom: 10px; /* Reduced margin to minimize gap */
}

:deep(.n-form-item-row) {
  margin-bottom: 15px; /* Reduced spacing between form items */
}

/* Form Item Label */
:deep(.n-form-item-label) {
  color: #ffffff;
  font-weight: 500;
  font-size: 14px;
}

/* Input */
:deep(.n-input) {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.2);
  color: #ffffff08;
  border-radius: 8px;
  transition: all 0.3s ease;
}

:deep(.n-input:hover),
:deep(.n-input:focus) {
  border-color: #409eff;
  box-shadow: 0 0 8px rgba(64, 158, 255, 0.3);
}

:deep(.n-input .n-input__placeholder) {
  color: rgba(255, 255, 255, 0.5);
}

/* Button */
.auth-button {
  background: #409eff;
  border: none;
  border-radius: 8px;
  height: 40px;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-top: 10px; /* Reduced top margin to minimize gap */
}

.auth-button:hover {
  background: #66b1ff;
  transform: translateY(-2px);
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
}

/* Enhanced Fix for Autofill Background */
:deep(.n-input .n-input__input-el:-webkit-autofill),
:deep(.n-input .n-input__input-el:-webkit-autofill:hover),
:deep(.n-input .n-input__input-el:-webkit-autofill:focus),
:deep(.n-input .n-input__input-el:-webkit-autofill:active) {
  -webkit-text-fill-color: #ffffff !important;
  /* -webkit-box-shadow: 0 0 0px 1000px rgba(255, 255, 255, 0.1) inset !important; */
  transition: background-color 5000s ease-in-out 0s !important;
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}

/* Responsive Design */
@media (max-width: 480px) {
  .auth-card {
    padding: 15px; /* Reduced padding for mobile */
  }

  .logo {
    width: 100px;
  }
}
</style>