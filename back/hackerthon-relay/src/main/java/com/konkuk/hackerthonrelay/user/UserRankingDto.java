package com.konkuk.hackerthonrelay.user;

// UserRankingDto.java

public class UserRankingDto {
	private Long userId;
	private String username;
	private double score;

	public UserRankingDto(Long userId, String username, double score) {
		this.userId = userId;
		this.username = username;
		this.score = score;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

}
