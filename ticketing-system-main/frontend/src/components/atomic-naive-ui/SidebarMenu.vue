<script setup>
import { ref, h } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { NLayoutSider, NMenu, NScrollbar, NIcon } from "naive-ui";
import {
  GridOutline as DashboarIcon,
  DocumentTextOutline as TicketsIcon,
  AlbumsOutline as ProjectsIcon,
  BarChartOutline as AnalysisIcon
} from "@vicons/ionicons5";

const location = useRoute();
const selectedKey = ref(location.name);
const collapsed = ref(false);

const menuOptions = [
  {
    label: () =>
      h(
        RouterLink,
        { to: { name: "projects" }, class: "menu-link" },
        { default: () => "Projects" }
      ),
    key: "projects",
    icon: renderIcon(ProjectsIcon)
  },
  {
    label: () =>
      h(
        RouterLink,
        { to: { name: "tickets" }, class: "menu-link" },
        { default: () => "Tickets" }
      ),
    key: "tickets",
    icon: renderIcon(TicketsIcon)
  },
  {
    label: () =>
      h(
        RouterLink,
        { to: { name: "analysis" }, class: "menu-link" },
        { default: () => "Analysis" }
      ),
    key: "analysis",
    icon: renderIcon(AnalysisIcon)
  }
];

function renderIcon(icon) {
  return () => h(NIcon, { size: 20, class: "menu-icon",  }, { default: () => h(icon) });
}
</script>

<template>
  <n-layout-sider
    bordered
    collapse-mode="width"
    :collapsed-width="64"
    :width="200"
    show-trigger
    :collapsed="collapsed"
    @collapse="collapsed = true"
    @expand="collapsed = false"
    class="custom-sidebar"
  >
    <div class="sidebar-container">
      <n-scrollbar style="max-height: 80vh;">
        <n-menu
          :value="selectedKey"
          :collapsed="collapsed"
          :collapsed-width="64"
          :collapsed-icon-size="22"
          :options="menuOptions"
          v-model:value="selectedKey"
        />
      </n-scrollbar>
    </div>
  </n-layout-sider>
</template>

<style scoped>
/* Style the icon container with padding and background */
.menu-icon {
  width: 40px;
  height: 40px;
  background-color: #2c98f0; 
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #2c98f0;
}

/* Optional hover effect */
.menu-icon:hover {
  background-color: #2c98f0;
}

/* Prevent sidebar from clashing with profile button */
.custom-sidebar {
  background: linear-gradient(180deg, #2c98f0, #15c39a);
  padding: 10px 0 10px 8px; /* Add left padding */
  z-index: 10; /* Ensure it's below floating profile if needed */
}
</style>
