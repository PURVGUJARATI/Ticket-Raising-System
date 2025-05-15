<script setup>
import { ref, onMounted, computed, h } from 'vue';
import { NSpace, NButton, NDataTable, NModal } from 'naive-ui';
import { useUserStore } from '../stores/user';
import { useMembershipStore } from '../stores/membership';
import { useProjectStore } from '../stores/project';
import { usePhaseStore } from '../stores/phase';
import { useTicketStore } from '../stores/ticket';
import { useFetchAgent } from '../stores/fetchAgent';
import { storeToRefs } from 'pinia';
import { RouterLink } from 'vue-router';

import EditTicketForm from '../components/atomic-naive-ui/EditTicketForm.vue';

import { toPadding } from 'chart.js/helpers';

const fetchAgent = useFetchAgent();
const userStore = useUserStore();
const { user } = storeToRefs(userStore);
const membershipStore = useMembershipStore();
const { memberships } = storeToRefs(membershipStore);
const projectStore = useProjectStore();
const { projects } = storeToRefs(projectStore);
const phaseStore = usePhaseStore();
const { phases } = storeToRefs(phaseStore);
const ticketStore = useTicketStore();
const { tickets } = storeToRefs(ticketStore);
const tableRef = ref(null);
const ticketData = ref([]);
const selectedTicketId = ref(null);
const activateEditTicketForm = ref(false);
const projectIdOfSelectedTicketId = ref(null);

const columns = [
  {
    title: 'Title',
    key: 'title',
    sorter: 'default',
    render(row) {
      return h(
        "div",
        { 
          onClick: () => openEditTicketForm(row.key, row.projectId), 
          style: { 'cursor': 'pointer', color: '#63e2b7' } 
        },
        { default: () => row.title }
      );
    }
  },
  {
    title: 'Description',
    key: 'description'
  },
  {
    title: 'Project',
    key: 'projectName',
    filterOptions: projectFilterOptions(),
    filter(value, row) {
      return ~row.projectName.indexOf(value);
    },
    sorter: 'default',
    render(row) {
      return h(
        RouterLink,
        { 
          to: { name: "projectDetails", params: { id: row.projectId } },
          style: { 'text-decoration': 'none', color: '#63e2b7' } 
        },
        { default: () => row.projectName }
      );
    }
  },
  {
    title: 'Phase',
    key: 'phaseName'
  },
  {
    title: 'Due',
    key: 'dueTime',
  },
  {
    title: 'Assignees',
    key: 'assigneeNames',
    filterOptions: [
      {
        label: 'Assigned To Me',
        value: 'myUserId'
      },
      {
        label: 'Unassigned',
        value: 'unassigned'
      }
    ],
    filter(value, row) {
      if (value == 'myUserId') {
        return row.assigneeIds.includes(user.value.id);
      }
      return row.assigneeIds == "";
    }
  },
  {
  title: 'Priority',
  key: 'priority',
  sorter: (row1, row2) => {
    const order = { 'LOW': 1, 'MEDIUM': 2, 'HIGH': 3, 'URGENT': 4 };
    return order[row1.priority] - order[row2.priority];
  },
  render(row) {
    const colorMap = {
      'LOW': '#2ecc71',      // green
      'MEDIUM': '#3498db',   // orange
      'HIGH': '#f1c40f',     // red
      'URGENT': '#e74c3c'    // purple
    };
    return h(
      'span',
      {
        style: {
          backgroundColor: colorMap[row.priority] || '#ccc',
          color: '#fff',
          padding: '4px 8px',
          borderRadius: '8px',
          fontWeight: 'bold',
          fontSize: '12px'
        }
      },
      row.priority
    );
  }
}
];

function openEditTicketForm(ticketId, projectId) {
  selectedTicketId.value = ticketId;
  projectIdOfSelectedTicketId.value = projectId;
  activateEditTicketForm.value = true;
}

function handleCloseEditTicketForm() {
  activateEditTicketForm.value = false;
  selectedTicketId.value = null;
  projectIdOfSelectedTicketId.value = null;
}

function nullableDateSorter(dateAlpha, dateOmega) {
  if (dateAlpha == null) {
    return 1;
  }
  if (dateOmega == null) {
    return -1;
  }
  return new Date(dateAlpha) <= new Date(dateOmega) ? -1 : 1;
}

async function updateAll() {
  await membershipStore.updateMembershipsByEmail();
  await projectStore.updateProjectsByAcceptedMemberships();
  for (let project of projects.value) {
    await phaseStore.updatePhasesByProjectId(project.id);
    await ticketStore.updateTicketsByProjectId(project.id);
  }
}

async function compileTableData() {
  const newData = ref([]);
  for (let ticket of tickets.value) {
    const project = projects.value.find(project => project.id == ticket.projectId);
    const phase = phases.value.find(phase => phase.id == ticket.phaseId);
    const assignees = ref([]);
    for (let assigneeId of ticket.assigneeIds) {
      const response = await fetchAgent.getUserById(assigneeId);
      if (response.isSuccessful) {
        assignees.value.push({ userId: response.data.id, userName: response.data.name });
      }
    }

    const assigneeNames = assignees.value.map(assignee => assignee.userName);

    newData.value.push({
      key: ticket.id,
      title: ticket.title,
      description: ticket.description,
      dueTime: ticket.dueTime == null ? null : new Date(ticket.dueTime).toLocaleString(),
      projectId: project.id,
      projectName: project.name,
      phaseName: phase.name,
      assigneeNames: assigneeNames.toString(),
      assigneeIds: assignees.value.map(assignee => assignee.userId),
      priority: ticket.priority
    });
  }

  ticketData.value = newData.value;
}

function projectFilterOptions() {
  const filterOptions = ref([]);
  const projectNames = projects.value.map(project => project.name);
  for (let projectName of projectNames) {
    filterOptions.value.push({ label: projectName, value: projectName });
  }
  return filterOptions.value;
}

function reloadPage() {
  window.location.reload(true);
}


onMounted(async () => {
  await updateAll();
  compileTableData();
});
</script>

<template>
  
  <div style="background-color: #1e1e1e; width: 100%; color: #fff; padding-bottom: 16px; ">
    <div style="padding-left: 15px; padding-right: 10px; width: auto;">
      <h1 style="color: #63e2b7;">Tickets</h1>
      <n-modal v-model:show="activateEditTicketForm" :trap-focus="false" style="background-color: #2c2c2c; color: #fff;">
        <EditTicketForm 
          :ticketId="selectedTicketId" 
          :projectId="projectIdOfSelectedTicketId" 
          @closeEditTicketForm="handleCloseEditTicketForm"
          @updateTickets="reloadPage" 
        />
      </n-modal>
      <n-space>
        <n-data-table 
          ref="table" 
          :style="{ height: '535px', overflowY: 'auto', backgroundColor: '#2c2c2c', color: '#fff' }"
          :columns="columns" 
          :data="ticketData"
          :single-line="false" 
          :bordered="false" 
          flex-height 
          :header-props="{ style: 'background-color: #2c2c2c; color: #fff;' }" 
        />
      </n-space>
    </div>
  </div>
  
</template>

<style>
.n-popover:not(.n-popover--raw) {
    background-color: #2c2c2c !important;
}

.n-checkbox .n-checkbox__label {
  color: #fff !important;
}

.n-button.n-button--default-type.n-button--tiny-type
{
  color: #fff !important;
}
</style>

<style scoped>
/* Ensure table headers and rows match dark theme */
.n-data-table {
  background-color: #1e1e1e !important;
  color: #fff !important;
  /* border-radius: 10px 10px 10px 10px; */
}

:deep(.n-data-table th) {
  background-color: #1e1e1e !important;
  color: #fff !important;
  border-bottom: 1px solid #444 !important;
  border-top: 1px solid #444 !important;
  border-left: 1px solid #444 !important;
  border-right: 1px solid #444 !important;
  
}

:deep(.n-data-table td) {
  background-color: #1e1e1e !important;
  color: #fff !important;
  border-bottom: 1px solid #444 !important;
  border-left: 1px solid #444 !important;
  border-right: 1px solid #444 !important;
  
}

:deep(.n-data-table tr:hover td) {
  background-color: #333 !important; /* Slight highlight on hover */
}

/* Modal styling */
:deep(.n-modal) {
  background-color: #2c2c2c !important;
  color: #fff !important;
}

:deep(.n-modal .n-card) {
  background-color: #2c2c2c !important;
  color: #fff !important;
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

@media (max-width: 1280px) {
    .n-data-table{
      padding-bottom: 99px;
    }
  
}
</style>
