<script setup>
import { useUserStore } from '../../stores/user';
import { useSessionStore } from '../../stores/session';
import { h, ref, computed, onMounted } from "vue";
import { NIcon, NDropdown, NAvatar, NModal } from "naive-ui";
import { PersonCircleOutline as UserIcon, LogOutOutline as LogoutIcon, SunnyOutline as ThemeIcon } from "@vicons/ionicons5";
import { storeToRefs } from 'pinia';
import EditUserForm from './EditUserForm.vue';

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const sessionStore = useSessionStore();
const activateEditUserForm = ref(false);

// Avatar color
const randomColor = computed(() => {
  const colors = ["#2c98f0"];
  return colors[Math.floor(Math.random() * colors.length)];
});

// Theme toggling
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
  } else if (key === "change-theme") {
    toggleTheme();
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
    key: "change-theme",
    label: "Change Theme",
    icon: renderIcon(ThemeIcon)
  },
  {
    key: "logout",
    label: "Logout",
    icon: renderIcon(LogoutIcon)
  }
];
</script>

<template>
  <div class="user-menu">
    <!-- Avatar Dropdown -->
    <n-dropdown placement="bottom-end" :options="options" trigger="click" @select="handleSelect">
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
    <n-modal v-model:show="activateEditUserForm" :trap-focus="false">
      <EditUserForm @closeInvitationsForm="handleDeactivateEditUserForm" />
    </n-modal>
  </div>
</template>

