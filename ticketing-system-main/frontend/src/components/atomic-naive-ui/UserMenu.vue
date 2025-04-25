<script setup>
import { useUserStore } from '../../stores/user';
import { useSessionStore } from '../../stores/session';
import { h, ref, computed, onMounted } from "vue";
import { NIcon, NDropdown, NAvatar, NModal } from "naive-ui";
import { PersonCircleOutline as UserIcon, LogOutOutline as LogoutIcon } from "@vicons/ionicons5";
import { storeToRefs } from 'pinia';
import EditUserForm from './EditUserForm.vue';

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const sessionStore = useSessionStore();
const activateEditUserForm = ref(false);

// Avatar color
const randomColor = computed(() => {
  const colors = ["#2c98f5"];
  return colors[Math.floor(Math.random() * colors.length)];
});

// Theme toggling (kept for potential external use)
const isDark = ref(false);
const toggleTheme = () => {
  isDark.value = !isDark.value;
  document.documentElement.classList.toggle('dark-theme', isDark.value);
  localStorage.setItem('theme', isDark.value ? 'dark' : 'light');
};

onMounted(() => {
  const savedTheme = localStorage.getItem('theme');
  if (savedTheme === 'dark') {
    isDark.value = true;
    document.documentElement.classList.add('dark-theme');
  }
});

const handleSelect = (key) => {
  if (key === "profile") {
    activateEditUserForm.value = true;
  } else if (key === "logout") {
    sessionStore.logout();
  }
};

const renderIcon = (icon) => () => h(NIcon, null, { default: () => h(icon) });

const options = [
  {
    key: "profile",
    label: "Profile",
    icon: renderIcon(UserIcon)
  },
  {
    key: "logout",
    label: "Logout",
    icon: renderIcon(LogoutIcon)
  }
];

// Debug function to check applied styles
onMounted(() => {
  setTimeout(() => {
    const icons = document.querySelectorAll('.n-dropdown-option-body .n-icon svg');
    icons.forEach(icon => {
      console.log('Icon computed style:', window.getComputedStyle(icon).fill);
    });
  }, 1000); // Delay to ensure dropdown is rendered
});
</script>

<template>
  <div class="user-menu">
    <!-- Avatar Dropdown -->
    <n-dropdown 
      placement="bottom-end" 
      :options="options" 
      trigger="click" 
      @select="handleSelect" 
      style="background-color: #000; -webkit-text-fill-color: rgb(191 201 210);"
    >
      <n-avatar 
        :style="{ backgroundColor: randomColor }" 
        round 
        :size="48" 
        class="cursor-pointer hover:opacity-80 text-white font-bold uppercase"
      >
        {{ (user?.name?.charAt(0) || "U").toUpperCase() }}
      </n-avatar>
    </n-dropdown>

    <!-- Modal for Edit Profile -->
    <n-modal v-model:show="activateEditUserForm" :trap-focus="false" :style="{ backgroundColor: isDark ? '#000' : '' }">
      <EditUserForm @closeInvitationsForm="handleDeactivateEditUserForm" />
    </n-modal>
  </div>
</template>

<style>
/* Global style to override --n-prefix-color */
.n-dropdown-option-body .n-dropdown-option-body__prefix .n-icon {
  --n-prefix-color: rgb(255 255 255) !important;
  color: rgb(255 255 255) !important;
  fill: rgb(255 255 255) !important;
  stroke: rgb(255 255 255) !important;
}

.n-dropdown-option-body .n-dropdown-option-body__prefix .n-icon svg {
  fill: rgb(255 255 255) !important;
  stroke: rgb(255 255 255) !important;
  color: rgb(255 255 255) !important;
}

/* Add hover background color override */
.n-dropdown-option-body:hover {
  --n-option-color-hover: #4a4848 !important;
  background-color: #4a4848 !important;
}
</style>

<style scoped>
/* Scoped styles for text color consistency */
:deep(.n-dropdown-option-body) {
  color: rgb(191 201 210) !important;
  transition: color .3s var(--n-bezier);
}

:deep(.n-dropdown-option-body:hover) {
  color: #4a4848 !important;
  --n-option-color-hover: #4a4848 !important;
}

:deep(.n-dropdown-option-body.n-dropdown-option-body--active) {
  color: #18a058 !important;
}

/* Ensure the label text inherits the custom color */
:deep(.n-dropdown-option-body__label) {
  color: rgb(191 201 210) !important;
  white-space: nowrap;
  flex: 1;
  z-index: 1;
}
</style>