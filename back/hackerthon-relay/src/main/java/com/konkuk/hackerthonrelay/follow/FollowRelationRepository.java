package com.konkuk.hackerthonrelay.follow;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.konkuk.hackerthonrelay.user.User;

@Repository
public interface FollowRelationRepository extends JpaRepository<FollowRelation, Long> {

    FollowRelation findByFollowerAndFollowing(User follower, User following);
    List<FollowRelation> findFollowersByFollowing(User following);
    List<User> findFollowingByFollower(User follower);

    List<FollowRelation> findByFollowing(User following);

	List<FollowRelation> findByFollower(User follower);

}

