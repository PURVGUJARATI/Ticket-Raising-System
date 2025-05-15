package com.kett.TicketSystem.ticket.domain;

import com.kett.TicketSystem.ticket.domain.exceptions.TicketException;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Getter
    @Setter(AccessLevel.PROTECTED)
    @Column(length = 16)
    private UUID id;

    @Getter
    private String title;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter(AccessLevel.PROTECTED)
    private LocalDateTime creationTime;

    @Getter
    private LocalDateTime dueTime;

    @Getter
    @Column(length = 16)
    private UUID projectId;

    @Getter
    @Setter
    @Column(length = 16)
    private UUID phaseId;

    @Getter
    @Type(type = "uuid-char")
    @ElementCollection(targetClass = UUID.class, fetch = FetchType.EAGER)
    private List<UUID> assigneeIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TicketStatus status;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TicketPriority priority;

    // Enums
    public enum TicketStatus {
        OPEN, IN_PROGRESS, DONE, CLOSED
    }

    public enum TicketPriority {
        LOW, MEDIUM, HIGH, URGENT
    }

    // Constructor
    public Ticket(String title, String description, LocalDateTime dueTime, UUID projectId, UUID phaseId, List<UUID> assigneeIds) {
        this.setTitle(title);
        this.description = description;
        this.creationTime = LocalDateTime.now();
        this.setDueTime(dueTime);
        this.setPhaseId(phaseId);
        this.setProjectId(projectId);
        this.setAssigneeIds(assigneeIds);
        this.status = TicketStatus.OPEN;
        this.priority = TicketPriority.MEDIUM;
    }

    // Validation and setters
    public void setTitle(String title) {
        if (title == null || title.isEmpty()) {
            throw new TicketException("Title must not be null or empty");
        }
        this.title = title;
    }

    public void setDueTime(LocalDateTime newDueTime) {
        if (newDueTime != null && newDueTime.isBefore(LocalDateTime.now())) {
            throw new TicketException("Due time cannot be in the past");
        }
        this.dueTime = newDueTime;
    }

    protected void setProjectId(UUID projectId) {
        if (projectId == null) {
            throw new TicketException("Project ID must not be null");
        }
        this.projectId = projectId;
    }

    public void setAssigneeIds(List<UUID> assigneeIds) {
        if (assigneeIds == null) {
            throw new TicketException("Assignee IDs must not be null (but may be empty)");
        }
        this.assigneeIds.clear();
        this.assigneeIds.addAll(assigneeIds);
    }

    public void removeAssignee(UUID userId) {
        assigneeIds.remove(userId);
    }

    public Boolean isAssignee(UUID assigneeId) {
        return this.assigneeIds.contains(assigneeId);
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
        if (status == TicketStatus.DONE) {
            this.resolvedAt = LocalDateTime.now();
        } else {
            this.resolvedAt = null;
        }
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public boolean isResolved() {
        return this.status == TicketStatus.DONE;
    }

    public long getResolutionTimeInHours() {
        if (resolvedAt != null) {
            return Duration.between(creationTime, resolvedAt).toHours();
        }
        return 0;
    }

    public UUID getAssigneeId() {
        return assigneeIds.isEmpty() ? null : assigneeIds.get(0);
    }

    public TicketPriority getPriority() {
        return priority;
    }

    public void setPriority(TicketPriority priority) {
        if (priority == null) {
            throw new TicketException("Priority must not be null");
        }
        this.priority = priority;
    }

}
