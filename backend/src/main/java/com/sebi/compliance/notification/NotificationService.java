package com.sebi.compliance.notification;

import com.sebi.compliance.common.dto.PagedResponse;
import com.sebi.compliance.common.exception.ResourceNotFoundException;
import com.sebi.compliance.user.User;
import com.sebi.compliance.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public PagedResponse<Notification> getUserNotifications(Long userId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Notification> paged = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable);
        return new PagedResponse<>(paged.getContent(), paged.getNumber(), paged.getSize(), paged.getTotalElements(), paged.getTotalPages(), paged.isLast());
    }

    public long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndReadStatusFalse(userId);
    }

    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notif = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));
        notif.setReadStatus(true);
        notificationRepository.save(notif);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        unread.forEach(n -> n.setReadStatus(true));
        notificationRepository.saveAll(unread);
    }

    @Transactional
    public Notification createNotification(Long recipientUserId, String type, String title, String message,
                                           String severity, String entityType, Long entityId) {
        User recipient = userRepository.findById(recipientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", recipientUserId));

        Notification notif = new Notification();
        notif.setRecipient(recipient);
        notif.setType(type);
        notif.setTitle(title);
        notif.setMessage(message);
        notif.setSeverity(severity != null ? severity : "INFO");
        notif.setRelatedEntityType(entityType);
        notif.setRelatedEntityId(entityId);
        notif.setReadStatus(false);

        return notificationRepository.save(notif);
    }
}
