<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { NButton, NModal } from 'naive-ui';

import EditProjectForm from "./EditProjectForm.vue";

const projectProps = defineProps(['project']);
const emit = defineEmits(['updateProject', 'updatePhasesAndTickets'])
const activateEditForm = ref(false);

function handleEditProjectButtonClicked() {
  activateEditForm.value = true;
}
function handleCloseEditProjectForm() {
  activateEditForm.value = false;
}
</script>

<template>
  <n-modal v-model:show="activateEditForm" :trap-focus="false">
    <EditProjectForm
      :project="projectProps.project"
      @closeEditProjectForm="handleCloseEditProjectForm"
      @updateProject="emit('updateProject')"
      @updatePhasesAndTickets="emit('updatePhasesAndTickets')"
    />
  </n-modal>

  <n-button @click="handleEditProjectButtonClicked" block secondary strong :style="{ maxWidth: '125px', borderRadius: '5px',  }" class="custom-edit-button">
    Edit
  </n-button>
</template>

<style scoped>
/* Add a specific class to increase specificity */
.custom-edit-button {
  --n-color: #1e90ff !important; /* Solid dodger blue */
  --n-color-hover: #104e8b !important; /* Darker blue on hover */
  --n-color-pressed: #0a355a !important; /* Even darker blue when pressed */
  --n-color-focus: #104e8b !important; /* Darker blue on focus */
  --n-color-disabled: #4682b4 !important; /* Lighter blue when disabled */
  --n-text-color: #fff !important;
  --n-text-color-hover: #fff !important;
  --n-text-color-pressed: #fff !important;
  --n-text-color-focus: #fff !important;
  --n-text-color-disabled: #fff !important;
  max-width: 125px !important;
  border-radius: 5px !important;
  border: none !important;
  transition: background-color 0.3s !important;
}

/* Ensure state styles use custom variables with high specificity */
.custom-edit-button:hover,
:deep(.custom-edit-button:hover) {
  background-color: var(--n-color-hover) !important;
}

.custom-edit-button:active,
.custom-edit-button--pressed,
:deep(.custom-edit-button:active),
:deep(.custom-edit-button--pressed) {
  background-color: var(--n-color-pressed) !important;
}

.custom-edit-button--focus,
:deep(.custom-edit-button--focus) {
  background-color: var(--n-color-focus) !important;
}

.custom-edit-button--disabled,
:deep(.custom-edit-button--disabled) {
  background-color: var(--n-color-disabled) !important;
}

/* Override secondary type with custom colors */
.custom-edit-button.n-button--secondary,
:deep(.custom-edit-button.n-button--secondary) {
  --n-color: #1e90ff !important;
  --n-color-hover: #104e8b !important;
  --n-color-pressed: #0a355a !important;
  --n-color-focus: #104e8b !important;
  --n-color-disabled: #4682b4 !important;
  --n-text-color: #fff !important;
  --n-text-color-hover: #fff !important;
  --n-text-color-pressed: #fff !important;
  --n-text-color-focus: #fff !important;
  --n-text-color-disabled: #fff !important;
  background-color: var(--n-color) !important;
}

.custom-edit-button.n-button--secondary:hover,
:deep(.custom-edit-button.n-button--secondary:hover) {
  background-color: var(--n-color-hover) !important;
}

.custom-edit-button.n-button--secondary:active,
.custom-edit-button.n-button--secondary--pressed,
:deep(.custom-edit-button.n-button--secondary:active),
:deep(.custom-edit-button.n-button--secondary--pressed) {
  background-color: var(--n-color-pressed) !important;
}

.custom-edit-button.n-button--secondary--focus,
:deep(.custom-edit-button.n-button--secondary--focus) {
  background-color: var(--n-color-focus) !important;
}

.custom-edit-button.n-button--secondary--disabled,
:deep(.custom-edit-button.n-button--secondary--disabled) {
  background-color: var(--n-color-disabled) !important;
}
</style>