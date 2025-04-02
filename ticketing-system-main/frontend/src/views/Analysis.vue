<script setup>
import { ref, onMounted, watch } from "vue";
import { useAnalysisStore } from "../stores/analysis";
import { Chart, registerables } from "chart.js";

Chart.register(...registerables);

const analysisStore = useAnalysisStore();
const chartInstance = ref(null);
const selectedChart = ref("bar"); // ✅ Default to Bar Chart

// 📊 Function to Create or Update the Chart
const updateChart = () => {
  const ctx = document.getElementById("ticketChart").getContext("2d");

  if (chartInstance.value) {
    chartInstance.value.destroy();
  }

  chartInstance.value = new Chart(ctx, {
    type: selectedChart.value, // ✅ Switch chart dynamically
    data: {
      labels: ["Total Tickets", "Resolved Tickets"],
      datasets: [
        {
          data: [
            analysisStore.ticketStats.totalTickets || 0,
            analysisStore.ticketStats.resolvedTickets || 0,
          ],
          backgroundColor: ["#3498db", "#2ecc71"],
        },
      ],
    },
    options: { responsive: true, maintainAspectRatio: false },
  });
};

// 📥 Function to Download CSV
const downloadCsv = async () => {
  const result = await analysisStore.exportCsv();
  if (!result.isSuccessful) {
    alert("Failed to download CSV. Please check the backend.");
  }
};

// 🔄 Fetch Data on Page Load
onMounted(async () => {
  await analysisStore.getTicketStats();
  await analysisStore.getTopUsers(); // ✅ Fetch top users
  updateChart();
});

// 🔄 Update chart when dropdown changes
watch(selectedChart, () => {
  updateChart();
});
</script>

<template>
  <div class="analysis-container">
    <div class="header">
      <h1>📊 Ticket Analysis</h1>
      <button class="btn-download" @click="downloadCsv">📥 CSV</button>
    </div>

    <!-- 📦 Ticket Stats Boxes -->
    <div class="stats">
      <div class="stat-card">
        <h2>Total Tickets</h2>
        <p>{{ analysisStore.ticketStats.totalTickets }}</p>
      </div>
      <div class="stat-card">
        <h2>Resolved Tickets</h2>
        <p>{{ analysisStore.ticketStats.resolvedTickets }}</p>
      </div>
    </div>

    <!-- 🔽 Dropdown to Select Chart Type -->
    <div class="chart-selector">
      <label for="chartType">📌 Select Chart Type:</label>
      <select id="chartType" v-model="selectedChart">
        <option value="bar">📊 Bar Chart</option>
        <option value="pie">🥧 Pie Chart</option>
      </select>
    </div>

    <!-- 📊 Chart Section -->
    <div class="chart-container">
      <canvas id="ticketChart"></canvas>
    </div>

    <!-- 🏆 Top Users Handling Tickets -->
    <h2>🏆 Top Users Handling Tickets</h2>
    <ul v-if="analysisStore.topUsers.length">
      <li v-for="user in analysisStore.topUsers" :key="user.userName">
        <strong>{{ user.userName }}</strong>: {{ user.ticketCount }} Tickets
      </li>
    </ul>
    <p v-else>Loading top users...</p> <!-- ✅ Fallback message -->
  </div>
</template>

<style scoped>
.analysis-container {
  max-width: 900px;
  margin: auto;
  background: #ffffff;
  padding: 20px;
  border-radius: 10px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

/* 📦 Ticket Stats Boxes */
.stats {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.stat-card {
  background: #f8f9fa;
  padding: 15px;
  border-radius: 8px;
  text-align: center;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}


/* 🎯 Dropdown Styling */
.chart-selector {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 20px;
  gap: 10px;
}

select {
  padding: 6px 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
}

.chart-container {
  max-width: 600px;
  height: 200px;
  margin: auto;
  padding: 20px;
}

/* 🏆 Top Users Styling */
ul {
  list-style: none;
  padding: 0;
}

ul li {
  padding: 8px 0;
}

.btn-download {
  background-color: #2ecc71;
  color: white;
  border: none;
  padding: 5px 10px;
  font-size: 14px;
  border-radius: 5px;
  cursor: pointer;
  transition: 0.3s;
}

.btn-download:hover {
  background-color: #27ae60;
}
</style>
