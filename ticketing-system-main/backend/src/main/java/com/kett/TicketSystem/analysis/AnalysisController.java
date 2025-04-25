package com.kett.TicketSystem.analysis;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.*;

@RestController
@RequestMapping("/api/analysis")
@CrossOrigin
public class AnalysisController {
    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/ticket-stats")
    public ResponseEntity<Map<String, Object>> getTicketStats() {
        return ResponseEntity.ok(analysisService.calculateTicketStats());
    }

    @GetMapping("/top-users")
    public ResponseEntity<List<Map<String, Object>>> getTopUsers() {
        return ResponseEntity.ok(analysisService.getTopUsers());
    }

    @GetMapping("/export-csv") // ✅ Make CSV export public
    public ResponseEntity<byte[]> exportCsv() {
        String csvData = analysisService.exportCsv();
        byte[] csvBytes = csvData.getBytes();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ticket_analysis.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .body(csvBytes);
    }

    @GetMapping("/priority-stats")
    public ResponseEntity<Map<String, Long>> getTicketStatsByPriority() {
        return ResponseEntity.ok(analysisService.getTicketStatsByPriority());
    }
} 