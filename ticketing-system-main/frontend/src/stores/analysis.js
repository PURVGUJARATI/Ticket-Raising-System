import { ref } from "vue";
import { defineStore } from "pinia";
import { useFetchAgent } from "./fetchAgent";
import axios from "axios";

const analysisPath = "/api/analysis"; // ✅ Define the base API path

export const useAnalysisStore = defineStore("analysis", () => {
  const ticketStats = ref({ totalTickets: 0, resolvedTickets: 0 });
  const topUsers = ref([]);
  const tickets = ref([]); // ✅ Store ticket details
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
    const response = await fetchAgent.getTickets(); // ✅ Fetch tickets
    if (response.isSuccessful) {
      tickets.value = response.data;
      return { isSuccessful: true, data: response.data };
    } else {
      return { isSuccessful: false, data: response.data };
    }
  };

  const exportCsv = async () => {
    try {
      // ✅ Fetch CSV data as text
      const response = await axios.get(`${analysisPath}/export-csv`, { 
        headers: { 
          Authorization: "Bearer " + localStorage.getItem("jwt"),
          "Content-Type": "application/json",
        },
        responseType: "blob", // ✅ Fetch as a Blob
      });
  
      if (response.status !== 200) {
        throw new Error(`API responded with status: ${response.status}`);
      }
  
      // ✅ Convert Blob to Text
      const text = await response.data.text();
      console.log("🔍 Raw CSV Response:", text);
  
      // ✅ Convert CSV to Array of Objects
      const rows = text.split("\n").map(row => row.trim()).filter(row => row !== "");
      const headers = rows.shift().split(",");
  
      const tickets = rows.map(row => {
        const values = row.split(",");
        return headers.reduce((obj, header, index) => {
          obj[header.trim()] = values[index] ? values[index].trim() : "N/A";
          return obj;
        }, {});
      });
  
      console.log("🔍 Parsed JSON:", tickets); // ✅ Check parsed JSON
  
      // ✅ Prepare CSV Headers (With Technician Name)
      let csvContent = "Technician Name,Ticket Name,Status,Resolved At\n";
  
      // ✅ Process each ticket
      tickets.forEach(ticket => {
        const technicianName = ticket["Assigned Technician"] || "Unassigned"; // ✅ Technician name (Make sure this exists in the API response)
        const ticketName = ticket["Title"] || "No Title"; // ✅ Ticket name
        const status = ticket["Status"] || "Unknown"; // ✅ Ticket status
        const resolvedAt = ticket["Resolved At"] || "Not Resolved"; // ✅ Resolved date
  
        csvContent += `${technicianName},${ticketName},${status},${resolvedAt}\n`;
      });
  
      // ✅ Convert to CSV and trigger download
      const blob = new Blob([csvContent], { type: "text/csv" });
      const link = document.createElement("a");
      link.href = URL.createObjectURL(blob);
      link.download = "ticket_analysis.csv";
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
    exportCsv, // ✅ Now exports detailed ticket data
  };
});
