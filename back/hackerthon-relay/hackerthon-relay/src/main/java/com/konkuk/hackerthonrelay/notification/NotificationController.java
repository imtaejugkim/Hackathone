package com.konkuk.hackerthonrelay.notification;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

	@Autowired
	private NotificationRepository notificationRepository;

	@Autowired
	private UserRepository userRepository;

	@GetMapping("/{userId}")
	public ResponseEntity<List<NotificationDto>> getUserNotifications(@PathVariable Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		List<Notification> notifications = notificationRepository.findByRecipient(user);

		// Notification을 NotificationDto로 변환
		List<NotificationDto> notificationDtos = notifications.stream().map(notification -> {
			NotificationDto dto = new NotificationDto();
			dto.setId(notification.getId());
			dto.setMessage(notification.getMessage());
			dto.setCreatedAt(notification.getCreatedAt());
			dto.setUserId(notification.getUserId());
			dto.setUserName(notification.getUserName());
			dto.setRemainingTime(notification.getRemainingTime());
			dto.setPostId(notification.getPostId());
			return dto;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(notificationDtos);
	}

}
