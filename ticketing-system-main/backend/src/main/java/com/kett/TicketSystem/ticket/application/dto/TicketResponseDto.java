package com.kett.TicketSystem.ticket.application.dto;

import com.kett.TicketSystem.ticket.domain.Ticket; // Import for TicketPriority enum
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketResponseDto {
    private UUID id;
    private String title;
    private String description;
    private LocalDateTime creationTime;
    private LocalDateTime dueTime;
    private UUID phaseId;
    private UUID projectId;
    private List<UUID> assigneeIds = new ArrayList<>();
    private Ticket.TicketPriority priority; // New field for priority
}