package com.konkuk.hackerthonrelay.pictureupload;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import com.fasterxml.jackson.annotation.*;
import com.konkuk.hackerthonrelay.comment.Comment;
import com.konkuk.hackerthonrelay.pictureupload.tag.Tag;
import com.konkuk.hackerthonrelay.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
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
	@JsonIdentityReference(alwaysAsId = true)
	private User user;

	@ManyToOne
	@JoinColumn(name = "creator_id")
	//@JsonIdentityReference(alwaysAsId = true)
	@JsonManagedReference
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

	private Integer views = 0; // 동영상 조회수

	@ManyToMany
	@JoinTable(name = "RELAY_PARTICIPANTS",
			joinColumns = @JoinColumn(name = "POST_ID"),
			inverseJoinColumns = @JoinColumn(name = "USER_ID"))
	private Set<User> relayParticipants = new HashSet<>();

	private Integer imageCount = 0; // 이미지 개수

	private boolean canAddMusic = false; // 음악 추가 가능 여부
	private boolean canAddText = false; // 텍스트 추가 가능 여부

//	@ManyToOne
//	@JoinColumn(name = "shared_from_id")
//	private Post sharedFrom;

	private Integer shareCount = 0; // Initialize shareCount

	@JsonIgnore
	@ManyToMany(cascade = CascadeType.PERSIST) // 또는 CascadeType.ALL로 변경
	@JoinTable(
			name = "post_tags",
			joinColumns = @JoinColumn(name = "post_id"),
			inverseJoinColumns = @JoinColumn(name = "tag_id")
	)
	private Set<Tag> tags = new HashSet<>();// 태그정보

	public void addImage(Image image) {
		this.images.add(image);
		image.setPost(this);
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


	public void view() {
		this.views += 1;
	}


	public void setMentionedUser(User mentionedUser) {
		this.mentionedUser = mentionedUser;
	}

	public User getMentionedUser() {
		return mentionedUser;
	}


	@PrePersist
	public void prePersist() {
		this.createdAt = LocalDateTime.now();
		this.remainingTime = Duration.ofHours(24); // Here you set the initial remainingTime
	}

	public void mentionUser(User user) {
		this.mentionedUser = user;
		this.lastMentionedUser = user; // 마지막으로 언급된 사용자 업데이트
		this.remainingTime = Duration.ofSeconds(24 * 3600); // Here you reset the remainingTime when a user is
																	// mentioned
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

	public void incrementShareCount() {
		this.shareCount++;
	}

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

	public boolean isExpired() {
		return this.remainingTime != null
				&& (this.remainingTime.isNegative() || this.remainingTime.equals(Duration.ZERO));
	}

	public void addParticipant(User user) {
		PostUser postUser = new PostUser();
		postUser.setPost(this);
		postUser.setUser(user);
		this.participants.add(postUser);
	}

	public void addTag(Tag tag) {
		tags.add(tag);
		tag.getPosts().add(this);
	}
	public void removeTag(Tag tag) {
		tags.remove(tag);
		tag.getPosts().remove(this);
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


}
