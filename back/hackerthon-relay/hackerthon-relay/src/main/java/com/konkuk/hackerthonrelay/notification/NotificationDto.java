package com.konkuk.hackerthonrelay.notification;

import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDateTime;

@Setter @Getter
public class NotificationDto {
	private Long id;
	private String message;
	private LocalDateTime createdAt;
	private Long userId;
	private String userName;
	private Duration remainingTime;
	private Long postId;
}
