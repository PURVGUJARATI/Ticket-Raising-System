package com.kett.TicketSystem.analysis;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.project.domain.Project;    
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.user.domain.User;

@Service
public class AnalysisService {
    private final TicketRepository ticketRepository;
    private final PhaseRepository phaseRepository;
    private final UserRepository userRepository; 
    private final ProjectRepository projectRepository; 
    private final Project project;

    public AnalysisService(TicketRepository ticketRepository, PhaseRepository phaseRepository, UserRepository userRepository, ProjectRepository projectRepository) {
        this.ticketRepository = ticketRepository;
        this.phaseRepository = phaseRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
        this.project = projectRepository.findAll().get(0); // Assuming you want the first project for analysis
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

        long overDueTickets = tickets.stream()
            .filter(t -> {
            boolean isDueDatePassed = t.getDueTime() != null && t.getDueTime().isBefore(LocalDate.now().atStartOfDay());
            boolean isNotDone = t.getPhaseId() != null && ! "DONE".equalsIgnoreCase(phaseMap.get(t.getPhaseId()));
            return isDueDatePassed && isNotDone;
        })
        .count();

        long DueIn24Hours = tickets.stream()
            .filter(t -> {
            boolean isDueSoon = t.getDueTime() != null && t.getDueTime().isAfter(LocalDateTime.now()) && t.getDueTime().isBefore(LocalDateTime.now().plusHours(24));
            boolean isNotDone = t.getPhaseId() != null && !"DONE".equalsIgnoreCase(phaseMap.getOrDefault(t.getPhaseId(), ""));
            return isDueSoon && isNotDone;
        })
        .count();

        long UnAssignedTickets = tickets.stream()
            .filter(t -> t.getAssigneeId() == null && t.getPhaseId() != null && !"DONE".equalsIgnoreCase(phaseMap.get(t.getPhaseId())))
            .count();

        long UrgentOpenTickets = tickets.stream()
            .filter(t -> t.getPriority() != null && t.getPriority().toString().equalsIgnoreCase("URGENT") && t.getPhaseId() != null && !"DONE".equalsIgnoreCase(phaseMap.get(t.getPhaseId())))
            .count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalTickets", totalTickets);
        stats.put("openTickets", totalTickets - resolvedCount);
        stats.put("resolvedTickets", resolvedCount);
        stats.put("avgResolutionTime", avgResolutionTime);
        stats.put("overdueTickets", overDueTickets);
        stats.put("Ticketsduein24", DueIn24Hours);
        stats.put("unassignedTickets", UnAssignedTickets);
        stats.put("urgentTickets", UrgentOpenTickets);
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
    
        // Preload users and phases for quick lookup
        Map<UUID, String> userMap = userRepository.findAll()
                .stream().collect(Collectors.toMap(User::getId, User::getName));
    
        Map<UUID, String> phaseMap = phaseRepository.findAll()
                .stream().collect(Collectors.toMap(Phase::getId, Phase::getName));
    
        // Create CSV header
        StringBuilder csv = new StringBuilder("Title,Description,Project Name,Assignee Name,Phase,Priority,Due Date,Created At,Resolved At\n");
    
        for (Ticket ticket : tickets) {
            String title = ticket.getTitle() != null ? ticket.getTitle() : "No Title";
            String description = ticket.getDescription() != null ? ticket.getDescription() : "No Description";
            String projectName = project.getName() != null ? project.getName().toString() : "No Project";
            String assigneeName = ticket.getAssigneeId() != null ? userMap.getOrDefault(ticket.getAssigneeId(), "Unknown") : "Unassigned";
            String phaseName = ticket.getPhaseId() != null ? phaseMap.getOrDefault(ticket.getPhaseId(), "No Phase") : "No Phase";
            String Priority = ticket.getPriority() != null ? ticket.getPriority().toString() : "No Priority";
            String dueTime = ticket.getDueTime() != null ? ticket.getDueTime().toString() : "No Due Time";
            String resolvedAt = ticket.getResolvedAt() != null ? ticket.getResolvedAt().toString() : "Not Resolved";
    
            csv.append(ticket.getTitle()).append(",")
               .append(ticket.getDescription()).append(",")
               .append(projectName).append(",")
               .append(assigneeName).append(",")
               .append(phaseName).append(",")
               .append(Priority).append(",")
               .append(ticket.getDueTime()).append(",")
               .append(ticket.getCreationTime()).append(",")
               .append(resolvedAt).append("\n");
        }
        return csv.toString();
    }    

    public Map<String, Long> getTicketStatsByPriority() {
        List<Ticket> tickets = ticketRepository.findAll();
        return tickets.stream()
            .filter(ticket -> ticket.getPriority() != null)
            .collect(Collectors.groupingBy(
                ticket -> ticket.getPriority().toString().toUpperCase(),
                Collectors.counting()
            ));
    }
}