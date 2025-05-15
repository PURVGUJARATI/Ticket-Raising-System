<script setup>
import { ref, h } from "vue";
import { RouterLink, useRoute } from "vue-router";
import { NLayoutSider, NMenu, NScrollbar, NIcon } from "naive-ui";
import {
  GridOutline as DashboardIcon,
  DocumentTextOutline as TicketsIcon,
  AlbumsOutline as ProjectsIcon,
  BarChartOutline as AnalysisIcon
} from "@vicons/ionicons5";

const location = useRoute();
const selectedKey = ref(location.name);
const collapsed = ref(false);

const menuOptions = [
  {
    key: "projects",
    icon: renderIcon(ProjectsIcon),
    label: () =>
      h(
        RouterLink,
        { to: { name: "projects" }, class: "menu-link" },
        { default: () => h("span", { class: "n-menu-item-content__label" }, "Projects") }
      )
  },
  {
    key: "tickets",
    icon: renderIcon(TicketsIcon),
    label: () =>
      h(
        RouterLink,
        { to: { name: "tickets" }, class: "menu-link" },
        { default: () => h("span", { class: "n-menu-item-content__label" }, "Tickets") }
      )
  },
  {
    key: "analysis",
    icon: renderIcon(AnalysisIcon),
    label: () =>
      h(
        RouterLink,
        { to: { name: "analysis" }, class: "menu-link" },
        { default: () => h("span", { class: "n-menu-item-content__label" }, "Analysis") }
      )
  }
];

function renderIcon(icon) {
  return () => h(NIcon, { size: 20, class: "menu-icon" }, { default: () => h(icon) });
}
</script>

<template>
  <n-layout-sider
    bordered
    collapse-mode="width"
    :collapsed-width="64"
    :width="160"
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
          :collapsed-width="58"
          :collapsed-icon-size="22"
          :options="menuOptions"
          v-model:value="selectedKey"
        />
      </n-scrollbar>
    </div>
  </n-layout-sider>
</template>

<style scoped>
/* Style the icon container */
.menu-icon {
  width: 40px;
  height: 40px;
  background-color: #2c98f0;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #252121 !important;
  margin-right: 12px;
}

/* Override Naive UI menu variables */
.custom-sidebar :deep(.n-menu.n-menu--vertical) {
  --n-item-text-color: #252121 !important;
  --n-item-text-color-active: #252121 !important;
  --n-item-text-color-hover: #1d9ed0 !important;
  --n-item-text-color-active-hover: #1d9ed0 !important;
  --n-item-text-color-child-active: #252121 !important;
  --n-item-icon-color-active-hover: #63e2b7 !important;
  --n-item-icon-color-active: #63e2b7 !important;
  --n-item-color-hover: #2c98f0 !important;
}

/* Ensure menu item layout uses flexbox */
.custom-sidebar :deep(.n-menu .n-menu-item-content) {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  position: relative;
}

/* Make the entire menu item clickable, including icon */
.custom-sidebar :deep(.n-menu .n-menu-item-content a.menu-link) {
  color: #252121 !important;
  text-decoration: none;
  display: block;
  width: 100%;
  height: 100%;
  position: flex;
  top: 0;
  left: 0;
  z-index: 1;
}

/* For Setting Color of text in sidebar */
.custom-sidebar :deep(.n-menu .n-menu-item-content .n-menu-item-content__label) {
  color: #252121 !important;
  font-weight: 500;
  position: relative;
  z-index: 2;
}

/* For Setting Color of text in sidebar when clicked */
.custom-sidebar :deep(.n-menu .n-menu-item-content--selected .n-menu-item-content__label) {
  color: #252121 !important;
  font-weight: 600;
}


.custom-sidebar :deep(.n-menu .n-menu-item-content--selected a.menu-link) {
  color: #252121 !important;
}

/* For Setting Color of text in sidebar when hovered or when drag Arrow on text */
.custom-sidebar :deep(.n-menu .n-menu-item-content:hover .n-menu-item-content__label) {
  color: #252121 !important;
}

/* For Setting Color of text in sidebar when tab is selected and we hovered or drag Arrow on text */
.custom-sidebar :deep(.n-menu .n-menu-item-content--selected:hover .n-menu-item-content__label) {
  color: #252121 !important;
}
.menu-icon:hover {
  background-color: #252121;
}

/* Sidebar styling */
.custom-sidebar {
  background: linear-gradient(180deg, #288ad9, #058636);
  padding: 10px 0 10px 8px; /* Padding for the sidebar */
  /* border-radius: 0 15px 15px 0; Rounded corners */
  z-index: 10;
}

</style>