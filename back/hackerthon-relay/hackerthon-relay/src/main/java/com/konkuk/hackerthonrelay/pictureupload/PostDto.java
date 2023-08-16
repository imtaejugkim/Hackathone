package com.konkuk.hackerthonrelay.pictureupload;

import com.konkuk.hackerthonrelay.comment.CommentDto;
import lombok.Getter;
import lombok.Setter;

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

}
