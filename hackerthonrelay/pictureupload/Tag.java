package com.konkuk.hackerthonrelay.pictureupload;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;

@Entity
public class Tag {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name; // 태그 이름

	@ManyToMany(mappedBy = "tags")
	private List<Post> posts = new ArrayList<>(); // 이 태그가 달린 게시물

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Post> getPosts() {
		return posts;
	}

	public void setPosts(List<Post> posts) {
		this.posts = posts;
	}

}
