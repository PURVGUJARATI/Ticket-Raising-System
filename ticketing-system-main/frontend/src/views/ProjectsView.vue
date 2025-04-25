<script setup>
import { ref, onMounted } from 'vue';
import { useProjectStore } from '../stores/project';
import { storeToRefs } from 'pinia';
import { NLayout, NCard, NEllipsis, NRow, NCol, NEmpty, NIcon, NButton, NModal } from 'naive-ui';
import { AlbumsOutline as ProjectsIcon } from '@vicons/ionicons5';
import NewProjectButton from '../components/atomic-naive-ui/NewProjectButton.vue';
import InvitationsButton from '../components/atomic-naive-ui/InvitationsButton.vue';
import NewProjectForm from '../components/atomic-naive-ui/NewProjectForm.vue';
import { useMembershipStore } from '../stores/membership';

// Store setup
const projectStore = useProjectStore();
const { projects } = storeToRefs(projectStore);
const membershipStore = useMembershipStore();
const { memberships } = storeToRefs(membershipStore);

// Show modal
const showNewProjectModal = ref(false);

// Check if there are any open invitations
function invitationsExist() {
  return memberships.value.some(m => m.state === 'OPEN');
}

// Update projects list
async function updateProjects() {
  await projectStore.updateProjectsByAcceptedMemberships();
}

// Open modal
function handleNewProjectButtonClicked() {
  showNewProjectModal.value = true;
}

// Close modal
function handleModalClose() {
  showNewProjectModal.value = false;
}
</script>

<template>
  <n-layout class="dark-override-layout n-layout--static-positioned" style="height: 90.3vh;">
    <div class="projects-container">
      <!-- Header -->
      <div class="header">
        <h1 class="page-title">Projects</h1>
        <div class="action-buttons">
          <NewProjectButton />
          <div v-if="invitationsExist()" class="invitations-wrapper">
            <InvitationsButton @updateProjects="updateProjects()" />
          </div>
        </div>
      </div>

      <!-- Content -->
      <div class="content">
        <div class="no-projects-placeholder" v-if="projects.length === 0">
          <n-empty size="huge" description="No Projects Found">
            <template #icon>
              <n-icon size="60" color="#63e2b7">
                <ProjectsIcon />
              </n-icon>
            </template>
            <template #extra>
              <n-button type="primary" @click="handleNewProjectButtonClicked">
                Create Your First Project
              </n-button>
            </template>
          </n-empty>
        </div>

        <div v-else class="projects-grid">
          <n-row :gutter="[20, 20]">
            <n-col
              v-for="project in projects"
              :key="project.id"
              :span="24"
              :xs="24"
              :sm="12"
              :md="8"
              :lg="6"
            >
              <RouterLink :to="`/projects/${project.id}`" class="card-link">
                <n-card :title="project.name" hoverable class="project-card">
                  <n-ellipsis line-clamp="3" class="project-description">
                    {{ project.description }}
                    <template #tooltip>
                      <div class="tooltip-content">
                        {{ project.description }}
                      </div>
                    </template>
                  </n-ellipsis>
                  <template #footer>
                    <p class="creation-time">
                      Created: {{ new Date(project.creationTime).toLocaleString() }}
                    </p>
                  </template>
                </n-card>
              </RouterLink>
            </n-col>
          </n-row>
        </div>
      </div>

      <!-- Modal to Create New Project -->
      <n-modal v-model:show="showNewProjectModal" :trap-focus="false" preset="dialog" style="background-color: #2c2c2c; color: #fff;">
        <NewProjectForm @close="handleModalClose" @project-created="updateProjects" />
      </n-modal>
    </div>
  </n-layout>
</template>

<style scoped>
/* Global body styling with custom variables */
body {
  margin: 0;
  padding: 0;
  background-color: #1e1e1e; /* Dark background */
  color: #fff; /* Default text color */
  --bg-color: #1e1e1e; /* Custom variable for background */
  --text-color: #fff; /* Custom variable for text */
  --n-text-color: #fff; /* Override Naive UI text color */
  --n-title-text-color: #63e2b7; /* Lock title color to green */
  transition: background-color 0.3s, color 0.3s;
}

/* Main container styling */
.projects-container {
  padding: 30px;
  width: 100%;
  max-width: 800px;
  margin: 30px auto;
  background-color: #1e1e1e; /* Consistent dark base */
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.3); /* Enhanced shadow */
  color: #fff; /* Ensure text is white */
}

/* Header styling */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  font-size: 24px;
  font-weight: bold;
  color: #63e2b7; /* Green accent */
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* Content area styling */
.content {
  min-height: 60vh; /* Kept for layout */
}

/* No projects placeholder styling */
.no-projects-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #2c2c2c; /* Darker card background */
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2); /* Subtle shadow */
  padding: 20px;
  color: #fff; /* White text */
}

:deep(.n-empty .n-empty__description) {
  color: #bbb; /* Lighter gray for description */
}

/* Projects grid styling */
.projects-grid {
  width: 100%;
}

/* Project card styling */
.project-card {
  height: 200px;
  background-color: #2c2c2c; /* Consistent with other dark elements */
  border-radius: 10px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3); /* Enhanced shadow */
  transition: transform 0.2s, box-shadow 0.3s; /* Added box-shadow transition */
  color: #fff; /* White text */
}

.project-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.4);
}

/* Override Naive UI card header title style */
:deep(.n-card > .n-card-header .n-card-header__main) {
  font-weight: var(--n-title-font-weight);
  transition: none; /* Disable color transition to prevent flickering */
  flex: 1;
  color: #63e2b7; /* Explicitly set green color */
}

/* Ensure card body text remains white */
:deep(.n-card) {
  color: #fff; /* Override default text color */
}

.card-link {
  text-decoration: none;
  color: inherit; /* Inherits #fff from parent */
}

.card-link a {
  color: #63e2b7; /* Override link color to green */
}

.project-description {
  margin-bottom: 10px;
  color: #63e2b7; /* Lighter gray for description */
}

:deep(.n-ellipsis) {
  color: #63e2b7 !important; /* Ensure ellipsis text is readable */
}

.creation-time {
  font-size: 12px;
  color: #888; /* Gray for creation time */
  font-style: italic;
}

.tooltip-content {
  max-width: 300px;
  background-color: #2c2c2c;
  color: #fff;
  padding: 10px;
  border-radius: 5px;
}

/* Responsive design */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
  }
  .action-buttons {
    margin-top: 10px;
  }
}

/* Override n-layout(main background) styles with dark theme */
.dark-override-layout.n-layout--static-positioned {
  --n-color: #232222 !important; /* Dark background */
  --n-text-color: #fff !important; /* White text */
  --n-border-color: #444 !important; /* Dark border */
  --n-header-color: #2c2c2c !important; /* Darker header */
  --n-footer-color: #2c2c2c !important; /* Darker footer */
}

/* Ensure nested layout elements inherit the theme */
:deep(.dark-override-layout.n-layout--static-positioned) {
  background-color: var(--n-color) !important;
  color: var(--n-text-color) !important;
}
</style>