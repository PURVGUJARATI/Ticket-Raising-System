<template>
    <div class="notification-wrapper">
      <div 
        v-for="ticket in notifications" 
        :key="ticket.id" 
        class="notification">
        <p>
          Reminder: Ticket "<strong>{{ ticket.title }}</strong>" has its deadline today!
        </p>
      </div>
    </div>
  </template>
  
  <script>
  export default {
    name: 'Notification',
    props: {
      notifications: {
        type: Array,
        required: true
      }
    }
  };
  </script>
  
  <style scoped>
  .notification-wrapper {
    position: fixed;
    top: 20px;
    right: 20px;
    z-index: 1000;
  }
  .notification {
    background: #fffae6;
    border: 1px solid #ffecb3;
    padding: 10px;
    margin-bottom: 10px;
    border-radius: 4px;
  }
  </style>
  