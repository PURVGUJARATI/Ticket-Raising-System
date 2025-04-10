<script setup>
import { ref, onMounted, watch, nextTick } from "vue";
import { Chart, registerables } from "chart.js";
import { useAnalysisStore } from "../stores/analysis";
import html2canvas from "html2canvas";

Chart.register(...registerables);

const analysisStore = useAnalysisStore();

const chartInstance1 = ref(null);
const chartInstance2 = ref(null);
const selectedChartType = ref("pie");

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

  chartInstance1.value = new Chart(ctx1, {
    type: "bar",
    data: {
      labels: ["Total Tickets", "Resolved Tickets"],
      datasets: [{
        label: "Tickets",
        data: [stats.totalTickets, stats.resolvedTickets],
        backgroundColor: ["#3498db", "#2ecc71"],
      }],
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: { display: false }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: { stepSize: 1 }
        }
      }
    }
  });

  chartInstance2.value = new Chart(ctx2, {
    type: selectedChartType.value,
    data: {
      labels: ["Total Tickets", "Resolved Tickets"],
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
          display: true
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
    link.download = "dashboard-screenshot.png";
    link.href = canvas.toDataURL("image/png");
    link.click();
  });
};

onMounted(async () => {
  await analysisStore.getTicketStats();
  await analysisStore.getTopUsers();
  await nextTick();
  updateCharts();
});

watch(selectedChartType, () => {
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
      <!-- Bar Chart -->
      <div class="stats-box chart-box">
        <div class="chart-container">
          <canvas id="chart1"></canvas>
        </div>
      </div>

      <!-- Pie/Doughnut Chart -->
      <div class="stats-box chart-box">
        <select v-model="selectedChartType">
          <option value="pie">Pie</option>
          <option value="doughnut">Doughnut</option>
        </select>
        <div class="chart-container">
          <canvas id="chart2"></canvas>
        </div>
      </div>

      <!-- Top Users -->
      <div class="stats-box chart-box">
        <h2>🏆 Top Users</h2>
        <ul v-if="analysisStore.topUsers.length">
          <li v-for="user in analysisStore.topUsers" :key="user.userName">
            <strong>{{ user.userName }}</strong>: {{ user.ticketCount }} Tickets
          </li>
        </ul>
        <p v-else>Loading...</p>
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
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
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
  background: #f9f9f9;
  padding: 20px;
  text-align: center;
  border-radius: 10px;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
  font-size: 1.0rem;
}

.charts-container {
  display: flex;
  gap: 20px;
  width: 100%;
  flex-wrap: wrap;
}

.chart-box {
  display: flex;
  flex-direction: column;
  flex: 1;
}

.chart-container {
  position: relative;
  height: 300px;
  width: 100%;
  margin-top: 15px;
}

select {
  width: 100%;
  max-width: 200px;
  padding: 8px 12px;
  border-radius: 4px;
  border: 1px solid #ddd;
  background-color: #f9f9f9;
  margin: 0 auto;
}

ul {
  list-style: none;
  padding: 0;
  margin-top: 15px;
}

li {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

/* Floating download buttons */
.floating-buttons {
  position: fixed;
  top: 20px;
  right: 100px; /* moved to the left to avoid overlapping */
  z-index: 1000;
  display: flex;
  gap: 12px;
}

.floating-buttons button {
  padding: 10px 14px;
  background-color: #3498db;
  color: white;
  font-weight: bold;
  border: none;
  border-radius: 999px; /* full-rounded buttons */
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.15);
  transition: background-color 0.3s ease;
}

.floating-buttons button:hover {
  background-color: #2980b9;
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
