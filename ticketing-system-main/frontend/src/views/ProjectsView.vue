<script setup>
import { useProjectStore } from '../stores/project';
import { storeToRefs } from 'pinia';
import { NCard, NEllipsis, NRow, NCol, NEmpty, NIcon, NButton } from 'naive-ui';
import { AlbumsOutline as ProjectsIcon } from '@vicons/ionicons5';
import NewProjectButton from '../components/atomic-naive-ui/NewProjectButton.vue';
import InvitationsButton from '../components/atomic-naive-ui/InvitationsButton.vue';
import { useMembershipStore } from '../stores/membership';

// Set up project store
const projectStore = useProjectStore();
const { projects } = storeToRefs(projectStore);

// Set up membership store
const membershipStore = useMembershipStore();
const { memberships } = storeToRefs(membershipStore);

// Check if there are any open invitations
function invitationsExist() {
  let unacceptedMemberships = memberships.value.filter(
    (membership) => membership.state === 'OPEN'
  );
  return unacceptedMemberships !== undefined && unacceptedMemberships.length > 0;
}

// Update projects list
async function updateProjects() {
  await projectStore.updateProjectsByAcceptedMemberships();
}
</script>

<template>
  <div class="projects-container">
    <!-- Header with title and action buttons -->
    <div class="header">
      <h1 class="page-title">Projects</h1>
      <div class="action-buttons">
        <NewProjectButton />
        <div v-if="invitationsExist()" class="invitations-wrapper">
          <InvitationsButton @updateProjects="updateProjects()" />
        </div>
      </div>
    </div>

    <!-- Content area -->
    <div class="content">
      <!-- Placeholder when no projects exist -->
      <div class="no-projects-placeholder" v-if="projects.length === 0">
        <n-empty size="huge" description="No Projects Found">
          <template #icon>
            <n-icon size="60" color="#888">
              <ProjectsIcon />
            </n-icon>
          </template>
          <template #extra>
            <n-button type="primary" @click="$emit('new-project')">
              Create Your First Project
            </n-button>
          </template>
        </n-empty>
      </div>

      <!-- Project grid when projects exist -->
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
  </div>
</template>

<style scoped>
/* Main container styling */
.projects-container {
  padding: 30px;
  width: 100%;
  max-width: 800px;
  margin: 0 auto;
  background-color: #f5f5f5;
  border-radius: 10px;
  box-shadow: 0 0 20px rgba(0, 0, 0, 0.2);
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
}

.action-buttons {
  display: flex;
  gap: 10px;
}

/* Content area styling */
.content {
  min-height: 60vh;
}

/* No projects placeholder styling */
.no-projects-placeholder {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  padding: 20px;
}

/* Projects grid styling */
.projects-grid {
  width: 100%;
}

/* Project card styling */
.project-card {
  height: 200px;
  background-color: #fff;
  border-radius: 10px;
  box-shadow: 0 6px 12px rgba(0, 0, 0, 0.3);
  transition: transform 0.2s, box-shadow 0.2s;
}

.project-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.4);
}

.card-link {
  text-decoration: none;
  color: inherit;
}

.project-description {
  margin-bottom: 10px;
}

.creation-time {
  font-size: 12px;
  color: #888;
  font-style: italic;
}

.tooltip-content {
  max-width: 300px;
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
</style>