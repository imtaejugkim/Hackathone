package com.konkuk.hackerthonrelay.pictureupload;

import com.konkuk.hackerthonrelay.comment.CommentDto;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class PostDto {
	private Long id;
	private ArrayList<String> images = new ArrayList<>();
	private String mainImage;
	private Integer theme;
	private Integer likeCount; // 따로 반환이 필요한 경우에 사용
	private LocalDateTime date;
	private String text;
	private List<CommentDto> comments;
	private Integer musicNum;
	private List<Long> participantUserIds = new ArrayList<>();
	private List<String> participantUserIdStrings = new ArrayList<>();
	private List<String> participantsUserProfileUrl = new ArrayList<>();

	private boolean likedByCurrentUser;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ArrayList<String> getImages() {
		return images;
	}

	public void setImages(ArrayList<String> images) {
		this.images = images;
	}

	public Integer getTheme() {
		return theme;
	}

	public void setTheme(Integer theme) {
		this.theme = theme;
	}

	public String getMainImage() {
		return mainImage;
	}

	public void setMainImage(String mainImage) {
		this.mainImage = mainImage;
	}

	public Integer getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(Integer likeCount) {
		this.likeCount = likeCount;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<CommentDto> getComments() {
		return comments;
	}

	public void setComments(List<CommentDto> comments) {
		this.comments = comments;
	}

	public Integer getMusicNum() {
		return musicNum;
	}

	public void setMusicNum(Integer musicNum) {
		this.musicNum = musicNum;
	}

	public List<Long> getParticipantUserIds() {
		return participantUserIds;
	}

	public void setParticipantUserIds(List<Long> participantUserIds) {
		this.participantUserIds = participantUserIds;
	}

	public boolean isLikedByCurrentUser() {
		return likedByCurrentUser;
	}

	public void setLikedByCurrentUser(boolean likedByCurrentUser) {
		this.likedByCurrentUser = likedByCurrentUser;
	}

}
