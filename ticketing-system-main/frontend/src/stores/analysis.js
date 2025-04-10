import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";
import axios from "axios";

const analysisPath = "/api/analysis";

export const useAnalysisStore = defineStore("analysis", () => {
  const ticketStats = ref({ totalTickets: 0, resolvedTickets: 0 });
  const topUsers = ref([]);
  const tickets = ref([]);
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
    const response = await fetchAgent.getTickets();
    if (response.isSuccessful) {
      tickets.value = response.data;
      return { isSuccessful: true, data: response.data };
    } else {
      return { isSuccessful: false, data: response.data };
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
      link.download = "ticket_summary.csv";
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
    getTicketStats,
    getTopUsers,
    getTickets,
    exportCsv,
  };
});
