package com.konkuk.hackerthonrelay.pictureupload;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.konkuk.hackerthonrelay.notification.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.konkuk.hackerthonrelay.comment.Comment;
import com.konkuk.hackerthonrelay.comment.CommentDto;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class PostService {

	private final PostRepository postRepository;
	private final UserRepository userRepository;


	public Post findById(Long postId) {
		return postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
	}

	@Autowired
	public PostService(PostRepository postRepository, UserRepository userRepository,
					   NotificationRepository notificationRepository) {
		this.postRepository = postRepository;
		this.userRepository = userRepository;
	}

	public boolean likePost(Long postId, Long userId) {
		Post post = postRepository.findById(postId)
				.orElseThrow(() -> new EntityNotFoundException("Post with ID " + postId + " not found"));
		User currentUser = userRepository.findById(userId).orElse(null);

		if (currentUser == null) {
			throw new RuntimeException("User with ID " + userId + " not found");
		}

		boolean isLiked = post.toggleLike(currentUser);

		if (isLiked) {
			post.getCreator().updateScoreForReceivingLike(true);
		} else {
			post.getCreator().updateScoreForReceivingLike(false);
		}
		userRepository.save(post.getCreator());
		postRepository.save(post);

		return isLiked;
	}


//	@Scheduled(fixedDelay = 1000) // This method will be executed every 60 seconds
//	public void checkPostRemainingTime() {
//		List<Post> posts = postRepository.findByIsCompletedFalse(); // 아직 완료되지 않은 게시물만 조회
//		for (Post post : posts) {
//			post.decreaseRemainingTime();
//			post.passEditRight();
//			postRepository.save(post);
//		}
//	}

	public PostDto toDto(Post post) { // 한번에 반환이 필요한 경우에 사용
		PostDto dto = new PostDto();

		dto.setId(post.getId());
		dto.setLikeCount(post.getLikes()); // 좋아요 수 설정
		dto.setTheme(post.getTheme()); // 테마 설정
		dto.setDate(post.getCreatedAt());
		dto.setText(post.getText());

		// Image 객체 리스트를 URI 리스트로 변환
		List<String> imgList = post.getImages().stream().map(Image::getPath).collect(Collectors.toList());

		// 이미지가 4개 미만인 경우 나머지를 null로 채우기
		while (imgList.size() < 4) {
			imgList.add(null); // null 대신 기본 이미지 URI를 설정할 수 있습니다.
		}

		dto.setImages(new ArrayList<>(imgList));

		// Fetching user IDs who uploaded at least one image to the post
		List<Long> participantUserIds = post.getImages().stream().map(image -> image.getUser().getId()).distinct()
				.collect(Collectors.toList());
		dto.setParticipantUserIds(participantUserIds);

		if (post.getMainImage() != null) {
			dto.setMainImage(post.getMainImage().getPath());
		}

		List<String> participantUserIdStrings = post.getImages().stream()
				.map(image -> image.getUser().getUserId()).distinct().collect(Collectors.toList());

		dto.setParticipantUserIdStrings(participantUserIdStrings);

		List<String> participantsUserProfileUrls = post.getImages().stream()
				.map(image -> image.getUser().getProfilePictureUrl()).distinct().collect(Collectors.toList());

		dto.setParticipantsUserProfileUrl(participantsUserProfileUrls);

		return dto;
	}



	public CommentDto toDto(Comment comment) {
		CommentDto dto = new CommentDto();
		dto.setId(comment.getId());
		dto.setContent(comment.getContent());
		dto.setAuthorId(comment.getAuthor().getId()); // 작성자 ID 설정
		dto.setAuthorName(comment.getAuthor().getUsername()); // 작성자 이름 설정
		// dto.setCreatedAt(comment.getCreatedAt()); // Uncomment this if you add a
		// creation date to Comment

		return dto;
	}

}
