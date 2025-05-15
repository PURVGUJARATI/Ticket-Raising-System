package com.kett.TicketSystem.notification.domain;

import com.kett.TicketSystem.common.domainprimitives.EmailAddress;
import com.kett.TicketSystem.common.exceptions.IllegalStateUpdateException;
import com.kett.TicketSystem.common.exceptions.ImpossibleException;
import com.kett.TicketSystem.membership.domain.Membership;
import com.kett.TicketSystem.membership.domain.Role;
import com.kett.TicketSystem.membership.domain.State;
import com.kett.TicketSystem.membership.domain.events.UnacceptedProjectMembershipCreatedEvent;
import com.kett.TicketSystem.membership.repository.MembershipRepository;
import com.kett.TicketSystem.notification.domain.consumedData.UserDataOfNotification;
import com.kett.TicketSystem.notification.domain.exceptions.NoNotificationFoundException;
import com.kett.TicketSystem.notification.domain.exceptions.NotificationException;
import com.kett.TicketSystem.notification.repository.NotificationRepository;
import com.kett.TicketSystem.notification.repository.UserDataOfNotificationRepository;
import com.kett.TicketSystem.project.domain.Project;
import com.kett.TicketSystem.project.repository.ProjectRepository;
import com.kett.TicketSystem.ticket.domain.Ticket;
import com.kett.TicketSystem.ticket.domain.events.TicketAssignedEvent;
import com.kett.TicketSystem.ticket.domain.events.TicketUnassignedEvent;
import com.kett.TicketSystem.ticket.repository.TicketRepository;
import com.kett.TicketSystem.phase.domain.Phase;
import com.kett.TicketSystem.phase.repository.PhaseRepository;
import com.kett.TicketSystem.user.domain.events.UserCreatedEvent;
import com.kett.TicketSystem.user.domain.events.UserDeletedEvent;
import com.kett.TicketSystem.user.domain.events.UserPatchedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NotificationDomainService {
    private static final Logger logger = LoggerFactory.getLogger(NotificationDomainService.class);

    private final NotificationRepository notificationRepository;
    private final UserDataOfNotificationRepository userDataOfNotificationRepository;
    private final ProjectRepository projectRepository;
    private final TicketRepository ticketRepository;
    private final MembershipRepository membershipRepository;
    private final PhaseRepository phaseRepository;

    public NotificationDomainService(
            NotificationRepository notificationRepository,
            UserDataOfNotificationRepository userDataOfNotificationRepository,
            ProjectRepository projectRepository,
            TicketRepository ticketRepository,
            MembershipRepository membershipRepository,
            PhaseRepository phaseRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.userDataOfNotificationRepository = userDataOfNotificationRepository;
        this.projectRepository = projectRepository;
        this.ticketRepository = ticketRepository;
        this.membershipRepository = membershipRepository;
        this.phaseRepository = phaseRepository;
    }

    public Notification getNotificationById(UUID id) throws NoNotificationFoundException {
        return notificationRepository
                .findById(id)
                .orElseThrow(() -> new NoNotificationFoundException("could not find notification with id: " + id));
    }

    public List<Notification> getNotificationsByRecipientId(UUID recipientId) throws NoNotificationFoundException {
        List<Notification> notifications = notificationRepository.findByRecipientId(recipientId);
        if (notifications.isEmpty()) {
            throw new NoNotificationFoundException("could not find notifications with recipientId: " + recipientId);
        }
        return notifications;
    }

    public List<Notification> getNotificationsByUserEmail(EmailAddress emailAddress) throws NotificationException {
        logger.info("Fetching unread notifications for email: {}", emailAddress);
        List<Notification> notifications = getUnreadNotificationsByRecipientId(
                getUserIdByUserEmailAddress(emailAddress)
        );
        logger.info("Found {} unread notifications", notifications.size());
        return notifications;
    }

    public List<Notification> getUnreadNotificationsByRecipientId(UUID recipientId) throws NoNotificationFoundException {
        List<Notification> notifications = notificationRepository.findByRecipientIdAndIsReadFalse(recipientId);
        if (notifications.isEmpty()) {
            throw new NoNotificationFoundException("could not find notifications with recipientId: " + recipientId);
        }
        return notifications;
    }

    public UUID getGetRecipientIdByNotificationId(UUID id) throws NoNotificationFoundException {
        return this
                .getNotificationById(id)
                .getRecipientId();
    }

    public UUID getUserIdByUserEmailAddress(EmailAddress emailAddress) {
        List<UserDataOfNotification> userData = userDataOfNotificationRepository.findByUserEmailEquals(emailAddress);
        if (userData.isEmpty()) {
            throw new ImpossibleException("No user data found for user: " + emailAddress.toString());
        }
        return userData.get(0).getUserId();
    }

    public void patchById(UUID id, Boolean isRead) throws NoNotificationFoundException, NotificationException, IllegalStateUpdateException {
        logger.info("Patching notification ID: {} with isRead: {}", id, isRead);
        Notification notification = this.getNotificationById(id);
        notification.setIsRead(isRead);
        notificationRepository.save(notification);
        logger.info("Notification ID: {} patched successfully", id);
    }

    public void deleteById(UUID id) throws NoNotificationFoundException {
        Long numOfDeletedNotifications = notificationRepository.removeById(id);
        if (numOfDeletedNotifications == 0) {
            throw new NoNotificationFoundException("Could not find notification with id: " + id);
        }
    }

    public void deleteByRecipientId(UUID recipientId) {
        notificationRepository.deleteByRecipientId(recipientId);
    }

    @EventListener
    @Async
    public void handleUnacceptedProjectMembershipCreatedEvent(UnacceptedProjectMembershipCreatedEvent unacceptedProjectMembershipCreatedEvent) {
        UUID projectId = unacceptedProjectMembershipCreatedEvent.getProjectId();
        String projectName = projectRepository.findById(projectId)
                .map(Project::getName)
                .orElse("Unknown Project (ID: " + projectId + ")");

        String message = "You got invited to project " + projectName + ".";
        Notification notification = new Notification(unacceptedProjectMembershipCreatedEvent.getInviteeId(), message);
        notificationRepository.save(notification);
        logger.info("Saved notification for project membership invite to recipientId: {}", unacceptedProjectMembershipCreatedEvent.getInviteeId());
    }

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketAssignedEvent(TicketAssignedEvent ticketAssignedEvent) {
        UUID ticketId = ticketAssignedEvent.getTicketId();
        UUID projectId = ticketAssignedEvent.getProjectId();

        String ticketTitle = ticketRepository.findById(ticketId)
                .map(Ticket::getTitle)
                .orElse("Unknown Ticket (ID: " + ticketId + ")");
        String projectName = projectRepository.findById(projectId)
                .map(Project::getName)
                .orElse("Unknown Project (ID: " + projectId + ")");

        String message = "You got assigned to ticket ( " + ticketTitle + " ) of project " + projectName + ".";
        Notification notification = new Notification(ticketAssignedEvent.getAssigneeId(), message);
        notificationRepository.save(notification);
        logger.info("Saved notification for ticket assignment to assigneeId: {}", ticketAssignedEvent.getAssigneeId());
    }

    @EventListener
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTicketUnassignedEvent(TicketUnassignedEvent ticketUnassignedEvent) {
        UUID ticketId = ticketUnassignedEvent.getTicketId();
        UUID projectId = ticketUnassignedEvent.getProjectId();

        String ticketTitle = ticketRepository.findById(ticketId)
                .map(Ticket::getTitle)
                .orElse("Unknown Ticket (ID: " + ticketId + ")");
        String projectName = projectRepository.findById(projectId)
                .map(Project::getName)
                .orElse("Unknown Project (ID: " + projectId + ")");

        String message = "Your assignment to ticket ( " + ticketTitle + " ) of project " + projectName + " has been revoked.";
        Notification notification = new Notification(ticketUnassignedEvent.getAssigneeId(), message);
        notificationRepository.save(notification);
        logger.info("Saved notification for ticket unassignment to assigneeId: {}", ticketUnassignedEvent.getAssigneeId());
    }

    @EventListener
    @Async
    public void handleUserCreatedEvent(UserCreatedEvent userCreatedEvent) {
        userDataOfNotificationRepository.save(new UserDataOfNotification(userCreatedEvent.getUserId(), userCreatedEvent.getEmailAddress()));
        logger.info("Saved user data for userId: {}", userCreatedEvent.getUserId());
    }

    @EventListener
    @Async
    public void handleUserPatchedEvent(UserPatchedEvent userPatchedEvent) {
        UserDataOfNotification userDataOfNotification = userDataOfNotificationRepository
                .findByUserId(userPatchedEvent.getUserId())
                .get(0);
        userDataOfNotification.setUserEmail(userPatchedEvent.getEmailAddress());
        userDataOfNotificationRepository.save(userDataOfNotification);
        logger.info("Updated user data for userId: {}", userPatchedEvent.getUserId());
    }

    @EventListener
    @Async
    public void handleUserDeletedEvent(UserDeletedEvent userDeletedEvent) {
        this.deleteByRecipientId(userDeletedEvent.getUserId());
        userDataOfNotificationRepository.deleteByUserId(userDeletedEvent.getUserId());
        logger.info("Deleted notifications and user data for userId: {}", userDeletedEvent.getUserId());
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    // @Scheduled(cron = "0 */1 * * * *") 
    @Async
    @Transactional
    public void sendDueDateNotifications() {
        try {
            logger.info("Starting due date notification task at {}", LocalDateTime.now());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfDay = now.truncatedTo(ChronoUnit.DAYS);
            LocalDateTime endOfDay = startOfDay.plusDays(1).minusNanos(1);
            logger.info("Checking tickets due between {} and {}", startOfDay, endOfDay);

            // Fetch tickets using repository query
            List<Ticket> dueTickets;
            try {
                dueTickets = ticketRepository.findByDueTimeBetweenAndNotResolved(startOfDay, endOfDay);
                logger.info("Found {} tickets with due date today using repository query", dueTickets.size());
            } catch (Exception e) {
                logger.error("Error fetching tickets with repository query: {}", e.getMessage(), e);
                dueTickets = new ArrayList<>();
            }

            // Filter tickets with assignees
            dueTickets = dueTickets.stream()
                    .filter(ticket -> ticket.getAssigneeIds() != null && !ticket.getAssigneeIds().isEmpty())
                    .toList();
            logger.info("Filtered to {} tickets with non-empty assigneeIds", dueTickets.size());

            // Process each ticket
            for (Ticket ticket : dueTickets) {
                logger.info("Processing ticket {}: title={}, dueTime={}, assigneeIds={}",
                        ticket.getId(), ticket.getTitle(), ticket.getDueTime(), ticket.getAssigneeIds());

                List<UUID> assigneeIds = ticket.getAssigneeIds();
                UUID projectId = ticket.getProjectId();
                String ticketTitle = ticket.getTitle();
                String projectName = projectRepository.findById(projectId)
                        .map(Project::getName)
                        .orElse("Unknown Project (ID: " + projectId + ")");
                String message = "Reminder: Today is the due date for ticket (" + ticketTitle + ") in project " + projectName + ".";

                // Create notifications for valid assignees
                List<Notification> notifications = new ArrayList<>();
                for (UUID assigneeId : assigneeIds) {
                    if (assigneeId == null) {
                        logger.warn("Null assigneeId found for ticket {}, skipping", ticket.getId());
                        continue;
                    }
                    // Validate assignee exists in user_data_of_notification
                    List<UserDataOfNotification> userData = userDataOfNotificationRepository.findByUserId(assigneeId);
                    if (userData.isEmpty()) {
                        logger.warn("No user data found for assigneeId {} for ticket {}, skipping notification", assigneeId, ticket.getId());
                        continue;
                    }
                    logger.info("Creating notification for assignee {} for ticket {} with message: {}", assigneeId, ticket.getId(), message);
                    notifications.add(new Notification(assigneeId, message));
                }

                // Save notifications
                if (!notifications.isEmpty()) {
                    try {
                        notificationRepository.saveAll(notifications);
                        logger.info("Successfully saved {} notifications for ticket {}", notifications.size(), ticket.getId());
                    } catch (Exception e) {
                        logger.error("Failed to save notifications for ticket {}: {}", ticket.getId(), e.getMessage(), e);
                    }
                } else {
                    logger.warn("No valid notifications created for ticket {}", ticket.getId());
                }
            }

            if (dueTickets.isEmpty()) {
                logger.warn("No tickets found with due date today and valid assignees, no notifications sent");
            }
            logger.info("Completed due date notification task at {}", LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Unexpected error in sendDueDateNotifications: {}", e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 * * * *") // Runs every hour
    // @Scheduled(cron = "0 */1 * * * *") 
    @Async
    @Transactional
    public void sendOverdueNotificationsToAdmin() {
        try {
            logger.info("Starting overdue notification task for admins at {}", LocalDateTime.now());
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startOfToday = now.truncatedTo(ChronoUnit.DAYS);
            logger.info("Checking tickets overdue before {}", startOfToday);

            // Fetch overdue tickets
            List<Ticket> overdueTickets;
            try {
                overdueTickets = ticketRepository.findOverdueAndNotResolved(startOfToday);
                logger.info("Found {} overdue tickets using repository query", overdueTickets.size());
            } catch (Exception e) {
                logger.error("Error fetching overdue tickets with repository query: {}", e.getMessage(), e);
                overdueTickets = new ArrayList<>();
            }

            // Filter tickets with assignees and phase name not DONE
            overdueTickets = overdueTickets.stream()
                    .filter(ticket -> ticket.getAssigneeIds() != null && !ticket.getAssigneeIds().isEmpty())
                    .filter(ticket -> {
                        UUID phaseId = ticket.getPhaseId();
                        if (phaseId == null) {
                            logger.warn("Null phaseId for ticket {}, skipping", ticket.getId());
                            return false;
                        }
                        return phaseRepository.findById(phaseId)
                                .map(phase -> !phase.getName().equals("DONE"))
                                .orElseGet(() -> {
                                    logger.warn("No phase found for phaseId {} for ticket {}, skipping", phaseId, ticket.getId());
                                    return false;
                                });
                    })
                    .toList();
            logger.info("Filtered to {} overdue tickets with non-empty assigneeIds and phase not DONE", overdueTickets.size());

            // Process each ticket
            for (Ticket ticket : overdueTickets) {
                UUID phaseId = ticket.getPhaseId();
                String phaseName = phaseRepository.findById(phaseId)
                        .map(Phase::getName)
                        .orElse("Unknown Phase (ID: " + phaseId + ")");
                logger.info("Processing overdue ticket {}: title={}, dueTime={}, phase={}, assigneeIds={}",
                        ticket.getId(), ticket.getTitle(), ticket.getDueTime(), phaseName, ticket.getAssigneeIds());

                UUID projectId = ticket.getProjectId();
                String ticketTitle = ticket.getTitle();

                // Fetch project
                Project project = projectRepository.findById(projectId)
                        .orElse(null);
                if (project == null) {
                    logger.warn("No project found for ticket {} with projectId {}, skipping", ticket.getId(), projectId);
                    continue;
                }
                String projectName = project.getName();

                // Fetch admin from membership
                // Fetch admin from membership
                List<Membership> adminMemberships = membershipRepository.findByProjectIdAndRoleAndAccepted(projectId, Role.ADMIN);
                if (adminMemberships.isEmpty()) {
                    logger.warn("No accepted admin found for project {} (ID: {}) for ticket {}, skipping", projectName, projectId, ticket.getId());
                    continue;
                }
                logger.info("Found {} accepted admin memberships for project {} (ID: {}) for ticket {}",
                        adminMemberships.size(), projectName, projectId, ticket.getId());

                // Create notifications for all admins
                for (Membership adminMembership : adminMemberships) {
                    UUID adminId = adminMembership.getUserId();
                    logger.info("Processing adminId {} for project {} (ID: {}) for ticket {}", adminId, projectName, projectId, ticket.getId());

                    // Validate admin exists in user_data_of_notification
                    List<UserDataOfNotification> adminData = userDataOfNotificationRepository.findByUserId(adminId);
                    if (adminData.isEmpty()) {
                        logger.warn("No user data found for adminId {} for project {} (ID: {}) for ticket {}, skipping notification", 
                                adminId, projectName, projectId, ticket.getId());
                        continue;
                    }
                    logger.info("Validated user data for adminId {} with email {}", adminId, adminData.get(0).getUserEmail());

                    // Create notification
                    String message = "Alert: Ticket (" + ticketTitle + ") in project " + projectName + 
                            " passed its due date (" + ticket.getDueTime().toLocalDate() + ") and remains unresolved.";
                    Notification notification = new Notification(adminId, message);
                    logger.info("Creating notification for admin {} for overdue ticket {} with message: {}", 
                            adminId, ticket.getId(), message);

                    // Save notification
                    try {
                        notificationRepository.save(notification);
                        logger.info("Successfully saved notification for admin {} for overdue ticket {}", adminId, ticket.getId());
                    } catch (Exception e) {
                        logger.error("Failed to save notification for admin {} for overdue ticket {}: {}", 
                                adminId, ticket.getId(), e.getMessage(), e);
                    }
                }
            }

            if (overdueTickets.isEmpty()) {
                logger.warn("No overdue tickets found with valid assignees and phase not DONE, no admin notifications sent");
            }
            logger.info("Completed overdue notification task for admins at {}", LocalDateTime.now());
        } catch (Exception e) {
            logger.error("Unexpected error in sendOverdueNotificationsToAdmin: {}", e.getMessage(), e);
        }
    }
}