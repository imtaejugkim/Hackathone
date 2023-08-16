package com.konkuk.hackerthonrelay.pictureupload;

import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.konkuk.hackerthonrelay.notification.Notification;
import com.konkuk.hackerthonrelay.notification.Notification.NotificationType;
import com.konkuk.hackerthonrelay.notification.NotificationRepository;
import com.konkuk.hackerthonrelay.pictureupload.tag.Tag;
import com.konkuk.hackerthonrelay.pictureupload.tag.TagRepository;
import com.konkuk.hackerthonrelay.pictureupload.tag.TagService;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
	private final ImageUploadService uploadService;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ImageRepository imageRepository;
	private final NotificationRepository notificationRepository;
	private final TagService tagService;
	private final TagRepository tagRepository;

	@Autowired
	public ImageUploadController(ImageUploadService uploadService, PostRepository postRepository,
			ImageRepository imageRepository, UserRepository userRepository, TagRepository tagRepository,
			TagService tagService, NotificationRepository notificationRepository) {
		this.uploadService = uploadService;
		this.postRepository = postRepository;
		this.userRepository = userRepository;
		this.imageRepository = imageRepository;
		this.tagRepository = tagRepository;
		this.tagService = tagService;
		this.notificationRepository = notificationRepository;
	}

	// db 업데이트
	@Scheduled(fixedRate = 1000) // 매 초마다 실행
	public void decreaseRemainingTime() {
		List<Post> posts = postRepository.findAll();
		for (Post post : posts) {
			post.decreaseRemainingTime();
		}
		postRepository.saveAll(posts); // 변경 사항 저장
	}


	// 새 게시물 생성
	@PostMapping("/create")
	public ResponseEntity<Map<String, Object>> createPost(
			@RequestPart(value = "image", required = false) MultipartFile[] imageFiles,
			@RequestParam(value = "theme", required = false) Integer theme, @RequestParam("userId") String userId,
			@RequestParam(required = false) String text,
			@RequestParam(value = "musicNum", required = false) Integer musicNum,
			@RequestParam(required = false) String tags) {

		Map<String, Object> response = new HashMap<>();

		try {
			User user = userRepository.findByUserId(userId);
			if (user == null) {
				log.error("User not found with userId: {}", userId);
				response.put("success", false);
				response.put("message", "User not found.");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			Post post = new Post();
			post.setTheme(theme); // 게시물 테마 설정
			post.setUser(user); // 게시물과 사용자 연결
			post.setCreator(user); // 게시물 작성자 설정
			post.addParticipant(user); // 참여자 추가
			post.setMusicNum(musicNum); // 여기에 musicNum 설정 로직 추가
			postRepository.save(post); // 먼저 Post를 저장하여 ID를 생성합니다.

			int position = 0;
			for (MultipartFile imageFile : imageFiles) {
				Image image = uploadService.uploadImage(imageFile);
				image.setPost(post); // Image에 Post 참조를 설정합니다.
				image.setUser(user); // 이미지와 사용자 연결
				image.setPosition(position++); // 이미지 위치 설정
				post.getImages().add(image);

			}

			if (text != null) {
				post.setText(text); // 텍스트 설정
			}

			post.setMainImage(post.getImages().get(0)); // 첫 번째 이미지를 메인 이미지로 설정

			if (tags != null && !tags.isEmpty()) {
				Set<Tag> tagSet = parseTags(tags);
				for (Tag tag : tagSet) {
					Tag existingTag = tagRepository.findByName(tag.getName());
					log.info("existingTag = {}", existingTag);
					if (existingTag != null) {
						log.info("existingIf = {}", existingTag);
						post.addTag(existingTag);
					} else {
						log.info("existingElse = {}", existingTag);
						log.info("tag = {}", tag);
						// 새 태그를 만들어서 저장
						Tag createdTag = tagService.createHashTag(tag.getName());
						log.info("createdTag = {}", createdTag);
						tagRepository.save(createdTag);
						post.addTag(createdTag); // tag 넣으면 null이 추가로 db에 넣어짐 왜??
					}
				}
			}

			post.setMainImage(post.getImages().get(0)); // 첫 번째 이미지를 메인 이미지로 설정

			if (post.getImages().size() == 4) {
				post.setCompleted(true); // 게시물 완성 상태로 설정
			}

			postRepository.save(post); // Post를 다시 저장하여 변경 사항을 반영합니다.
			response.put("success", true);
			response.put("postId", post.getId());
			return new ResponseEntity<>(response, HttpStatus.CREATED); // 수정된 부분

		} catch (IOException e) {
			response.put("success", false);
			response.put("message", "Could not upload file. Please try again."); // 추가된 부분
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR); // 수정된 부분 }
		}
	}

	private Set<Tag> parseTags(String tags) {
		Set<Tag> tagSet = new HashSet<>();
		String[] tagNames = tags.split(",");
		for (String tagName : tagNames) {
			Tag tag = new Tag(tagName.trim());
			tagSet.add(tag);
		}
		return tagSet;
	}

	// 이미지 추가
	@PostMapping("/add")
	public ResponseEntity<String> addImage(@RequestParam("postId") Long postId,
			@RequestParam("image") MultipartFile[] imageFiles, @RequestParam("userId") String userId) {
		try {
			Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

			User user = userRepository.findByUserId(userId);

			// 참여자 목록에서 특정 사용자가 이미 포함되어 있는지 확인
			boolean isUserAlreadyParticipant = post.getParticipants().stream()
					.anyMatch(postUser -> postUser.getUser().equals(user));

			if (user != null && !isUserAlreadyParticipant) {
				post.addParticipant(user); // 참여자 추가
			}

			int position = post.getImages().size(); // Start from the current number of images in the post
			for (MultipartFile imageFile : imageFiles) {
				Image image = uploadService.uploadImage(imageFile);
				image.setPost(post);
				image.setUser(user); // 이미지에 사용자 연결
				image.setPosition(position++);
				post.getImages().add(image);
			}

			if (post.getImages().size() == 4) {
				post.setCompleted(true); // 게시물 완성 상태로 설정
			}

			postRepository.save(post);
			return ResponseEntity.ok("Images added successfully");
		} catch (IOException e) {
			throw new RuntimeException("Could not upload file. Please try again.", e);
		}
	}


	@GetMapping("/{imageId}")
	public ResponseEntity<Image> getImageById(@PathVariable Long imageId) {
		Image image = imageRepository.findById(imageId)
				.orElseThrow(() -> new RuntimeException("Image not found with id: " + imageId));
		return ResponseEntity.ok(image);
	}

	// 릴레이 이어가기 (사용자 언급 또는 공개 설정)
	// 릴레이 이어가기 (사용자 언급만 허용)
	@PostMapping("/relay")
	public ResponseEntity<Map<String, Object>> relayPost(@RequestParam("postId") Long postId,
			@RequestParam("user") String userId,
			@RequestParam(value = "remainingSeconds", required = false, defaultValue = "86400") int remainingSeconds) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));

		User creator = post.getCreator();
		if (creator == null) {
			throw new RuntimeException("Creator user not found.");
		}
		User mentioned = userRepository.findByUserId(userId);
		if (mentioned == null) {
			throw new RuntimeException("Mentioned user not found.");
		}

		if (creator.equals(mentioned)) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "You cannot mention yourself.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		if (post.getUser().equals(mentioned)) {
			Map<String, Object> response = new HashMap<>();
			response.put("success", false);
			response.put("message", "You cannot relay to yourself.");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		User currentUser = post.getUser(); // 릴레이를 주는 사용자
		currentUser.updateScoreForGivingRelay(); // 릴레이를 준 사용자의 점수 업데이트
		userRepository.save(currentUser); // 변경 사항을 저장합니다.

		mentioned.updateScoreForReceivingRelay(); // 릴레이를 받은 사용자의 점수 업데이트
		userRepository.save(mentioned); // 변경 사항을 저장합니다.

		// 현재 릴레이의 사용자 (이전에 언급된 사용자)를 lastMentionedUser로 설정
		post.setLastMentionedUser(post.getMentionedUser());
		// 새로 언급된 사용자를 mentionedUser로 설정
		post.setMentionedUser(mentioned);
		post.setRemainingTime(Duration.ofSeconds(remainingSeconds));

		// 알림 생성
		Notification notification = new Notification();
		notification.setRecipient(mentioned);

		User lastUser = post.getLastMentionedUser();

		if (lastUser == null) {
			notification.setMessage("님이 릴레이를 신청하셨습니다.");
		} else {
			notification.setMessage("님이 릴레이를 신청하셨습니다.");
		}


		notification.setType(NotificationType.RELAY);
		notification.setPostId(postId);
		notification.setUserId(currentUser.getId()); // 현재 사용자의 ID
		notification.setUserIdString(currentUser.getUserId()); // 현재 사용자의 String userId 설정
		notification.setUserName(currentUser.getUsername()); // 현재 사용자의 이름
		notification.setRemainingTime(Duration.ofSeconds(remainingSeconds)); // 남은 시간 설정
		notification.setUserProfileUrl(currentUser.getProfilePictureUrl()); // 'getProfileUrl()'는 사용자의 프로필 URL을 반환하는
																			// 메서드여야 합니다.

		notificationRepository.save(notification);
		postRepository.save(post);
		Map<String, Object> response = new HashMap<>();
		response.put("success", true);
		response.put("message", "Relay set for post: " + postId);
		return ResponseEntity.ok(response);
	}

	// 게시물 삭제
	@PostMapping("/deletePost")
	public ResponseEntity<String> deletePost(@RequestParam("postId") Long postId, @RequestParam("user") String userId) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		if (!post.getUser().getUserId().equals(userId)) {
			return new ResponseEntity<>("You do not have permission to delete this post.", HttpStatus.FORBIDDEN);
		}

		postRepository.delete(post);
		return new ResponseEntity<>("Post deleted successfully.", HttpStatus.OK);
	}
}