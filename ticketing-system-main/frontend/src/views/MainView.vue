<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '../stores/user.js';
import { useSessionStore } from '../stores/session';
import { useProjectStore } from '../stores/project';
import { useMembershipStore } from '../stores/membership';
import { usePhaseStore } from '../stores/phase';
import { NSpace, NLayout, NLayoutHeader, NNotificationProvider, NDivider } from 'naive-ui';

import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';
import UserMenu from '../components/atomic-naive-ui/UserMenu.vue';

const sessionStore = useSessionStore();
const membershipStore = useMembershipStore();
const projectStore = useProjectStore();
const phasestore = usePhaseStore();
const userStore = useUserStore();
const { projects } = storeToRefs(projectStore);
const { user } = storeToRefs(userStore);

onMounted(async () => {
  userStore.setEmail(sessionStore.email);
  await userStore.updateUserByEmail();
  await membershipStore.updateMembershipsByEmail();
  await projectStore.updateProjectsByAcceptedMemberships();
  const projectIds = projects.value.map(element => element.id);
  projectIds.forEach(async (projectId) => await phasestore.updatePhasesByProjectId(projectId));
});
</script>

<template>
  <div>
    <!-- Fixed Top Bar -->
    <div style="display: flex; justify-content: space-between; border-bottom: 1px solid #F0F0F0; position: fixed; top: 0; left: 0; right: 0; z-index: 1000; background-color: #008bfd; height: 68px;">
      <div style="display: flex; align-items: center;">
        <img src="../assets/logo.png" alt="logo" style="height: 68px; background-color: #008bfd;">
      </div>
      <div style="padding: 10px 12px 5px 5px; background-color: #008bfd; display: flex; align-items: center;">
        <UserMenu />
      </div>
    </div>
    <!-- Content with Padding to Avoid Overlap -->
    <div style="padding-top: 68px; padding-left: 0px;" id="content-wrapper">
      <n-notification-provider>
        <n-space vertical>
          <n-layout>
            <n-layout has-sider>
              <SidebarMenuVue />
              <RouterView />
            </n-layout>
          </n-layout>
        </n-space>
      </n-notification-provider>
    </div>
  </div>
</template>

<style>
/* Global styles to override Naive UI notification defaults */
.n-notification-container .n-notification {
  --n-color: #1e1e1e !important; /* Dark background */
  --n-text-color: #05cdffe8 !important; /* Match dark-theme text */
  --n-description-text-color: #b0bec5 !important; /* Lighter shade */
  --n-title-text-color: #ffffff !important; /* White for titles */
  --n-icon-color: #05cdffe8 !important; /* Match text */
  --n-close-icon-color: #b0bec5 !important; /* Lighter close */
  --n-close-icon-color-hover: #ffffff !important; /* White on hover */
  --n-close-color-hover: rgba(255, 255, 255, 0.1) !important; /* Subtle hover */
  --n-box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important; /* Darker shadow */
  --n-width: 400px !important; /* Wider */
  --n-padding-top: 12px !important; /* Reduced padding */
  --n-padding-bottom: 12px !important;
  background-color: var(--n-color) !important;
  color: var(--n-text-color) !important;
  border-radius: 8px !important;
}

/* Style the main content area */
.n-notification-container .n-notification .n-notification-main {
  padding-top: var(--n-padding-top) !important;
  padding-bottom: var(--n-padding-bottom) !important;
  margin-left: 12px !important;
  width: calc(100% - 12px) !important;
  box-sizing: border-box !important;
  display: flex !important;
  flex-direction: column !important;
}

/* Position notifications */
.n-notification-container {
  position: fixed !important;
  top: 68px !important; /* Match top bar height */
  right: 20px !important;
  z-index: 3000 !important; /* Above top bar and sidebar */
}

/* Ensure smooth transitions */
.n-notification-container .n-notification {
  transition: all 0.3s ease-in-out !important;
  opacity: 1 !important;
}

/* Debug: Add a class to check if styles are applied */
.n-notification-container.debug .n-notification {
  border: 2px solid red !important; /* Visible debug border */
}
</style>

<!-- <script>
export default {
  mounted() {
    // Debug: Add class to test styling
    setTimeout(() => {
      const container = document.querySelector('.n-notification-container');
      if (container) container.classList.add('debug');
      console.log('Notification container found:', container);
    }, 1000);
  }
}
</script>

<script setup>
import { onMounted } from 'vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '../stores/user.js';
import { useSessionStore } from '../stores/session';
import { useProjectStore } from '../stores/project';
import { useMembershipStore } from '../stores/membership';
import { usePhaseStore } from '../stores/phase';
import { NSpace, NLayout, NLayoutHeader, NNotificationProvider, NDivider } from 'naive-ui';

import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';
import UserMenu from '../components/atomic-naive-ui/UserMenu.vue';

const sessionStore = useSessionStore();
const membershipStore = useMembershipStore();
const projectStore = useProjectStore();
const phasestore = usePhaseStore();
const userStore = useUserStore();
const { projects } = storeToRefs(projectStore);
const { user } = storeToRefs(userStore);

onMounted(async () => {
  userStore.setEmail(sessionStore.email);
  await userStore.updateUserByEmail();
  await membershipStore.updateMembershipsByEmail();
  await projectStore.updateProjectsByAcceptedMemberships();
  const projectIds = projects.value.map(element => element.id);
  projectIds.forEach(async (projectId) => await phasestore.updatePhasesByProjectId(projectId));
});
</script>

<template>
  <div>
    <div style="display: flex; justify-content: space-between; border-bottom: 1px solid #F0F0F0; position: fixed; top: 0; left: 0; right: 0; z-index: 1000; background: linear-gradient(to right, #00c9ff, #92fe9d);
 height: 68px;">
      <div style="display: flex; align-items: center;">
        <img src="../assets/logo_ticketing.png" alt="logo" style="height: 68px; background-color: transparent;">
      </div>
      <div style="padding: 10px 12px 5px 5px; background: transparent; display: flex; align-items: center;">
        <UserMenu />
      </div>
    </div>
    <div style="padding-top: 68px; padding-left: 0px;" id="content-wrapper">
      <n-notification-provider>
        <n-space vertical>
          <n-layout>
            <n-layout has-sider>
              <SidebarMenuVue />
              <RouterView />
            </n-layout>
          </n-layout>
        </n-space>
      </n-notification-provider>
    </div>
  </div>
</template>

<style>
.n-notification-container .n-notification {
  --n-color: #1e1e1e !important; /* Dark background */
  --n-text-color: #05cdffe8 !important; /* Match dark-theme text */
  --n-description-text-color: #b0bec5 !important; /* Lighter shade */
  --n-title-text-color: #ffffff !important; /* White for titles */
  --n-icon-color: #05cdffe8 !important; /* Match text */
  --n-close-icon-color: #b0bec5 !important; /* Lighter close */
  --n-close-icon-color-hover: #ffffff !important; /* White on hover */
  --n-close-color-hover: rgba(255, 255, 255, 0.1) !important; /* Subtle hover */
  --n-box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important; /* Darker shadow */
  --n-width: 400px !important; /* Wider */
  --n-padding-top: 12px !important; /* Reduced padding */
  --n-padding-bottom: 12px !important;
  background-color: var(--n-color) !important;
  color: var(--n-text-color) !important;
  border-radius: 8px !important;
}

.n-notification-container .n-notification .n-notification-main {
  padding-top: var(--n-padding-top) !important;
  padding-bottom: var(--n-padding-bottom) !important;
  margin-left: 12px !important;
  width: calc(100% - 12px) !important;
  box-sizing: border-box !important;
  display: flex !important;
  flex-direction: column !important;
}

.n-notification-container {
  position: fixed !important;
  top: 68px !important; /* Match top bar height */
  right: 20px !important;
  z-index: 3000 !important; /* Above top bar and sidebar */
}

.n-notification-container .n-notification {
  transition: all 0.3s ease-in-out !important;
  opacity: 1 !important;
}

.n-notification-container.debug .n-notification {
  border: 2px solid red !important; /* Visible debug border */
}
</style>

<script>
export default {
  mounted() {
    setTimeout(() => {
      const container = document.querySelector('.n-notification-container');
      if (container) container.classList.add('debug');
      console.log('Notification container found:', container);
    }, 1000);
  }
}
</script> -->