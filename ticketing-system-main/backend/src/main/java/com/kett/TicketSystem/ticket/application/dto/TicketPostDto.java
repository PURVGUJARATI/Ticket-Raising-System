package com.kett.TicketSystem.ticket.application.dto;

import com.kett.TicketSystem.ticket.domain.Ticket; // Import for TicketPriority enum
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketPostDto {
    private UUID projectId;
    private String title;
    private String description;
    private LocalDateTime dueTime;
    private List<UUID> assigneeIds = new ArrayList<>();
    private Ticket.TicketPriority priority; // New field for priority
}