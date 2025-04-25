import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";
import axios from "axios";

const analysisPath = "/api/analysis";

export const useAnalysisStore = defineStore("analysis", () => {
  const ticketStats = ref({ totalTickets: 0, resolvedTickets: 0 });
  const topUsers = ref([]);
  const tickets = ref([]);
  const ticketStatsByPriority = ref({ LOW: 0, MEDIUM: 0, HIGH: 0, URGENT: 0 }); // New ref for priority stats
  const fetchAgent = useFetchAgent();

  const getTicketStats = async () => {
    const response = await fetchAgent.getTicketStats();
    if (response.isSuccessful) {
      ticketStats.value = response.data;
      return { isSuccessful: true, data: response.data };
    } else {
      return { isSuccessful: false, data: response.data };
    }
  };

  const getTopUsers = async () => {
    const response = await fetchAgent.getTopUsers();
    if (response.isSuccessful) {
      topUsers.value = response.data;
      return { isSuccessful: true, data: response.data };
    } else {
      return { isSuccessful: false, data: response.data };
    }
  };

  const getTickets = async () => {
    const response = await fetchAgent.getTicketsByProjectId(); // Assuming project-based tickets
    if (response.isSuccessful) {
      tickets.value = response.data;
      return { isSuccessful: true, data: response.data };
    } else {
      return { isSuccessful: false, data: response.data };
    }
  };

  const getTicketStatsByPriority = async () => {
    try {
      // Attempt to fetch priority stats from the backend
      const response = await fetchAgent.getTicketStatsByPriority();
      if (response.isSuccessful) {
        ticketStatsByPriority.value = response.data;
        return { isSuccessful: true, data: response.data };
      } else {
        throw new Error("Failed to fetch priority stats from API");
      }
    } catch (error) {
      console.warn("API call failed, falling back to local calculation:", error);
      // Fallback: Calculate priority stats from tickets array
      const priorityCounts = { LOW: 0, MEDIUM: 0, HIGH: 0, URGENT: 0 };
      if (tickets.value.length > 0) {
        tickets.value.forEach((ticket) => {
          const priority = ticket.priority || ticket.Priority || "MEDIUM"; // Try common field names
          const upperPriority = priority.toUpperCase();
          if (priorityCounts.hasOwnProperty(upperPriority)) {
            priorityCounts[upperPriority] += 1;
          } else {
            console.warn(`Unknown priority value: ${priority}, defaulting to MEDIUM`);
            priorityCounts["MEDIUM"] += 1;
          }
        });
      } else {
        console.warn("No tickets available for priority calculation");
      }
      ticketStatsByPriority.value = priorityCounts;
      return { isSuccessful: true, data: priorityCounts };
    }
  };

  const exportCsv = async () => {
    try {
      const response = await axios.get(`${analysisPath}/export-csv`, {
        headers: {
          Authorization: "Bearer " + localStorage.getItem("jwt"),
          "Content-Type": "application/json",
        },
        responseType: "blob",
      });

      if (response.status !== 200) {
        throw new Error(`API responded with status: ${response.status}`);
      }

      const text = await response.data.text();
      console.log("🔍 Raw CSV Response:", text);

      const rows = text.split("\n").map(row => row.trim()).filter(row => row !== "");
      const headers = rows.shift().split(",");

      const tickets = rows.map(row => {
        const values = row.split(",");
        return headers.reduce((obj, header, index) => {
          obj[header.trim()] = values[index] ? values[index].trim() : "N/A";
          return obj;
        }, {});
      });

      console.log("🔍 Parsed JSON:", tickets);

      // ✅ Generate new CSV with required columns
      let csvContent = "Assignee Name,Ticket Name,Phase,Priority,Created At,Resolved At\n";

      tickets.forEach(ticket => {
        const assigneeName = ticket["Assignee Name"] || "Unassigned";
        const ticketName = ticket["Title"] || "No Title";
        const phase = ticket["Phase"] || "Unknown";
        const Priority = ticket["Priority"] || "Unknown";
        const createdAt = ticket["Created At"] || "N/A";
        const resolvedAt = ticket["Resolved At"] || "Not Resolved";

        csvContent += `"${assigneeName}","${ticketName}","${phase}","${Priority}","${createdAt}","${resolvedAt}"\n`;
      });

      // ✅ Download the file
      const blob = new Blob([csvContent], { type: "text/csv" });
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = "Ticket-Analysis.csv";
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);

      return { isSuccessful: true };
    } catch (error) {
      console.error("❌ Error exporting CSV:", error);
      alert(`Error exporting CSV: ${error.message}`);
      return { isSuccessful: false, data: error };
    }
  };

  return {
    ticketStats,
    topUsers,
    tickets,
    ticketStatsByPriority,
    getTicketStats,
    getTopUsers,
    getTickets,
    getTicketStatsByPriority,
    exportCsv,
  };
});