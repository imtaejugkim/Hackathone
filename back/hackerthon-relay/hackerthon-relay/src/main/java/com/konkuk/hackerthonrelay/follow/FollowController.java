package com.konkuk.hackerthonrelay.follow;

import com.konkuk.hackerthonrelay.notification.Notification;
import com.konkuk.hackerthonrelay.notification.Notification.NotificationType;
import com.konkuk.hackerthonrelay.notification.NotificationRepository;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/follow")
public class FollowController {

    private final FollowRelationRepository followRelationRepository;
    private final UserRepository userRepository;
    private final FollowService followService;
	private final NotificationRepository notificationRepository;

	public FollowController(FollowRelationRepository followRelationRepository, UserRepository userRepository,
			FollowService followService, NotificationRepository notificationRepository) {
		this.followRelationRepository = followRelationRepository;
		this.userRepository = userRepository;
		this.followService = followService;
		this.notificationRepository = notificationRepository;
	}

	@PostMapping("/toggle/{requesterId}/{targetId}")
	public ResponseEntity<Map<String, Object>> toggleFollow(@PathVariable Long requesterId,
			@PathVariable Long targetId) {
		ResponseEntity<Map<String, Object>> responseEntity = followService.toggleFollow(requesterId, targetId);
		Map<String, Object> response = responseEntity.getBody();

		boolean isFollowing = (boolean) response.get("isFollowing");
		if (isFollowing) {
			// 팔로우 관계가 생성되었을 때 알림 생성
			User requester = userRepository.findById(requesterId)
					.orElseThrow(() -> new RuntimeException("User not found"));
			User target = userRepository.findById(targetId).orElseThrow(() -> new RuntimeException("User not found"));

			Notification notification = new Notification();
			notification.setRecipient(target);
			notification.setMessage(requester.getUsername() + "님이 당신을 팔로우했습니다.");
			notification.setType(NotificationType.FOLLOW);
			notification.setUserId(requester.getId()); // 팔로우 요청한 사용자의 ID
			notification.setUserIdString(requester.getUserId()); // 팔로우 요청한 사용자의 String userId 설정
			notification.setUserName(requester.getUsername()); // 팔로우 요청한 사용자의 이름
			notification.setUserProfileUrl(requester.getProfilePictureUrl());

			notificationRepository.save(notification);
		}
		return responseEntity;
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<FollowerInfo>> getFollowers(@PathVariable Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        log.info("user = {}", user);
        List<FollowRelation> followRelations = followRelationRepository.findFollowersByFollowing(user);
        log.info("followRelations = {}", followRelations);

        List<FollowerInfo> followersInfo = new ArrayList<>();
        for (FollowRelation relation : followRelations) {
            followersInfo.add(new FollowerInfo(relation.getFollower().getId(), relation.getFollower().getUsername()));
        }

        return ResponseEntity.ok(followersInfo);
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<FollowerInfo>> getFollowing(@PathVariable Long userId) {

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<FollowRelation> followRelations = followRelationRepository.findByFollower(user);
        List<FollowerInfo> followingInfo = new ArrayList<>();
        for (FollowRelation relation : followRelations) {
            followingInfo.add(new FollowerInfo(relation.getFollowing().getId(), relation.getFollowing().getUsername()));
        }

        return ResponseEntity.ok(followingInfo);
    }


    @GetMapping("/is-following/{requesterId}/{targetId}")
    public ResponseEntity<Boolean> isFollowing(@PathVariable Long requesterId, @PathVariable Long targetId) {
        return followService.isFollowing(requesterId, targetId);
    }
}
