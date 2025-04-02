package com.kett.TicketSystem.analysis;

import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.user.domain.User;

@Service
public class AnalysisService {
    private final TicketRepository ticketRepository;
    private final PhaseRepository phaseRepository;
    private final UserRepository userRepository; 

    public AnalysisService(TicketRepository ticketRepository, PhaseRepository phaseRepository, UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.phaseRepository = phaseRepository;
        this.userRepository = userRepository;
    }

    public Map<String, Object> calculateTicketStats() {
        List<Ticket> tickets = ticketRepository.findAll();

        int totalTickets = tickets.size();

        Map<UUID, String> phaseMap = phaseRepository.findAll()
            .stream().collect(Collectors.toMap(Phase::getId, Phase::getName));

        long resolvedCount = tickets.stream()
            .filter(t -> t.getPhaseId() != null && "DONE".equalsIgnoreCase(phaseMap.get(t.getPhaseId())))
            .count();

        double avgResolutionTime = tickets.stream()
            .filter(t -> t.getPhaseId() != null && "DONE".equalsIgnoreCase(phaseMap.get(t.getPhaseId())))
            .mapToLong(Ticket::getResolutionTimeInHours)
            .average()
            .orElse(0.0);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTickets", totalTickets);
        stats.put("resolvedTickets", resolvedCount);
        stats.put("avgResolutionTime", avgResolutionTime);
        return stats;
    }

    public List<Map<String, Object>> getTopUsers() {
        List<Ticket> tickets = ticketRepository.findAll();

        Map<UUID, Long> userTicketCount = tickets.stream()
                .filter(ticket -> ticket.getAssigneeId() != null) 
                .collect(Collectors.groupingBy(Ticket::getAssigneeId, Collectors.counting()));

        return userTicketCount.entrySet().stream()
                .sorted(Map.Entry.<UUID, Long>comparingByValue().reversed())
                .map(entry -> {
                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("userId", entry.getKey().toString());
                    userMap.put("ticketCount", entry.getValue());
                    return userMap;
                })
                .collect(Collectors.toList());
    }
    

    public String exportCsv() {
        List<Ticket> tickets = ticketRepository.findAll();
        StringBuilder csv = new StringBuilder("Ticket ID,Title,Status,Created At,Resolved At\n");

        for (Ticket ticket : tickets) {
            csv.append(ticket.getId()).append(",")
                .append(ticket.getTitle()).append(",")
                .append(ticket.getStatus()).append(",")
                .append(ticket.getCreationTime()).append(",")
                .append(ticket.getResolvedAt() != null ? ticket.getResolvedAt() : "Not Resolved")
                .append("\n");
        }

        return csv.toString();
    }
}
