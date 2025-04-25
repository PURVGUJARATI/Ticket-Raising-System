<script setup>
import { ref, defineEmits, onMounted } from "vue";
import { useFetchAgent } from "../../stores/fetchAgent";
import { NForm, NButton, NGi, NInput, NFormItemGi, NGrid, useNotification, NCard, NAvatar, NSwitch, NDivider, NFormItem } from "naive-ui";
import { useRoute } from "vue-router";
import DeleteMembershipButton from "./DeleteMembershipButton.vue";

const props = defineProps(['projectMembers']);
const fetchAgent = useFetchAgent();
const notificationAgent = useNotification();
const route = useRoute();
const emit = defineEmits(['updateMembers', 'closeEditMembersForm']);
const inputEmail = ref(null);

async function handlePatchRoleTriggered(membershipId, role) {
  const result = await fetchAgent.putMembershipRole(membershipId, { role: role });
  if (result.isSuccessful) {
    emit('updateMembers');
  } else {
    emit('updateMembers');
    sendNotification("Error", result.data)
  }
};
async function handleKeyUp() {
  if (true) {
    const getUserResult = await fetchAgent.getUserByEmail(inputEmail.value);
    if (getUserResult.isSuccessful) {
      const postMembershipResult = await fetchAgent.postMembership({ projectId: route.params.id, userId: getUserResult.data.id, role: 'MEMBER' });
      if (postMembershipResult.isSuccessful) {
        emit('updateMembers');
        sendNotification("Success", "Sent invitation to user \"" + getUserResult.data.name + "\".");
        inputEmail.value = null;
      } else {
        sendNotification("Error", postMembershipResult.data);
      }
    } else {
      sendNotification("Error", getUserResult.data);
    }
  }
}
function handleCancelButtonClick(e) {
  emit('closeEditMembersForm');
}
function sendNotification(_title, _content) {
  notificationAgent.create({
    title: _title,
    content: _content,
    duration: 5000
  });
}
</script>

<template>
    <n-card class="custom-card" style="width: 60%; max-width: 1200px; background-color: #000; border-radius: 5px;" :title-style="{ color: '#63e2b7' }" title="Edit Members" :bordered="false" size="huge" role="dialog" aria-modal="true">
      <div>
        <div style="font-size: 1.1em; color: #fff;">
          Invite other users by email
        </div>
        <input class="inputEmail" style="width: 50%; border-radius: 3px; border: 1px solid #444; background-color: #333; color: #fff;" id="inputEmail"
          type="search" v-model="inputEmail" v-on:keyup.enter="handleKeyUp" />
      </div>

      <n-divider />

      <div v-for="user in props.projectMembers">
        <div style="display: flex; font-size: 1.2em; color: #fff;">
          <div style="width: 10%;">
            <n-avatar :size='48' round :style="{ backgroundColor: '#2c2c2c' }">{{ Array.from(user.name)[0] }}</n-avatar>
          </div>
          <div style="width: 30%; margin-top: 10px;">
            {{ user.name }}
          </div>
          <div style="width: 35%; margin-top: 10px;">
            {{ user.email }}
          </div>
          <div style="margin-top: 10px;">
            <n-switch v-model:value="user.role" size="large" checked-value="ADMIN" unchecked-value="MEMBER"
              @update:value="handlePatchRoleTriggered(user.membershipId, user.role)">
              <template #checked>
                <span style="color: #fff;">Admin</span>
              </template>
              <template #unchecked>
                <span style="color: #fff;">Member</span>
              </template>
            </n-switch>
          </div>
          <div style="margin-top: 8px; padding-left: 60px;">
            <DeleteMembershipButton @updateMembers="emit('updateMembers')" :membershipId="user.membershipId" />
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
.inputEmail:focus {
  outline: none;
  border-color: #63e2b7; /* Accent color on focus */
  -webkit-text-fill-color: #ffffff !important;
  /* -webkit-box-shadow: 0 0 0px 1000px rgba(255, 255, 255, 0.1) inset !important; */
  transition: background-color 5000s ease-in-out 0s !important;
  background-color: rgba(255, 255, 255, 0.1) !important;
  color: #ffffff !important;
}
:deep(.input:-internal-autofill-selected)
{
    appearance: menulist-button;
    background-image: none !important;
    background-color: light-dark(rgb(0, 0, 0), rgba(0, 0, 0, 0.4)) !important;
    color: fieldtext !important;
}
</style>