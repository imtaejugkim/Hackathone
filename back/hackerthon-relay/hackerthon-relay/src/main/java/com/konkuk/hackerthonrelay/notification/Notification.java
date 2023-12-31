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
	private String userIdString; // String 형태의 사용자 ID
	private String userName; // 릴레이나 좋아요를 보낸 사용자의 이름
	private Duration remainingTime; // 릴레이에 대한 남은 시간
	private String userProfileUrl; // 작성자의 프로필 사진 URL 필드 추가
	private boolean isActive = true; // 알림이 활성화되어 있는지 나타내는 필드. 기본값은 true입니다.

	private NotificationType type;

	public enum NotificationType {
		LIKE, COMMENT, FOLLOW, RELAY
		// ... 다른 알림 타입들 추후에 추가
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
	}

}
