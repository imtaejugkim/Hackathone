package com.konkuk.hackerthonrelay.pictureupload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.konkuk.hackerthonrelay.comment.Comment;
import com.konkuk.hackerthonrelay.comment.CommentDto;
import com.konkuk.hackerthonrelay.comment.CommentService;
import com.konkuk.hackerthonrelay.follow.FollowRelation;
import com.konkuk.hackerthonrelay.follow.FollowRelationRepository;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostRepository postRepository;
	private final PostService postService;
	private final UserRepository userRepository;
	private final FollowRelationRepository followRelationRepository;
	private final CommentService commentService;

	@Autowired
	public PostController(PostService postService, PostRepository postRepository, UserRepository userRepository,
			FollowRelationRepository followRelationRepository, CommentService commentService) {
		this.postService = postService;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.followRelationRepository = followRelationRepository;
		this.commentService = commentService;
	}


	@GetMapping("/{postId}")
	public ResponseEntity<PostDto> getPost(@PathVariable Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post with ID " + postId + " not found"));

		// Post 엔터티를 PostDto로 변환
		PostDto postDto = postService.toDto(post);
		// 참여자 목록을 DTO에 추가
		postDto.setParticipantUserIds(post.getParticipants().stream().map(postUser -> postUser.getUser().getId())
				.collect(Collectors.toList()));

		// Main Image 설정
		postDto.setMainImage(post.getMainImage().getPath());

		return ResponseEntity.ok(postDto);
	}

	@GetMapping("/{postId}/participants")
	public ResponseEntity<List<Long>> getParticipantsForPost(@PathVariable Long postId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new RuntimeException("Post with ID " + postId + " not found"));

		List<Long> userIds = post.getParticipants().stream().map(postUser -> postUser.getUser().getId())
				.collect(Collectors.toList());

		return ResponseEntity.ok(userIds);
	}


	@PostMapping("/{postId}/like")
	public ResponseEntity<Map<String, Object>> likePost(@PathVariable Long postId, @RequestParam Long userId) {
	    Map<String, Object> response = new HashMap<>();

		boolean isLiked = postService.likePost(postId, userId);

		if (isLiked) {
			response.put("message", "Like added successfully");
		} else {
			response.put("message", "Like removed successfully");
		}
		response.put("status", true);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/loadMain")
	public ResponseEntity<List<PostDto>> getMainPosts(@RequestParam Long userId, @RequestParam Integer theme,
			@RequestParam(required = false) String lastPostDateStr, @RequestParam(defaultValue = "20") int limit) {

		LocalDateTime lastPostDate = null;
		if (lastPostDateStr != null && !lastPostDateStr.isEmpty()) {
			lastPostDate = convertStringToLocalDateTime(lastPostDateStr);
		}

		// Fetch users that the current user is following
		List<FollowRelation> followRelations = followRelationRepository
				.findByFollower(userRepository.findById(userId).orElse(null));
		List<Long> followingUserIds = followRelations.stream().map(relation -> relation.getFollowing().getId())
				.collect(Collectors.toList());
		// Also add the current user's ID to the list
		followingUserIds.add(userId);

		List<Post> posts;

		if (lastPostDate == null) {
			posts = postRepository.findByThemeAndUserIdInOrderByCreatedAtDesc(theme, followingUserIds,
					PageRequest.of(0, limit));
		} else {
			posts = postRepository.findByThemeAndUserIdInAndCreatedAtBeforeOrderByCreatedAtDesc(theme, followingUserIds,
					lastPostDate, PageRequest.of(0, limit));
		}

		User currentUser = userRepository.findById(userId).orElse(null); // 현재 사용자 정보 가져오기

		List<PostDto> postDtos = posts.stream().map(post -> {
			PostDto dto = postService.toDto(post);

			// 사용자가 해당 게시물에 좋아요를 눌렀는지 확인
			if (currentUser != null && post.getLikedUsers().contains(currentUser)) {
				dto.setLikedByCurrentUser(true);
			} else {
				dto.setLikedByCurrentUser(false);
			}

			// Fetch comments for the post
			List<Comment> commentsForPost = commentService.getCommentsByPostId(post.getId());
			List<CommentDto> commentDtosForPost = commentsForPost.stream().map(commentService::convertToDto)
					.collect(Collectors.toList());
			dto.setComments(commentDtosForPost);

			// 참여자 ID 리스트에서 중복 제거
			dto.setParticipantUserIds(dto.getParticipantUserIds().stream().distinct().collect(Collectors.toList()));

			return dto;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(postDtos);
	}

	private LocalDateTime convertStringToLocalDateTime(String str) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		return LocalDateTime.parse(str, formatter);
	}


	@GetMapping("/user/{userId}")
	public ResponseEntity<List<PostDto>> getUserPosts(@PathVariable Long userId, @RequestParam Long currentUser) {
		User profileUser = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

		List<Post> posts = postRepository.findByUser(profileUser);

		User loggedInUser = userRepository.findById(currentUser).orElse(null); // 로그인한 사용자 정보 가져오기

		List<PostDto> postDtos = posts.stream().map(post -> {
			PostDto dto = postService.toDto(post);

			// 로그인한 사용자가 해당 게시물에 좋아요를 눌렀는지 확인
			if (loggedInUser != null && post.getLikedUsers().contains(loggedInUser)) {
				dto.setLikedByCurrentUser(true);
			} else {
				dto.setLikedByCurrentUser(false);
			}

			// 참여자 ID 리스트에서 중복 제거
			dto.setParticipantUserIds(dto.getParticipantUserIds().stream().distinct().collect(Collectors.toList()));

			return dto;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(postDtos);
	}

}
