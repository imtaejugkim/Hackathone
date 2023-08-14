package com.konkuk.hackerthonrelay.comment;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.konkuk.hackerthonrelay.pictureupload.Post;
import com.konkuk.hackerthonrelay.user.User;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Comment {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JsonBackReference
	private Post post;

	@ManyToOne
	@JsonBackReference
	@JoinColumn(name = "author_id") // 데이터베이스 테이블에서의 외래 키 컬럼 이름
	private User author;

	@Column(nullable = false, updatable = false)
	private LocalDateTime createdAt; // 생성 시간 필드 추가

	private String content;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public User getAuthor() {
		return author;
	}

	public void setAuthor(User author) {
		this.author = author;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	@PrePersist
	protected void onCreate() {
		this.createdAt = LocalDateTime.now();
	}

}
