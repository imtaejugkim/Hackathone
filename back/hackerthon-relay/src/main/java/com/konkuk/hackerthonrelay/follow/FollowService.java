package com.konkuk.hackerthonrelay.follow;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

public interface FollowService {
	ResponseEntity<Map<String, Object>> followUser(Long followerId, Long followingId);

	ResponseEntity<Map<String, Object>> unfollowUser(Long followerId, Long followingId);

	ResponseEntity<List<FollowerInfo>> getFollowers(Long userId);

	ResponseEntity<List<FollowerInfo>> getFollowing(Long userId);

	ResponseEntity<Map<String, Object>> toggleFollow(Long requesterId, Long targetId);

	ResponseEntity<Boolean> isFollowing(Long requesterId, Long targetId);

}
