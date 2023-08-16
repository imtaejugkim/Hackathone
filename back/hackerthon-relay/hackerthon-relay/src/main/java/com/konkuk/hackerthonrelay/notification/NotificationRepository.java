package com.konkuk.hackerthonrelay.notification;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.konkuk.hackerthonrelay.notification.Notification.NotificationType;
import com.konkuk.hackerthonrelay.user.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByRecipient(User recipient);

	Notification findByPostIdAndTypeAndRecipient(Long postId, NotificationType relay, User findByUserId);
}
