package com.konkuk.hackerthonrelay.follow;

import com.konkuk.hackerthonrelay.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FollowRelationRepository extends JpaRepository<FollowRelation, Long> {

    FollowRelation findByFollowerAndFollowing(User follower, User following);
    List<FollowRelation> findFollowersByFollowing(User following);
    List<User> findFollowingByFollower(User follower);

    List<FollowRelation> findByFollowing(User following);
    List<FollowRelation> findByFollower(User follower);
}

