<script setup>
  import { defineEmits, onMounted, computed } from "vue";
  import { useFetchAgent } from "../../stores/fetchAgent";
  import { NButton, useNotification, NCard } from "naive-ui";
  import { useMembershipStore } from "../../stores/membership";
  import { storeToRefs } from "pinia";
  import { useProjectStore } from "../../stores/project";

  const emit = defineEmits(['updateProjects', 'closeInvitationsForm']);
  const fetchAgent = useFetchAgent();
  const notificationAgent = useNotification();
  const membershipStore = useMembershipStore();
  const { memberships } = storeToRefs(membershipStore);
  const unaccceptedMemberships = computed(() => memberships.value.filter(membership => membership.state == 'OPEN'));
  const projectStore = useProjectStore();
  const { projects } = storeToRefs(projectStore);

  async function handleAcceptInvitation(membershipId) {
    const response = await fetchAgent.putMembershipState(membershipId, {state: 'ACCEPTED'});
    if (response.isSuccessful) {
      await membershipStore.updateMembershipsByEmail();
      await projectStore.updateProjectsByAcceptedMemberships();
      emit('updateProjects');
      if (unaccceptedMemberships.length < 1) {
        emit('closeInvitationsForm');
      }
    } else {
      sendNotification("Error", response.data);
    }
  }
  async function handleDeclineInvitation(membershipId) {
    const response = await fetchAgent.deleteMembershipById(membershipId);
    if (response.isSuccessful) {
      await membershipStore.updateMembershipsByEmail();
      if (unaccceptedMemberships.length < 1) {
        emit('closeInvitationsForm');
      }
    } else {
      sendNotification("Error", response.data);
    }
  }

  function handleCloseButtonClick(e) {
    emit('closeInvitationsForm');
  }
  function sendNotification(_title, _content) {
    notificationAgent.create({
      title: _title,
      content: _content
    });
  }

  const projectNameById = computed(() => {
    const result = unaccacceptedMemberships.value.reduce((acc, invitation) => {
        acc[invitation.projectId] = invitation.projectName || 'Unnamed Project';
        return acc;
    }, {});
    console.log('projectNameById:', JSON.stringify(result, null, 2));
    return result;
});
</script>

<template>
  <n-card style=" max-width: 1000px; border-radius: 5px; background-color: #1e1e1e;" title="Invitations" :bordered="false" size="huge" role="dialog" aria-modal="true">
    <div v-for="invitation in unaccceptedMemberships">
      <div style="display: flex; justify-content: space-around; font-size: 1.2em; padding-bottom: 10px;">
        
        <div style="display: flex;">
          <div style="margin-top: 10px; font-weight: bold; color: #63e2b7;">
            Project Name:
          </div>
          &nbsp;
          <div style="margin-top: 10px; color: #fff;">
            {{ invitation.projectName || 'Missing' }}          
          </div>
        </div>

        <div style="display: flex;">
          <div style="margin-top: 10px; font-weight: bold; color: #63e2b7;">
            Role:
          </div>
          &nbsp;
          <div style="margin-top: 10px; min-width: 67px; color: #fff;">
            {{ invitation.role }}
          </div>
        </div>

        <div style="display: flex;">
          <div style="margin-top: 6px;">
            <n-button @click="handleAcceptInvitation(invitation.id)" type="primary" block strong primary
              style="border-radius: 5px;">
              Accept
            </n-button>
          </div>
          &nbsp;&nbsp;&nbsp;
          <div style="margin-top: 6px;">
            <n-button @click="handleDeclineInvitation(invitation.id)" type="error" block strong primary
              style="border-radius: 5px; ">
              Decline
            </n-button>
          </div>
        </div>

      </div>
    </div>
  </n-card>
</template>

<style>
.n-card
{
  --n-card-color: #000 !important;
  --n-title-text-color:#63e2b7 !important;
}
.inputEmail:focus 
{
  outline: none;
}

</style>
