<script setup>
import { ref, defineEmits, onMounted, computed } from "vue";
import { useTicketStore } from "../../stores/ticket";
import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, NDatePicker, NTransfer, useNotification, NCard, NSelect } from "naive-ui";
import { useFetchAgent } from "../../stores/fetchAgent";

const props = defineProps(['ticketId', 'projectId']);
const ticketStore = useTicketStore();
const fetchAgent = useFetchAgent();
const notificationAgent = useNotification();
const emit = defineEmits(['closeEditTicketForm', 'updateTickets']);
const formRef = ref(null);
const creationTime = ref(null);
const ticketPostData = ref({
  title: null,
  description: null,
  dueTime: null,
  assigneeIds: null,
  priority: null // New field for priority
});
const rules = {
  title: {
    required: true,
    trigger: ["blur", "input"],
    message: "Please input a title"
  },
  description: {
    required: true,
    trigger: ["blur", "input"],
    message: "Please input description"
  }
};
const projectMembers = ref([]);
const priorityOptions = [
  { label: "Low", value: "LOW" },
  { label: "Medium", value: "MEDIUM" },
  { label: "High", value: "HIGH" },
  { label: "Urgent", value: "URGENT" } // New priority option
];

const getProjectMembers = async () => {
  const response = await fetchAgent.getMembershipsByProjectId(props.projectId);
  if (response.isSuccessful) {
    const acceptedMemberships = response.data.filter(membership => membership.state == 'ACCEPTED');
    projectMembers.value = [];
    for (let membership of acceptedMemberships) {
      const getUserResponse = await fetchAgent.getUserById(membership.userId);
      if (getUserResponse.isSuccessful) {
        projectMembers.value.push({ label: getUserResponse.data.name, value: getUserResponse.data.id });
      }
    }
  }
};

async function handleSubmitButtonClick(e) {
  e.preventDefault();
  formRef.value?.validate(async (errors) => {
    if (!errors) {
      if (ticketPostData.value.dueTime < 1 || ticketPostData.value.dueTime == null) {
        ticketPostData.value.dueTime = null;
      } else {
        ticketPostData.value.dueTime = new Date(ticketPostData.value.dueTime);
      }
      if (ticketPostData.value.assigneeIds == null) {
        ticketPostData.value.assigneeIds = [];
      }

      const result = await ticketStore.patchTicket(props.projectId, props.ticketId, ticketPostData.value);
      if (result.isPostSuccessful) {
        emit('closeEditTicketForm');
        emit('updateTickets');
        ticketPostData.value.title = null;
        ticketPostData.value.description = null;
        ticketPostData.value.dueTime = null;
        ticketPostData.value.assigneeIds = null;
        ticketPostData.value.priority = null; // Reset priority
      } else {
        sendNotification("Error", result.message);
      }
    } else {
      console.log(errors);
    }
  });
}

async function handleDeleteTicketButtonClicked(e) {
  const result = await fetchAgent.deleteTicketById(props.ticketId);
  if (result.isSuccessful) {
    emit('closeEditTicketForm');
    emit('updateTickets');
  } else {
    sendNotification("Error", result.message);
  }
}

function handleCloseButtonClick(e) {
  emit('closeEditTicketForm');
  ticketPostData.value.title = null;
  ticketPostData.value.description = null;
  ticketPostData.value.dueTime = null;
  ticketPostData.value.assigneeIds = [];
  ticketPostData.value.priority = null; // Reset priority
}

function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content
  });
}

// Dirty fix to not decrease due time by two hours on every patch
function addTwoHoursToDate(date) {
  return date.setTime(date.getTime() + (2 * 60 * 60 * 1000));
}

onMounted(async () => {
  await getProjectMembers();
  const currentTicket = (await fetchAgent.getTicketById(props.ticketId)).data;
  ticketPostData.value.title = currentTicket.title;
  ticketPostData.value.description = currentTicket.description;
  ticketPostData.value.dueTime = currentTicket.dueTime == null ? null : addTwoHoursToDate(new Date(currentTicket.dueTime));
  ticketPostData.value.assigneeIds = currentTicket.assigneeIds;
  ticketPostData.value.priority = currentTicket.priority; // Load existing priority
  creationTime.value = new Date(currentTicket.creationTime).toLocaleString();
});
</script>

<template>
  <n-form ref="formRef" :model="ticketPostData" :rules="rules" :size="medium" label-placement="top"
    style="min-width: 300px; width: 50%; max-width: 600px; background-color: #fdfdfd; padding: 25px; border-radius: 5px;">
    <n-grid :span="24" :x-gap="24" :cols="1">
      <n-gi :span="24">
        <div style="display: flex; justify-content: space-between; font-size: 1.5em; font-weight: bold; padding-top: 10px; padding-bottom: 40px;">
          <div>Edit Ticket</div>
          <n-button @click="handleDeleteTicketButtonClicked" type="error" block error strong
            style="max-width: 75px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;">
            Delete
          </n-button>
        </div>
      </n-gi>

      <n-form-item-gi :span="24" label="Title" path="title">
        <n-input style="border-radius: 5px;" v-model:value="ticketPostData.title" placeholder="Title" />
      </n-form-item-gi>

      <n-form-item-gi :span="24" label="Description" path="description">
        <n-input style="border-radius: 5px;" v-model:value="ticketPostData.description" placeholder="Description"
          type="textarea" :autosize="{ minRows: 5, maxRows: 10 }" />
      </n-form-item-gi>

      <n-form-item-gi :span="12" label="Creation Time">
        <n-input :disabled="true" style="border-radius: 5px;" v-model:value="creationTime" placeholder="creationTime" />
      </n-form-item-gi>

      <n-form-item-gi :span="12" label="Due Time" path="dueTime">
        <n-date-picker :clearable="true" v-model:value="ticketPostData.dueTime" type="datetime" />
      </n-form-item-gi>

      <n-form-item-gi :span="12" label="Priority" path="priority">
        <n-select v-model:value="ticketPostData.priority" :options="priorityOptions" />
      </n-form-item-gi>

      <n-form-item-gi label="Assignees" path="assigneeIds">
        <n-transfer size="large" virtual-scroll ref="transfer" v-model:value="ticketPostData.assigneeIds"
          :options="projectMembers" />
      </n-form-item-gi>

      <n-gi :span="24">
        <div style="display: flex; justify-content: flex-end">
          <n-button type="error" block error strong
            style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;"
            @click="handleCloseButtonClick">
            Cancel
          </n-button>
             
          <n-button type="primary" block primary strong
            style="max-width: 125px; border-radius: 5px; box-shadow: 2px 2px 3px lightgrey;"
            @click="handleSubmitButtonClick">
            Submit Changes
          </n-button>
        </div>
      </n-gi>
    </n-grid>
  </n-form>
</template>