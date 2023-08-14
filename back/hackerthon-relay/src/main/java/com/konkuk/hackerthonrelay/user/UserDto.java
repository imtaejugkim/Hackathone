package com.konkuk.hackerthonrelay.user;

import java.util.List;

public class UserDto {

	private Long userId;
	private String email;
	private String username;
	private String userIdString; // String 타입의 userId 필드
	private int relayReceivedCount;
	private Integer score;
	private String profilePictureUrl;
	private String backgroundPictureUrl;
	private List<Long> createdPostIds;
	private List<Long> followerIds;
	private List<Long> followingIds;
	private boolean success;
	private boolean selfProfile = false;
	private boolean editProfile = false;
	private boolean following = false;

	// 기본 생성자
	public UserDto() {
	}

	// 모든 필드를 인자로 받는 생성자
	public UserDto(Long userId, String email, String username, Integer score, String profilePictureUrl,
			String backgroundPictureUrl) {
		this.userId = userId;
		this.email = email;
		this.username = username;
		this.score = score;
		this.profilePictureUrl = profilePictureUrl;
		this.backgroundPictureUrl = backgroundPictureUrl;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getScore() {
		return score;
	}

	public void setScore(Integer score) {
		this.score = score;
	}

	public String getProfilePictureUrl() {
		return profilePictureUrl;
	}

	public void setProfilePictureUrl(String profilePictureUrl) {
		this.profilePictureUrl = profilePictureUrl;
	}

	public String getBackgroundPictureUrl() {
		return backgroundPictureUrl;
	}

	public void setBackgroundPictureUrl(String backgroundPictureUrl) {
		this.backgroundPictureUrl = backgroundPictureUrl;
	}

	public List<Long> getCreatedPostIds() {
		return createdPostIds;
	}

	public void setCreatedPostIds(List<Long> createdPostIds) {
		this.createdPostIds = createdPostIds;
	}

	public List<Long> getFollowerIds() {
		return followerIds;
	}

	public void setFollowerIds(List<Long> followerIds) {
		this.followerIds = followerIds;
	}

	public List<Long> getFollowingIds() {
		return followingIds;
	}

	public void setFollowingIds(List<Long> followingIds) {
		this.followingIds = followingIds;
	}

	public String getUserIdString() {
		return userIdString;
	}

	public void setUserIdString(String userIdString) {
		this.userIdString = userIdString;
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public int getRelayReceivedCount() {
		return relayReceivedCount;
	}

	public void setRelayReceivedCount(int relayReceivedCount) {
		this.relayReceivedCount = relayReceivedCount;
	}

	public boolean isSelfProfile() {
		return selfProfile;
	}

	public void setSelfProfile(boolean selfProfile) {
		this.selfProfile = selfProfile;
	}

	public boolean isEditProfile() {
		return editProfile;
	}

	public void setEditProfile(boolean editProfile) {
		this.editProfile = editProfile;
	}

	public boolean isFollowing() {
		return following;
	}

	public void setFollowing(boolean following) {
		this.following = following;
	}

}
