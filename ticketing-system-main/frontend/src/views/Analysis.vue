<script setup>
import { ref, onMounted, watch, nextTick } from "vue";
import { Chart, registerables } from "chart.js";
import { useAnalysisStore } from "../stores/analysis";
import html2canvas from "html2canvas";

Chart.register(...registerables);

const analysisStore = useAnalysisStore();

const chartInstance1 = ref(null); // For the ticket status pie/doughnut chart
const chartInstance2 = ref(null); // For the priority-based pie/doughnut chart
const selectedChartType1 = ref("pie"); // Chart type for ticket status
const selectedChartType2 = ref("pie"); // Chart type for priority

const updateCharts = () => {
  if (chartInstance1.value) {
    chartInstance1.value.destroy();
    chartInstance1.value = null;
  }
  if (chartInstance2.value) {
    chartInstance2.value.destroy();
    chartInstance2.value = null;
  }

  const ctx1 = document.getElementById("chart1");
  const ctx2 = document.getElementById("chart2");

  if (!ctx1 || !ctx2) {
    console.warn("Chart canvas not found");
    return;
  }

  const stats = analysisStore.ticketStats;

  // Chart 1: Ticket Status (Total vs Resolved)
  chartInstance1.value = new Chart(ctx1, {
    type: selectedChartType1.value,
    data: {
      labels: ["Total", "Resolved"],
      datasets: [{
        label: "Ticket Status",
        data: [stats.totalTickets, stats.resolvedTickets],
        backgroundColor: ["#3498db", "#2ecc71"],
        borderWidth: 1
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'right',
          display: true,
          labels: { color: "#fff" }
        },
        title: {
          display: true, position: "top",
          text: "Tickets by Stats", 
          color: "#fff",
          font: {
            size: 13, 
          }
        }
      }
    }
  });

  // Chart 2: Priority-based
  const priorityStats = analysisStore.ticketStatsByPriority || { LOW: 0, MEDIUM: 0, HIGH: 0, URGENT: 0 };
  console.log("Priority Stats for Chart:", priorityStats);
  if (priorityStats.LOW === 0 && priorityStats.MEDIUM === 0 && priorityStats.HIGH === 0 && priorityStats.URGENT === 0) {
    console.warn("No priority data available, chart may not render");
  }
  chartInstance2.value = new Chart(ctx2, {
    type: selectedChartType2.value,
    data: {
      labels: ["Low", "Medium", "High", "Urgent"],
      datasets: [{
        label: "Tickets by Priority",
        data: [priorityStats.LOW, priorityStats.MEDIUM, priorityStats.HIGH, priorityStats.URGENT],
        backgroundColor: ["#2ecc71", "#3498db", "#f1c40f", "#e74c3c"],
        borderWidth: 1
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          position: 'right',
          display: true,
          labels: { color: "#fff" }
        },
        title: {
          display: true,
          text: "Tickets by Priority",
          color: "#fff",
          font: {
            size: 13
          }
        }
      }
    }
  });
};

const downloadCsv = async () => {
  const result = await analysisStore.exportCsv();
  if (!result.isSuccessful) {
    alert("Failed to download CSV. Please check the backend.");
  }
};

const downloadDashboardImage = () => {
  const container = document.querySelector(".main-container");
  if (!container) return;

  html2canvas(container).then((canvas) => {
    const link = document.createElement("a");
    link.download = "Dashboard.png";
    link.href = canvas.toDataURL("image/png");
    link.click();
  });
};

onMounted(async () => {
  await analysisStore.getTicketStats();
  // await analysisStore.getTickets(); // Ensure tickets are fetched for fallback
  await analysisStore.getTicketStatsByPriority();
  await analysisStore.getTopUsers();
  await nextTick();
  updateCharts();
  console.log("Ticket Stats:", analysisStore.ticketStats);
  console.log("Tickets:", analysisStore.tickets); // Debug tickets data
  console.log("Priority Stats:", analysisStore.ticketStatsByPriority);
});

watch(selectedChartType1, () => {
  if (chartInstance1.value) {
    chartInstance1.value.destroy();
    chartInstance1.value = null;
  }
  updateCharts();
});

watch(selectedChartType2, () => {
  if (chartInstance2.value) {
    chartInstance2.value.destroy();
    chartInstance2.value = null;
  }
  updateCharts();
});
</script>

<template>
  <!-- Floating Download Buttons -->
  <div class="floating-buttons">
    <button @click="downloadDashboardImage">Download Dashboard</button>
    <button @click="downloadCsv">Download CSV</button>
  </div>

  <div class="main-container">
    <!-- Top Stats -->
    <div class="top-container">
      <div class="stats-box">
        <h2>Total Tickets</h2>
        <p>{{ analysisStore.ticketStats.totalTickets }}</p>
      </div>
      <div class="stats-box">
        <h2>Resolved Tickets</h2>
        <p>{{ analysisStore.ticketStats.resolvedTickets }}</p>
      </div>
    </div>

    <!-- Charts Section -->
    <div class="charts-container">
      <!-- Pie/Doughnut Chart (Ticket Status) -->
      <div class="stats-box chart-box">
        <div class="chart-container">
          <canvas id="chart1"></canvas>
        </div>
      </div>

      <!-- Pie/Doughnut Chart (Priority-based) -->
      <div class="stats-box chart-box">
        <div class="chart-container">
          <canvas id="chart2"></canvas>
          <p v-if="analysisStore.ticketStatsByPriority.LOW === 0 && analysisStore.ticketStatsByPriority.MEDIUM === 0 && analysisStore.ticketStatsByPriority.HIGH === 0 && analysisStore.ticketStatsByPriority.URGENT === 0" class="no-data-message">
            No priority data available
          </p>
        </div>
      </div>

      <!-- Top Users -->
      <div class="stats-box chart-box">
        <h2>🏆 Top Users</h2>
        <div class="user-list">
        <ul v-if="analysisStore.topUsers.length">
          <li v-for="user in analysisStore.topUsers" :key="user.userName">
            <strong>{{ user.userName }}</strong> : {{ user.ticketCount }} Tickets
          </li>
        </ul>
        <p v-else>Loading...</p>
      </div>
    </div>
  </div>
</div>
</template>

<style scoped>
.main-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  /* max-width: 1200px; */
  margin: 0.1px auto;
  padding: 32.2px;
  background-color: #1e1e1e; /* Dark background */
  color: #fff; /* White text */
}

.top-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
  width: 100%;
  gap: 20px;
  margin-bottom: 30px;
}

.stats-box {
  flex: 1;
  background: #2c2c2c; /* Darker card background */
  padding: 20px;
  text-align: center;
  border-radius: 10px;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3); /* Enhanced shadow */
  font-size: 1.0rem;
}

.charts-container {
  color: #fff;
  display: flex;
  gap: 20px;
  width: 100%;
  flex-wrap: wrap;
}

.chart-box {
  display: flex;
  flex-direction: column;
  flex: 1;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
}

.chart-container {
  position: relative;
  height: 300px;
  width: 100%;
  margin-top: 15px;
}

.no-data-message {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: #ccc;
  font-style: italic;
}

.user-list {
  max-height: 260px; /* Adjust based on your layout */
  overflow-y: auto;
  padding-right: 5px;
}

.user-list::-webkit-scrollbar {
  width: 6px;
}

.user-list::-webkit-scrollbar-thumb {
  background-color: #555;
  border-radius: 4px;
}


select {
  width: 100%;
  max-width: 200px;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #444; /* Darker border */
  background-color: #2c2c2c; /* Match card background */
  color: #fff; /* White text */
  margin: 0 auto;
}

select option {
  background-color: #2c2c2c; /* Ensure dropdown options match */
  color: #fff;
}

ul {
  list-style: none;
  padding: 0;
  margin-top: 15px;
}

li {
  padding: 8px 0;
  border-bottom: 1px solid #444; /* Darker border */
  color: #fff;
}

strong {
  color: #63e2b7; /* Green accent for emphasis */
}

/* Floating download buttons */
.floating-buttons {
  position: fixed;
  top: 20px;
  right: 100px;
  z-index: 1000;
  display: flex;
  gap: 12px;
}

.floating-buttons button {
  padding: 10px 14px;
  background-color: #0099ff; /* Blue from dashboard buttons */
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 999px;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.3);
  transition: background-color 0.3s ease;
}

.floating-buttons button:hover {
  background-color: #2980b9; /* Darker blue on hover */
}

@media (max-width: 900px) {
  .top-container,
  .charts-container {
    flex-wrap: wrap;
  }

  .stats-box {
    min-width: calc(50% - 10px);
  }

  .floating-buttons {
    flex-direction: column;
    right: 10px;
  }
}

@media (max-width: 600px) {
  .stats-box {
    min-width: 100%;
  }
}
</style>