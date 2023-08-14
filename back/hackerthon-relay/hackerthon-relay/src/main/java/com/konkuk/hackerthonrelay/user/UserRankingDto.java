package com.konkuk.hackerthonrelay.user;

// UserRankingDto.java

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserRankingDto {
	private Long userId;
	private String username;
	private double score;

	public UserRankingDto(Long userId, String username, double score) {
		this.userId = userId;
		this.username = username;
		this.score = score;
	}
}
