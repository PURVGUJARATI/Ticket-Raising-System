package com.kett.TicketSystem.ticket.application;

import com.kett.TicketSystem.common.DtoMapper;
import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.ticket.application.dto.TicketPatchDto;
import com.kett.TicketSystem.ticket.application.dto.TicketPostDto;
import com.kett.TicketSystem.ticket.application.dto.TicketResponseDto;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.domain.TicketDomainService;
import com.kett.TicketSystem.user.domain.User;
import com.kett.TicketSystem.user.repository.UserRepository;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import com.kett.TicketSystem.user.domain.exceptions.UserException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TicketApplicationService {
    private final TicketDomainService ticketDomainService;
    private final DtoMapper dtoMapper;
    private final UserRepository userRepository;
    private final TicketRepository ticketRepository;

    @Autowired
    public TicketApplicationService(
            TicketDomainService ticketDomainService,
            DtoMapper dtoMapper,
            UserRepository userRepository,
            TicketRepository ticketRepository
    ) {
        this.ticketDomainService = ticketDomainService;
        this.dtoMapper = dtoMapper;
        this.userRepository = userRepository;
        this.ticketRepository = ticketRepository;
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)))")
    public TicketResponseDto getTicketById(UUID id) {
        Ticket ticket = ticketDomainService.getTicketById(id);
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByPhaseIdOfTicket(#phaseId)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByPhaseIdOfTicket(#phaseId)))")
    public List<TicketResponseDto> getTicketsByPhaseId(UUID phaseId) {
        List<Ticket> tickets = ticketDomainService.getTicketsByPhaseId(phaseId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAuthority('ROLE_USER_'.concat(#assigneeId))")
    public List<TicketResponseDto> getTicketsByAssigneeId(UUID assigneeId) {
        List<Ticket> tickets = ticketDomainService.getTicketsByAssigneeId(assigneeId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#projectId))")
    public List<TicketResponseDto> getTicketsByProjectId(UUID projectId) {
        List<Ticket> tickets = ticketDomainService.getTicketsByProjectId(projectId);
        return dtoMapper.mapTicketListToTicketResponseDtoList(tickets);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(#ticketPostDto.projectId), " +
            "'ROLE_PROJECT_MEMBER_'.concat(#ticketPostDto.projectId))")
    public TicketResponseDto addTicket(TicketPostDto ticketPostDto, EmailAddress postingUserEmail) {
        Ticket ticket = ticketDomainService.addTicket(
                dtoMapper.mapTicketPostDtoToTicket(ticketPostDto, null), // Priority is mapped here via DtoMapper
                postingUserEmail
        );
        return dtoMapper.mapTicketToTicketResponseDto(ticket);
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)))")
    public void patchTicketById(UUID id, TicketPatchDto ticketPatchDto) {
        ticketDomainService.patchTicket(
                id,
                ticketPatchDto.getTitle(),
                ticketPatchDto.getDescription(),
                ticketPatchDto.getDueTime(),
                ticketPatchDto.getPhaseId(),
                ticketPatchDto.getAssigneeIds(),
                ticketPatchDto.getPriority() // Pass priority to TicketDomainService
        );
    }

    @PreAuthorize("hasAnyAuthority(" +
            "'ROLE_PROJECT_ADMIN_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)), " +
            "'ROLE_PROJECT_MEMBER_'.concat(@ticketDomainService.getProjectIdByTicketId(#id)))")
    public void deleteTicketById(UUID id) {
        ticketDomainService.deleteTicketById(id);
    }

    public List<TicketResponseDto> getTicketsDueTodayForAssignee(EmailAddress emailAddress) {
        User user = userRepository.findByEmailEquals(emailAddress) // Use injected userRepository
                .orElseThrow(() -> new UserException("No user found with email: " + emailAddress));
        UUID userId = user.getId();
        List<Ticket> tickets = ticketDomainService.getTicketsByAssigneeId(userId);

        LocalDate today = LocalDate.now();

        return tickets.stream()
                .filter(ticket -> ticket.getDueTime() != null && ticket.getDueTime().toLocalDate().isEqual(today))
                .map(dtoMapper::mapTicketToTicketResponseDto)
                .collect(Collectors.toList());
    }
}