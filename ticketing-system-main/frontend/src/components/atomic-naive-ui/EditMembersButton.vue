<script setup>
import { ref, defineProps, defineEmits } from "vue";
import { NButton, NModal } from 'naive-ui';

import EditMembersForm from "./EditMembersForm.vue";

const emit = defineEmits(['updateMembers']);
const props = defineProps(['projectMembers']);
const activateEditMembersForm = ref(false);

function handleEditMembersButtonClicked() {
  activateEditMembersForm.value = true;
}
function handleCloseEditMembersForm() {
  activateEditMembersForm.value = false;
}
function handleUpdateMembers() {
  emit('updateMembers');
}
</script>

<template>
  <n-modal v-model:show="activateEditMembersForm" :trap-focus="false">
    <EditMembersForm
      :projectMembers="props.projectMembers"
      @closeEditMembersForm="handleCloseEditMembersForm" 
      @updateMembers="handleUpdateMembers" 
    />
  </n-modal>

  <n-button @click="handleEditMembersButtonClicked" block secondary strong :style="{ maxWidth: '125px', borderRadius: '5px'}" class="custom-edit-members-button">
    Edit Members
  </n-button>
</template>

<style scoped>
/* Add a specific class to increase specificity */
.custom-edit-members-button {
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
  --n-border: none !important;
  --n-border-hover: none !important;
  --n-border-pressed: none !important;
  --n-border-focus: none !important;
  --n-border-disabled: none !important;
  max-width: 125px !important;
  border-radius: 5px !important;
  border: none !important;
  transition: background-color 0.3s !important;
}

/* Ensure state styles use custom variables with high specificity */
.custom-edit-members-button:hover,
:deep(.custom-edit-members-button:hover) {
  background-color: var(--n-color-hover) !important;
}

.custom-edit-members-button:active,
.custom-edit-members-button--pressed,
:deep(.custom-edit-members-button:active),
:deep(.custom-edit-members-button--pressed) {
  background-color: var(--n-color-pressed) !important;
}

.custom-edit-members-button--focus,
:deep(.custom-edit-members-button--focus) {
  background-color: var(--n-color-focus) !important;
}

.custom-edit-members-button--disabled,
:deep(.custom-edit-members-button--disabled) {
  background-color: var(--n-color-disabled) !important;
}

/* Override secondary type with custom colors */
.custom-edit-members-button.n-button--secondary,
:deep(.custom-edit-members-button.n-button--secondary) {
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

.custom-edit-members-button.n-button--secondary:hover,
:deep(.custom-edit-members-button.n-button--secondary:hover) {
  background-color: var(--n-color-hover) !important;
}

.custom-edit-members-button.n-button--secondary:active,
.custom-edit-members-button.n-button--secondary--pressed,
:deep(.custom-edit-members-button.n-button--secondary:active),
:deep(.custom-edit-members-button.n-button--secondary--pressed) {
  background-color: var(--n-color-pressed) !important;
}

.custom-edit-members-button.n-button--secondary--focus,
:deep(.custom-edit-members-button.n-button--secondary--focus) {
  background-color: var(--n-color-focus) !important;
}

.custom-edit-members-button.n-button--secondary--disabled,
:deep(.custom-edit-members-button.n-button--secondary--disabled) {
  background-color: var(--n-color-disabled) !important;
}
</style>