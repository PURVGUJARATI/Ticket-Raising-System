<template>
  <div class="notification" v-if="ticketStore.notifications.length">
    <div
      v-for="note in ticketStore.notifications"
      :key="note.ticketId"
      class="notification-container"
    >
      <strong>Ticket ID:</strong> {{ note.ticketId }}<br />
      <strong>Deadline:</strong> {{ note.deadline }}<br />
      <strong>Message:</strong> {{ note.message }}<br />
      <strong>Created At:</strong> {{ note.createdAt }}<br />
      {{ note.message }}
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useTicketStore } from "../stores/ticket.js";

const ticketStore = useTicketStore();

onMounted(() => {
  ticketStore.checkDeadlines();
});
</script>

<style scoped>

.notification-container {
  margin-top: 20px;
  padding: 10px;
  background-color: #ffcccc;
  border-left: 5px solid red;
  border-radius: 5px;
}

.notification {
  font-size: 16px;
  font-weight: bold;
  padding: 8px;
}
</style>
