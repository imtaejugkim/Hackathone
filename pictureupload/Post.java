package com.konkuk.hackerthonrelay.pictureupload;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private Image mainImage;

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	private boolean isCompleted = false;

	private String mentionedUser; // This can be a user ID, username, etc.

	private String theme; // 게시물 테마

	private LocalDateTime createdAt; // 게시물 생성 시간

	private String videoPath; // 동영상 파일 경로

	private String musicPath; // 음악 파일 경로

	private Integer frameSize; // 사진 프레임 크기

	private Integer framePosition; // 사진이 들어갈 프레임 위치

	private String text; // 게시물의 텍스트

	private LocalDateTime expirationTime; // 시간제한, 예를 들어 24시간

	@Convert(converter = DurationToLongConverter.class)
	private Duration remainingTime; // 남은 시간

	private Integer likes; // 좋아요 수

	private Integer views; // 동영상 조회수

	@OneToMany
	private List<Tag> tags = new ArrayList<>(); // 태그 정보

	public void addImage(Image image) {
		this.images.add(image);
		image.setPost(this);
	}

	public String getVideoPath() {
		return videoPath;
	}

	public void setVideoPath(String videoPath) {
		this.videoPath = videoPath;
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

	public Integer getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(Integer frameSize) {
		this.frameSize = frameSize;
	}

	public Integer getFramePosition() {
		return framePosition;
	}

	public void setFramePosition(Integer framePosition) {
		this.framePosition = framePosition;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}

	public void setExpirationTime(LocalDateTime expirationTime) {
		this.expirationTime = expirationTime;
	}

	public Duration getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(Duration remainingTime) {
		this.remainingTime = Duration.ofSeconds(remainingTime.getSeconds());
	}

	public void decreaseRemainingTime() {
		// Decrease the remaining time by 1 hour
		if (remainingTime != null) {
			this.remainingTime = Duration.ofSeconds(remainingTime.getSeconds() - 3600);
		}
	}

	public Integer getLikes() {
		return likes;
	}

	public void setLikes(Integer likes) {
		this.likes = likes;
	}

	public Integer getViews() {
		return views;
	}

	public void setViews(Integer views) {
		this.views = views;
	}

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}

	public Image getMainImage() {
		return mainImage;
	}

	public void setMainImage(Image mainImage) {
		this.mainImage = mainImage;
	}

	public boolean isCompleted() {
		return isCompleted;
	}

	public void setCompleted(boolean isCompleted) {
		this.isCompleted = isCompleted;
	}

	public String getMentionedUser() {
		return mentionedUser;
	}

	public void setMentionedUser(String mentionedUser) {
		this.mentionedUser = mentionedUser;
	}

	public String getTheme() {
		return theme;
	}

	public void setTheme(String theme) {
		this.theme = theme;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void like() {
		this.likes += 1;
	}

	public void view() {
		this.views += 1;
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.remainingTime = Duration.ofHours(24); // Here you set the initial remainingTime
	}

	public void mentionUser(String user) {
		this.mentionedUser = user;
		this.remainingTime = Duration.ofSeconds(24 * 3600); // Here you reset the remainingTime when a user is
																	// mentioned
	}

	public void passEditRight() {
		if (this.remainingTime.getSeconds() <= 0) {
			// Here you pass the edit right back to the last mentioned user.
			// This logic depends on how you manage the users in your application.
			this.remainingTime = Duration.ofSeconds(24 * 3600); // Reset the remainingTime
		}
	}
}
