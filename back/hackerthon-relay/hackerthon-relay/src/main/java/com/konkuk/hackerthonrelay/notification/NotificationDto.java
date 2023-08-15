package com.konkuk.hackerthonrelay.notification;

import java.time.Duration;
import java.time.LocalDateTime;

public class NotificationDto {
	private Long id;
	private String message;
	private LocalDateTime createdAt;
	private Long userId;
	private String userName;
	private Duration remainingTime;
	private Long postId;


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Duration getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(Duration remainingTime) {
		this.remainingTime = remainingTime;
	}

	public Long getPostId() {
		return postId;
	}

	public void setPostId(Long postId) {
		this.postId = postId;
	}

}
