// notification.js
import { defineStore } from 'pinia';
import { ref } from 'vue';
import { useFetchAgent } from './fetchAgent';
import { useSessionStore } from './session';

export const useNotificationStore = defineStore('notification', () => {
  const fetchAgent = useFetchAgent();
  const sessionStore = useSessionStore();
  const notifications = ref([]);

  async function fetchNotifications() {
    const email = sessionStore.email;
    console.log('Fetching notifications for email:', email);
    if (!email) {
      console.warn('No email found in sessionStore, skipping fetchNotifications');
      return;
    }
    const response = await fetchAgent.getNotifications(email);
    console.log('Fetch response data:', response.data);
    if (response.isSuccessful) {
      console.log('Before update, notifications:', notifications.value);
      notifications.value = response.data;
      console.log('After update, notifications:', notifications.value);
    } else {
      console.error('Failed to fetch notifications:', response.data);
    }
  }

  async function markAsRead(notificationId) {
    console.log('Marking notification as read:', notificationId);
    try {
      const response = await fetchAgent.patchNotification(notificationId, { isRead: true });
      console.log('Patch response:', response);
      if (response.isSuccessful) {
        notifications.value = []; // Clear to avoid duplicates
        console.log('Cleared notifications before fetch');
        await fetchNotifications(); // Refresh
        console.log('Notifications after refresh:', notifications.value);
        console.log('Badge count should be:', notifications.value.length);
      } else {
        console.error('Failed to mark notification as read:', response.data);
      }
    } catch (error) {
      console.error('Error in markAsRead:', error);
    }
  }

  return { notifications, fetchNotifications, markAsRead };
});