package com.konkuk.hackerthonrelay.comment;

import java.time.LocalDateTime;

import com.konkuk.hackerthonrelay.user.User;
import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class CommentDto {
	private Long id;
	private Long postId;
	private String content;
	private LocalDateTime createdAt;
	private Long authorId;
	private String authorName;
	private String authorUserIdString;
	private String profileUrl;
}
