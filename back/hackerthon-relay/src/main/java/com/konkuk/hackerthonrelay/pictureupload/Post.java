package com.konkuk.hackerthonrelay.pictureupload;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.konkuk.hackerthonrelay.comment.Comment;
import com.konkuk.hackerthonrelay.user.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Post {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@OneToOne
	private Image mainImage;

	@JsonManagedReference
	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Comment> comments = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	private User creator; // 게시물의 생성자

	@OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PostUser> participants = new ArrayList<>();

	@ManyToOne
	@JoinColumn(name = "mentioned_user_id")
	private User mentionedUser;

	@ManyToOne
	@JoinColumn(name = "last_mentioned_user_id")
	private User lastMentionedUser; // 마지막에 언급된 사용자를 저장하는 필드

	@ManyToMany
	@JoinTable(name = "LIKED_POSTS", // 이 이름은 실제 데이터베이스 테이블 이름과 일치해야 합니다.
			joinColumns = @JoinColumn(name = "LIKED_POSTS_ID"), inverseJoinColumns = @JoinColumn(name = "LIKED_USERS_ID"))
	private Set<User> likedUsers = new HashSet<>(); // 해당 게시물에 좋아요를 누른 사용자들의 목록을 나타냅니다.

	private int commentCount = 0;

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Post post = (Post) o;
		return id.equals(post.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}


	private boolean isCompleted = false;

	private Integer theme; // 게시물 테마

	private LocalDateTime createdAt; // 게시물 생성 시간

	private String videoPath; // 동영상 파일 경로

	private Integer musicNum; // 음악 파일 경로

	private Integer frameSize; // 사진 프레임 크기

	private String text; // 게시물의 텍스트

	private LocalDateTime expirationTime; // 시간제한, 예를 들어 24시간

	@Convert(converter = DurationToLongConverter.class)
	private Duration remainingTime; // 남은 시간

	private Integer likes = 0; // 좋아요 수

	private Integer views; // 동영상 조회수

	@OneToMany
	private List<Tag> tags = new ArrayList<>(); // 태그 정보

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

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

	public Integer getMusicNum() {
		return musicNum;
	}

	public void setMusicNum(Integer musicNum) {
		this.musicNum = musicNum;
	}

	public Integer getFrameSize() {
		return frameSize;
	}

	public void setFrameSize(Integer frameSize) {
		this.frameSize = frameSize;
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
		if (this.remainingTime != null) {
			this.remainingTime = this.remainingTime.minusSeconds(60); // 감소하는 시간 조정 가능
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

	public Integer getTheme() {
		return theme;
	}

	public void setTheme(Integer theme) {
		this.theme = theme;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void view() {
		this.views += 1;
	}

	public int getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(int commentCount) {
		this.commentCount = commentCount;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void addParticipant(User user) {
		PostUser postUser = new PostUser();
		postUser.setPost(this);
		postUser.setUser(user);
		this.participants.add(postUser);
	}

	public List<PostUser> getParticipants() {
		return participants;
	}

	public void setParticipants(List<PostUser> participants) {
		this.participants = participants;
	}

	public void setMentionedUser(User mentionedUser) {
		this.mentionedUser = mentionedUser;
	}

	public Set<User> getLikedUsers() {
		return likedUsers;
	}

	public void setLikedUsers(Set<User> likedUsers) {
		this.likedUsers = likedUsers;
	}

	public User getMentionedUser() {
		return mentionedUser;
	}

	public User getLastMentionedUser() {
		return lastMentionedUser;
	}

	public void setLastMentionedUser(User lastMentionedUser) {
		this.lastMentionedUser = lastMentionedUser;
	}

	public boolean isExpired() {
		return this.remainingTime != null
				&& (this.remainingTime.isNegative() || this.remainingTime.equals(Duration.ZERO));
	}

	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}

	public boolean toggleLike(User user) {
		if (this.likedUsers.contains(user)) {
			this.likedUsers.remove(user);
			this.likes--;
			return false; // 좋아요 취소됨을 나타내는 false 반환
		} else {
			this.likedUsers.add(user);
			this.likes++;
			return true; // 좋아요 눌림을 나타내는 true 반환
		}
	}

	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.remainingTime = Duration.ofHours(24); // Here you set the initial remainingTime
	}

	public void mentionUser(User user) {
		this.mentionedUser = user;
		this.lastMentionedUser = user; // 마지막으로 언급된 사용자 업데이트
		this.remainingTime = Duration.ofSeconds(24 * 3600);
	}

	public void passEditRight() {
		if (this.remainingTime != null
				&& (this.remainingTime.isNegative() || this.remainingTime.equals(Duration.ZERO))) {
			if (this.lastMentionedUser != null) {
				this.user = this.lastMentionedUser;
				this.mentionedUser = null; // 언급된 사용자 초기화
				this.remainingTime = Duration.ofSeconds(24 * 3600);
			}
		}
	}
}

