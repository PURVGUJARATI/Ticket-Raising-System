<script setup>
import { onMounted, ref, computed } from 'vue';
import { storeToRefs } from 'pinia';
import { useUserStore } from '../stores/user.js';
import { useSessionStore } from '../stores/session';
import { useProjectStore } from '../stores/project';
import { useMembershipStore } from '../stores/membership';
import { usePhaseStore } from '../stores/phase';
import { useNotificationStore } from '../stores/notification';
import { NotificationsOutline } from '@vicons/ionicons5';
import { NSpace, NLayout, NLayoutHeader, NNotificationProvider, NBadge, NDropdown, NIcon } from 'naive-ui';

import SidebarMenuVue from '../components/atomic-naive-ui/SidebarMenu.vue';
import UserMenu from '../components/atomic-naive-ui/UserMenu.vue';
import { RouterLink } from 'vue-router';

const sessionStore = useSessionStore();
const membershipStore = useMembershipStore();
const projectStore = useProjectStore();
const phasestore = usePhaseStore();
const userStore = useUserStore();
const notificationStore = useNotificationStore();

const { projects } = storeToRefs(projectStore);
const { notifications } = storeToRefs(notificationStore);
const { user } = storeToRefs(userStore);

const showDropdown = ref(false);

onMounted(async () => {
  userStore.setEmail(sessionStore.email);
  await userStore.updateUserByEmail();
  await membershipStore.updateMembershipsByEmail();
  await projectStore.updateProjectsByAcceptedMemberships();
  const projectIds = projects.value.map(element => element.id);
  projectIds.forEach(async (projectId) => await phasestore.updatePhasesByProjectId(projectId));

  // ⭐ Fetch Notifications
  await notificationStore.fetchNotifications();
});

// Create dropdown options from notifications
const dropdownOptions = computed(() => {
  console.log('Notifications:', notifications.value);
  const options = notifications.value.map((notification, index) => {
    console.log(`Processing notification ${index}:`, notification);
    return {
      label: notification.message || notification.text || notification.content || `Notification ${index + 1}`,
      key: notification.id || index
    };
  });
  console.log('Dropdown options:', options);
  return options;
});

async function handleSelect(key) {
  console.log('Selected notification:', key);
  try {
    await notificationStore.markAsRead(key);
  } catch (error) {
    console.error('Error marking notification as read:', error);
  }
}
</script>

<template>
  <div>
    <div style="display: flex; justify-content: space-between; border-bottom: 1px solid #F0F0F0; position: fixed; top: 0; left: 0; right: 0; z-index: 1000; background: linear-gradient(to right, #00c9ff, #92fe9d);
 height: 68px;">      
    <div style="display: flex; align-items: center;">
        <RouterLink to="/Analysis">
          <img src="../assets/logo_ticketing.png" alt="logo" style="height: 68px; position: relative; top: 5px; background-color: transparent;">
        </RouterLink>
      </div>
      <div style="padding: 10px 12px 5px 5px; background: transparent; display: flex; align-items: center;">
        
        <n-badge :value="notifications.length" v-if="notifications.length > 0">
          <n-dropdown style="width: fit-content; height: fit-content; color: 1e1e1e;"
            trigger="click"
            :options="dropdownOptions"
            @select="handleSelect"
          >
            <n-icon size="28" >
              <NotificationsOutline />
            </n-icon>
          </n-dropdown>
        </n-badge>
          <div style="margin-left: 15px;">

        <UserMenu />
      </div>
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
  --n-color: #1e1e1e !important;
  --n-text-color: #05cdffe8 !important;
  --n-description-text-color: #b0bec5 !important;
  --n-title-text-color: #ffffff !important;
  --n-icon-color: #05cdffe8 !important;
  --n-close-icon-color: #b0bec5 !important;
  --n-close-icon-color-hover: #ffffff !important;
  --n-close-color-hover: rgba(255, 255, 255, 0.1) !important;
  --n-box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3) !important;
  --n-width: 400px !important;
  --n-padding-top: 12px !important;
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
  top: 68px !important; 
  right: 20px !important;
  z-index: 3000 !important;
}

.n-notification-container .n-notification {
  transition: all 0.3s ease-in-out !important;
  opacity: 1 !important;
}

.n-notification-container.debug .n-notification {
  border: 2px solid rgb(209, 197, 197) !important;
}

.n-dropdown-menu
{
  background-color: #1e1e1e !important;
  --n-option-color-hover: #1e1e1e !important;
}

.n-dropdown-menu .n-dropdown-option .n-dropdown-option-body {   
    color: rgb(197, 199, 202);
    --n-option-text-color-hover: rgb(243, 243, 245);
}

.n-badge
{
  --n-color: #37373a !important; 
}

.n-badge-sup
{
  color: #e9e5e5 !important;
}

@media (max-width: 900px) {
  .n-badge
  {
    position: absolute !important;
    top: 20px !important;
    right: 68px !important;
  }
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
</style> -->