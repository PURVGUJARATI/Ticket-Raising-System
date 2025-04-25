<script setup>
import { ref, defineEmits, onMounted, computed, watch } from "vue";
import { useFetchAgent } from "../../stores/fetchAgent";
import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification, NCard, NDivider, NFormItem } from "naive-ui";
import { useRoute } from "vue-router";
import { usePhaseStore } from "../../stores/phase";
import { storeToRefs } from "pinia";

// Reactive references and computed properties
const props = defineProps(['project']);
const emit = defineEmits(['updateProject', 'updatePhasesAndTickets', 'closeEditProjectForm']);
const fetchAgent = useFetchAgent();
const notificationAgent = useNotification();
const route = useRoute();
const projectName = ref(props.project.name);
const projectDescription = ref(props.project.description);
const isNameSubmitButtonDisabled = computed(() => !projectName.value || projectName.value.length === 0);
const isDescriptionSubmitButtonDisabled = computed(() => !projectDescription.value || projectDescription.value.length === 0);
const phasestore = usePhaseStore();
const { phases } = storeToRefs(phasestore);
const phasesOfProject = computed(() => sortPhases(phases.value.filter(phase => phase.projectId == route.params.id)));
const newPhaseName = ref('');

// Watch input values to adjust text color dynamically
watch(projectName, (newValue) => {
  const input = document.querySelector('input[name="projectName"]');
  if (input) {
    input.style.color = newValue && newValue.length > 0 ? '#bbb' : '#fff';
  }
});
watch(projectDescription, (newValue) => {
  const textarea = document.querySelector('textarea[name="projectDescription"]');
  if (textarea) {
    textarea.style.color = newValue && newValue.length > 0 ? '#bbb' : '#fff';
  }
});
watch(newPhaseName, (newValue) => {
  const input = document.querySelector('input[name="newPhaseName"]');
  if (input) {
    input.style.color = newValue && newValue.length > 0 ? '#bbb' : '#fff';
  }
});

// Phase and project update functions
async function handleDeletePhase(phaseId) {
  const response = await fetchAgent.deletePhaseById(phaseId);
  if (response.isSuccessful) {
    emit('updatePhasesAndTickets');
  } else {
    sendNotification("Error", response.data);
  }
}
async function handlePatchPhaseName(phaseId, phaseName) {
  const response = await fetchAgent.patchPhaseNameById(phaseId, { name: phaseName });
  if (response.isSuccessful) {
    emit('updatePhasesAndTickets');
  } else {
    sendNotification("Error", response.data);
  }
}
async function handleAddPhase() {
  const projectPhases = sortPhases(phases.value.filter(phase => phase.projectId == route.params.id));
  const firstPhaseOfProjectId = projectPhases[projectPhases.length - 1].id;
  const response = await fetchAgent.postPhase({ projectId: route.params.id, name: newPhaseName.value, previousPhaseId: firstPhaseOfProjectId });
  if (response.isSuccessful) {
    emit('updatePhasesAndTickets');
    newPhaseName.value = '';
  } else {
    sendNotification("Error", response.data);
  }
}

function isPhaseButtonDisabled(phaseName) {
  return !phaseName || phaseName.length < 1;
}
function isPhaseDeleteButtonDisabled() {
  return phases.value.filter(phase => phase.projectId == route.params.id).length < 2;
}
async function handleSubmitNewProjectName() {
  const response = await fetchAgent.patchProjectById(props.project.id, { name: projectName.value, description: null });
  if (response.isSuccessful) {
    emit('updateProject');
  } else {
    sendNotification("Error", response.data);
  }
}
async function handleSubmitNewProjectDescription() {
  const response = await fetchAgent.patchProjectById(props.project.id, { name: null, description: projectDescription.value });
  if (response.isSuccessful) {
    emit('updateProject');
  } else {
    sendNotification("Error", response.data);
  }
}
function sortPhases(givenPhases) {
  if (!Array.isArray(givenPhases) || !givenPhases.length) return [];
  const sortedPhases = [];
  let phase = givenPhases.find(element => element.previousPhaseId == null);
  sortedPhases.push(phase);
  while (phase?.nextPhaseId != null) {
    phase = givenPhases.find(element => element.id == phase.nextPhaseId);
    sortedPhases.push(phase);
  }
  return sortedPhases;
}
function handleCloseButtonClick(e) {
  emit('closeEditProjectForm');
}
function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content,
    duration: 5000,
  });
}

// onMounted(async () => {
//   await phasestore.updatePhasesById(route.params.id);
// });
</script>

<template>
  <n-card class="custom-card" style="width: 50%; max-width: 1000px; background-color: #000; border-radius: 5px; color: #fff;" title="Edit Project" :bordered="false" size="huge" role="dialog" aria-modal="true">
    <div>
      <div style="display: flex; font-size: 1.2em; color: #fff;">
        <div style="width: 95%;">
          <n-form-item label="Edit Name" label-style="color: #63e2b7;">
            <n-input v-model:value="projectName" style="background-color: #333; color: #fff; border: 1px solid #444;" name="projectName" />
            <n-button @click="handleSubmitNewProjectName" :disabled="isNameSubmitButtonDisabled" block secondary strong
              style="max-width: 70px; margin-left: 10px; border-radius: 5px; box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3); background-color: #1e90ff; color: #fff;"
              class="custom-change-button">
              Change
            </n-button>
          </n-form-item>
        </div>
      </div>

      <div style="display: flex; font-size: 1.2em; color: #fff;">
        <div style="width: 95%; border-bottom: 2px solid #444;">
          <n-form-item label="Edit Description" label-style="color: #63e2b7;">
            <n-input :autosize="{ minRows: 3, maxRows: 3 }" type="textarea" v-model:value="projectDescription" style="background-color: #333; color: #fff; border: 1px solid #444;" name="projectDescription" />
            <n-button @click="handleSubmitNewProjectDescription" :disabled="isDescriptionSubmitButtonDisabled" block
              secondary strong
              style="max-width: 70px; margin-left: 10px; margin-bottom: 46px; border-radius: 5px; box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3); background-color: #1e90ff; color: #fff;"
              class="custom-change-button">
              Change
            </n-button>
          </n-form-item>
        </div>
      </div>

      <br />
      <div style="font-size: 1.3em; font-weight: bold; color: #63e2b7;">
        Phases
      </div>

      <div style="padding-left: 10px;" v-for="(phase, index) in phasesOfProject">
        <div style="width: 400px; margin-top: 18px; display: flex; font-size: 1.2em; color: #fff;">
          <div>{{ index + 1 }}</div>
          <div>:</div>
          <div> </div>
          <div> </div>
          <n-input v-model:value="phase.name" style="background-color: #333; color: #fff; border: 1px solid #444;" name="phaseName" />
          <n-button @click="handlePatchPhaseName(phase.id, phase.name)" block secondary strong :disabled="isPhaseButtonDisabled(phase.name)"
            style="margin-left: 15px; max-width: 70px; border-radius: 5px; box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3); background-color: #1e90ff; color: #fff;"
            class="custom-change-button">
            Change
          </n-button>
          <n-button @click="handleDeletePhase(phase.id)" type="error" block strong :disabled="isPhaseDeleteButtonDisabled()"
            style="margin-left: 15px; max-width: 40px; border-radius: 5px; box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3);">
            X
          </n-button>
        </div>
      </div>

      <div style="padding-left: 10px;">
        <div style="width: 400px; margin-top: 18px; display: flex; font-size: 1.2em; color: #fff;">
          <div>{{ phasesOfProject.length + 1 }}</div>
          <div>:</div>
          <div> </div>
          <div> </div>
          <n-input v-model:value="newPhaseName" style="background-color: #333; color: #fff; border: 1px solid #444;" name="newPhaseName" />
          <n-button @click="handleAddPhase" type="primary" block primary strong :disabled="isPhaseButtonDisabled(newPhaseName)"
            style="margin-left: 15px; margin-right: 55px; max-width: 70px; border-radius: 5px; box-shadow: 2px 2px 3px rgba(0, 0, 0, 0.3);">
            Add
          </n-button>
        </div>
      </div>
    </div>
  </n-card>
</template>

<style scoped>
.n-card {
  --n-card-color: #000 !important;
  --n-title-text-color:#63e2b7 !important;
}

/* Input and textarea styling */
:deep(.n-input .n-input__input-el),
:deep(.n-input .n-input__textarea-el) {
  scrollbar-width: none;
  width: 100%;
  min-width: 0;
  text-decoration-color: var(--n-text-decoration-color);
  color: #fff; /* Default color for empty/placeholder */
  caret-color: var(--n-caret-color);
  background-color: #333; /* Match input background */
}

/* Dynamic text color for filled inputs */
:deep(.n-input .n-input__input-el:not(:placeholder-shown)),
:deep(.n-input .n-input__textarea-el:not(:placeholder-shown)) {
  color: #bbb; /* Light gray for filled state */
}

/* Ensure placeholder text is visible */
:deep(.n-input .n-input__input-el::placeholder),
:deep(.n-input .n-input__textarea-el::placeholder) {
  color: #bbb; /* Light gray for placeholders */
  opacity: 1; /* Ensure placeholder is fully visible */
}

/* Custom styling for Change buttons */
.custom-change-button {
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

.custom-change-button:hover,
:deep(.custom-change-button:hover) {
  background-color: var(--n-color-hover) !important;
}

.custom-change-button:active,
.custom-change-button--pressed,
:deep(.custom-change-button:active),
:deep(.custom-change-button--pressed) {
  background-color: var(--n-color-pressed) !important;
}

.custom-change-button--focus,
:deep(.custom-change-button--focus) {
  background-color: var(--n-color-focus) !important;
}

.custom-change-button--disabled,
:deep(.custom-change-button--disabled) {
  background-color: var(--n-color-disabled) !important;
}

.custom-change-button.n-button--secondary,
:deep(.custom-change-button.n-button--secondary) {
  --n-color: #1e90ff !important;
  --n-color-hover: #104e8b !important;
  --n-color-pressed: #0a355a !important;
  --n-color-focus: #104e8b !important;
  --n-color-disabled: #4682b4 !important;
  --n-text-color: #fff !important;
  background-color: var(--n-color) !important;
}

.custom-change-button.n-button--secondary:hover,
:deep(.custom-change-button.n-button--secondary:hover) {
  background-color: var(--n-color-hover) !important;
}

.custom-change-button.n-button--secondary:active,
.custom-change-button.n-button--secondary--pressed,
:deep(.custom-change-button.n-button--secondary:active),
:deep(.custom-change-button.n-button--secondary--pressed) {
  background-color: var(--n-color-pressed) !important;
}

.custom-change-button.n-button--secondary--focus,
:deep(.custom-change-button.n-button--secondary--focus) {
  background-color: var(--n-color-focus) !important;
}

.custom-change-button.n-button--secondary--disabled,
:deep(.custom-change-button.n-button--secondary--disabled) {
  background-color: var(--n-color-disabled) !important;
}
</style>