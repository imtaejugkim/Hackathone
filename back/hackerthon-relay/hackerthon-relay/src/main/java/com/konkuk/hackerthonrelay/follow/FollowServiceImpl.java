package com.konkuk.hackerthonrelay.follow;

import com.konkuk.hackerthonrelay.notification.Notification;
import com.konkuk.hackerthonrelay.notification.NotificationRepository;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

@Slf4j
@Service
public class FollowServiceImpl implements FollowService {

    @Autowired
    private NotificationRepository notificationRepository;
    private final FollowRelationRepository followRelationRepository;
    private final UserRepository userRepository;

    public FollowServiceImpl(FollowRelationRepository followRelationRepository, UserRepository userRepository) {
        this.followRelationRepository = followRelationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<Map<String, Object>> followUser(Long followerId, Long followingId) {
        log.info("Attempting to create a follow relation between followerId: {} and followingId: {}", followerId,
                followingId);


        Map<String, Object> response = new HashMap<>();

        if (followerId.equals(followingId)) {
            response.put("success", false);
            response.put("message", "Users cannot follow themselves");
            return ResponseEntity.badRequest().body(response);
        }

        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);

        if (follower == null || following == null) {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }

        if (follower.getFollowing().contains(following)) {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }


        FollowRelation relation = new FollowRelation();
        relation.setFollower(follower);
        relation.setFollowing(following);
        followRelationRepository.save(relation);

        response.put("success", true);

        // 알림 생성
        Notification notification = new Notification();
        notification.setRecipient(following);
        notification.setMessage(follower.getUsername() + "님이 팔로우 했습니다.");
        notification.setUserId(followerId);
        notification.setUserName(follower.getUsername());
        notification.setType(Notification.NotificationType.FOLLOW);
        notificationRepository.save(notification);

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> unfollowUser(Long followerId, Long followingId) {
        log.info("Attempting to remove a follow relation between followerId: {} and followingId: {}", followerId,
                followingId);
        User follower = userRepository.findById(followerId).orElse(null);
        User following = userRepository.findById(followingId).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if (follower == null || following == null) {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }

        FollowRelation relation = followRelationRepository.findByFollowerAndFollowing(follower, following);
        if (relation == null) {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }

        followRelationRepository.delete(relation);
        response.put("success", true);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<FollowerInfo>> getFollowers(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<FollowRelation> followRelations = followRelationRepository.findFollowersByFollowing(user);
        List<FollowerInfo> followersInfo = new ArrayList<>();
        for (FollowRelation relation : followRelations) {
            followersInfo.add(new FollowerInfo(relation.getFollower().getId(), relation.getFollower().getUsername()));
        }

        return ResponseEntity.ok(followersInfo);
    }

    @Override
    public ResponseEntity<List<FollowerInfo>> getFollowing(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        List<FollowRelation> followRelations = followRelationRepository.findByFollowing(user);
        List<FollowerInfo> followingInfo = new ArrayList<>();
        for (FollowRelation relation : followRelations) {
            followingInfo.add(new FollowerInfo(relation.getFollowing().getId(), relation.getFollowing().getUsername()));
        }

        return ResponseEntity.ok(followingInfo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> toggleFollow(Long requesterId, Long targetId) {
        log.info("Attempting to toggle follow relation between requesterId: {} and targetId: {}", requesterId,
                targetId);
        Map<String, Object> response = new HashMap<>();

        // Check if a user is trying to follow themselves
        if (requesterId.equals(targetId)) {
            response.put("success", false);
            response.put("message", "Users cannot follow themselves");
            return ResponseEntity.badRequest().body(response);
        }

        User requester = userRepository.findById(requesterId).orElse(null);
        User target = userRepository.findById(targetId).orElse(null);

        if (requester == null || target == null) {
            response.put("success", false);
            return ResponseEntity.badRequest().body(response);
        }

        FollowRelation relation = followRelationRepository.findByFollowerAndFollowing(requester, target);
        boolean isNowFollowing;
        if (relation == null) {
            relation = new FollowRelation();
            relation.setFollower(requester);
            relation.setFollowing(target);
            followRelationRepository.save(relation);
            response.put("message", "Followed successfully");
            isNowFollowing = true;
        } else {
            followRelationRepository.delete(relation);
            response.put("message", "Unfollowed successfully");
            isNowFollowing = false;
        }

        response.put("success", true);
        response.put("isFollowing", isNowFollowing); // 여기서 "isFollowing" 값을 추가합니다.
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Boolean> isFollowing(Long requesterId, Long targetId) {
        User requester = userRepository.findById(requesterId).orElse(null);
        User target = userRepository.findById(targetId).orElse(null);

        if (requester == null || target == null) {
            return ResponseEntity.badRequest().body(false);
        }

        FollowRelation relation = followRelationRepository.findByFollowerAndFollowing(requester, target);
        return ResponseEntity.ok(relation != null);
    }


}

