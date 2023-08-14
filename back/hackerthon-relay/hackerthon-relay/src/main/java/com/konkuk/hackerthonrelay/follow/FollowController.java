package com.konkuk.hackerthonrelay.follow;

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

    public FollowController(FollowRelationRepository followRelationRepository, UserRepository userRepository, FollowService followService) {
        this.followRelationRepository = followRelationRepository;
        this.userRepository = userRepository;
        this.followService = followService;
    }

    @PostMapping("/toggle/{requesterId}/{targetId}")
    public ResponseEntity<Map<String, Object>> toggleFollow(@PathVariable Long requesterId,
                                                            @PathVariable Long targetId) {
        return followService.toggleFollow(requesterId, targetId);
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
