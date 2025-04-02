<template>
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

  <n-modal v-model:show="activateEditUserForm" :trap-focus="false">
    <EditUserForm @closeInvitationsForm="handleDeactivateEditUserForm" />
  </n-modal>
</template>

<script setup>
import { useUserStore } from '../../stores/user';
import { useSessionStore } from '../../stores/session';
import { h, ref, computed } from "vue";
import { NIcon, NDropdown, NAvatar, NModal } from "naive-ui";
import { PersonCircleOutline as UserIcon, LogOutOutline as LogoutIcon } from "@vicons/ionicons5";
import { storeToRefs } from 'pinia';
import EditUserForm from './EditUserForm.vue';

const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const sessionStore = useSessionStore();
const activateEditUserForm = ref(false);

// Generate a random color for avatar background
const randomColor = computed(() => {
  const colors = ["#FF5733", "#33FF57", "#5733FF", "#FFC300", "#C70039", "#900C3F"];
  return colors[Math.floor(Math.random() * colors.length)];
});

// Handles dropdown selection
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
</script>

<style scoped>
.cursor-pointer {
  cursor: pointer;
}
.text-white {
  color: white;
}
.font-bold {
  font-weight: bold;
}
.uppercase {
  text-transform: uppercase;
}
</style>
