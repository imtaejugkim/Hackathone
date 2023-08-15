package com.konkuk.hackerthonrelay.pictureupload;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.konkuk.hackerthonrelay.comment.CommentService;
import com.konkuk.hackerthonrelay.notification.Notification;
import com.konkuk.hackerthonrelay.notification.NotificationDto;
import com.konkuk.hackerthonrelay.notification.NotificationRepository;
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
import com.konkuk.hackerthonrelay.follow.FollowRelation;
import com.konkuk.hackerthonrelay.follow.FollowRelationRepository;
import com.konkuk.hackerthonrelay.search.UserService;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;

@RestController
@RequestMapping("/api/posts")
public class PostController {

	private final PostRepository postRepository;
	private final PostService postService;
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private FollowRelationRepository followRelationRepository;

	@Autowired
	private CommentService commentService; // CommentService 주입

	private final NotificationRepository notificationRepository;


	@Autowired
	public PostController(PostService postService, PostRepository postRepository, UserRepository userRepository,
						  FollowRelationRepository followRelationRepository, CommentService commentService,
						  NotificationRepository notificationRepository
	) {
		this.postService = postService;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.followRelationRepository = followRelationRepository;
		this.commentService = commentService;
		this.notificationRepository = notificationRepository;
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

		// 참여자 프로필 URL 목록을 DTO에 추가
		List<String> participantsUserProfileUrls = post.getParticipants().stream()
				.map(postUser -> postUser.getUser().getProfilePictureUrl()).collect(Collectors.toList());
		postDto.setParticipantsUserProfileUrl(participantsUserProfileUrls);


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

		// 알림 생성
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		User liker = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		Notification notification = new Notification();
		notification.setRecipient(post.getCreator());
		notification.setMessage(liker.getUsername() + "님이 당신의 게시물에 좋아요를 눌렀습니다.");
		notification.setType(Notification.NotificationType.LIKE);
		notification.setPostId(postId);
		notification.setUserId(liker.getId()); // 좋아요를 누른 사용자의 ID
		notification.setUserName(liker.getUsername()); // 좋아요를 누른 사용자의 이름

		notificationRepository.save(notification);

		return ResponseEntity.ok(response);
	}

	@GetMapping("/notifications/{userId}")
	public ResponseEntity<List<NotificationDto>> getNotifications(@PathVariable Long userId) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new RuntimeException("User with ID " + userId + " not found"));

		List<Notification> notifications = notificationRepository.findByRecipient(user);

		// Convert to DTOs
		List<NotificationDto> notificationDtos = notifications.stream().map(notification -> {
			NotificationDto dto = new NotificationDto();
			dto.setId(notification.getId());
			dto.setMessage(notification.getMessage());
			dto.setCreatedAt(notification.getCreatedAt());
			return dto;
		}).collect(Collectors.toList());

		return ResponseEntity.ok(notificationDtos);
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
