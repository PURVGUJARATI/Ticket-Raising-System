package com.kett.TicketSystem.ticket.repository;

import com.kett.TicketSystem.ticket.domain.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {
    List<Ticket> findByPhaseId(UUID phaseId);
    List<Ticket> findByProjectId(UUID projectId);
    List<Ticket> findByAssigneeIdsContaining(UUID assigneeId);
    List<Ticket> findByPhaseIdInAndAssigneeIdsContaining(List<UUID> phaseIds, UUID assigneeId);
    List<Ticket> findByAssigneeIdsContainingAndDueTimeBetween(UUID assigneeId, LocalDateTime startOfDay, LocalDateTime endOfDay);
    Boolean existsByPhaseIdEquals(UUID phaseId);
    void deleteByProjectId(UUID projectId);
    Long removeById(UUID id);

    @Query("SELECT DISTINCT t FROM Ticket t JOIN t.assigneeIds ai " +
           "WHERE t.dueTime >= :startOfDay AND t.dueTime <= :endOfDay AND t.status != 'DONE'")
    List<Ticket> findByDueTimeBetweenAndNotResolved(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("SELECT t FROM Ticket t " +
           "WHERE t.dueTime < :beforeDate AND t.status != 'DONE'")
    List<Ticket> findOverdueAndNotResolved(LocalDateTime beforeDate);
}