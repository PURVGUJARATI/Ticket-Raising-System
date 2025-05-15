package com.kett.TicketSystem.membership.repository;

import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MembershipRepository extends JpaRepository<Membership, UUID> {
    List<Membership> findByUserId(UUID userId);
    List<Membership> findByProjectId(UUID projectId);
    List<Membership> findByProjectIdAndStateEquals(UUID projectId, State state);
    List<Membership> findByUserIdAndStateEquals(UUID userId, State state);

    @Query("SELECT m FROM Membership m WHERE m.projectId = :projectId AND m.role = :role AND m.state = 'ACCEPTED'")
    List<Membership> findByProjectIdAndRoleAndAccepted(UUID projectId, Role role);

    Boolean existsByUserIdAndProjectId(UUID userId, UUID projectId);

    Integer countMembershipByProjectIdAndStateEqualsAndRoleEquals(UUID projectId, State state, Role role);

    Long removeById(UUID id);
    List<Membership> deleteByProjectId(UUID projectId);
}