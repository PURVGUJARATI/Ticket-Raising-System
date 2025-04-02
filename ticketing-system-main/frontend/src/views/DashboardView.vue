<template>
  <div>
    <h1 style="padding-left: 25px;">Hello, my friend. Welcome to Dashboard.</h1>

    <!-- Notification Section -->
    <div v-if="ticketStore.notifications.length" class="notification-container">
      <div v-for="notification in ticketStore.notifications" :key="notification.ticketId" class="notification">
        🔔 {{ notification.message }}
      </div>
    </div>
  </div>
</template>

<script setup>
import { onMounted } from "vue";
import { useTicketStore } from "@/stores/ticket.js";

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
