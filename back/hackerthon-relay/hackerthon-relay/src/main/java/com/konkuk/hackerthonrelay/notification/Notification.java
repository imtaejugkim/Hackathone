package com.konkuk.hackerthonrelay.notification;

import java.time.Duration;
import java.time.LocalDateTime;

import com.konkuk.hackerthonrelay.user.User;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	private User recipient;

	private String message;

	private LocalDateTime createdAt;

	private Long postId; // 릴레이 알림에 대한 postId
	private Long userId; // 릴레이나 좋아요를 보낸 사용자 ID
	private String userName; // 릴레이나 좋아요를 보낸 사용자의 이름
	private Duration remainingTime; // 릴레이에 대한 남은 시간

	private NotificationType type;

	public enum NotificationType {
		LIKE, COMMENT, FOLLOW,
		// ... 다른 알림 타입들 추후에 추가
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

}
