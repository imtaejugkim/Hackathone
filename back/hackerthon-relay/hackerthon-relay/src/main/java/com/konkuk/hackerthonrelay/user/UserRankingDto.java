package com.konkuk.hackerthonrelay.user;

// UserRankingDto.java

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserRankingDto {
	private Long userId;
	private String userIdStr;
	private String username;
	private Integer score;
	private String profileUrl;


	public UserRankingDto(Long userId, String userIdStr, String username, Integer score, String profileUrl) {
		this.userId = userId;
		this.userIdStr = userIdStr;
		this.username = username;
		this.score = score;
		this.profileUrl = profileUrl;
	}
}

