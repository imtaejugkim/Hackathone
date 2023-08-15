package com.konkuk.hackerthonrelay.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.konkuk.hackerthonrelay.comment.Comment;
import com.konkuk.hackerthonrelay.follow.FollowRelation;
import com.konkuk.hackerthonrelay.pictureupload.Post;

import com.konkuk.hackerthonrelay.pictureupload.PostUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter @Getter
@Table(name = "users")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String username;  // 중복 허용

    @Column(nullable = false, unique = true)
    private String userId;  // 중복 불허

    @Column(nullable = false, unique = true)
    private String email;  // 중복 불허

    @OneToMany(mappedBy = "author")
    @JsonManagedReference
    private List<Comment> comments = new ArrayList<>();

    @JsonManagedReference
    @ManyToMany(mappedBy = "likedUsers")
    private Set<Post> likedPosts = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PostUser> participatedPosts = new ArrayList<>();

    @OneToMany(mappedBy = "following")
    @JsonManagedReference
    private Set<FollowRelation> followersRelation = new HashSet<>();

    @OneToMany(mappedBy = "follower")
    @JsonManagedReference
    private Set<FollowRelation> followingRelation = new HashSet<>();

    @JsonManagedReference // 난 JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Post> posts = new HashSet<>();

    //private Set<> relays = new HashSet();

    // 프로필 사진 배경 사진 - 유저 아이디와 연결해야함, 저장가능해야함, 요청하면 화면 바꿔주고 db저장 보관, id와 엮기
    private String profilePictureUrl;   // 프로필 사진 url
    private String backgroundPictureUrl;    // 배경 사진 url

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public UserDto toDto() {
        UserDto dto = new UserDto(this.getId(), this.getEmail(), this.getUsername(), this.getScore(),
                this.getProfilePictureUrl(), this.getBackgroundPictureUrl());

        // creator로 돼 있는 게시물의 Long Ids
        dto.setCreatedPostIds(this.posts.stream().filter(post -> post.getCreator().equals(this))
                .map(post -> post.getId()).collect(Collectors.toList()));

        // 팔로워 Ids
        dto.setFollowerIds(this.getFollowers().stream().map(User::getId).collect(Collectors.toList()));

        // 팔로잉 Ids
        dto.setFollowingIds(this.getFollowing().stream().map(User::getId).collect(Collectors.toList()));

        dto.setUserIdString(this.getUserId()); // String userId 설정

        dto.setRelayReceivedCount(this.getRelayReceivedCount()); // 릴레이 받은 수 설정

        dto.setSuccess(true);

        return dto;
    }

    private int score = 0; // 사용자의 점수를 저장하는 필드를 추가

    public int getRelayGivenCount() {
        // 사용자가 릴레이를 준 게시물의 수를 반환합니다.
        return posts.size();
    }

    public int getRelayReceivedCount() {
        // 사용자가 릴레이를 받은 게시물의 수를 반환합니다.
        // 'creator' 필드를 기반으로 게시물의 수를 카운트해야 합니다.
        return (int) posts.stream()
                .filter(post -> post.getMentionedUser() != null && post.getMentionedUser().equals(this)).count();
    }

    public Set<User> getFollowers() {
        Set<User> followers = new HashSet<>();
        for (FollowRelation relation : this.followersRelation) {
            followers.add(relation.getFollower());
        }
        return followers;
    }

    public Set<User> getFollowing() {
        Set<User> following = new HashSet<>();
        for (FollowRelation relation : this.followingRelation) {
            following.add(relation.getFollowing());
        }
        return following;
    }

    public void updateScoreForLike() {
        int likesWeight = 2;
        this.score += (int) likesWeight;
    }

    public void updateScoreForGivingRelay() {
        int relayGivenWeight = 1;
        this.score += (int) relayGivenWeight;
    }

    public void updateScoreForReceivingRelay() {
        int relayReceivedWeight = 3;
        this.score += (int) relayReceivedWeight;
    }

    public void updateScoreForReceivingLike(boolean isLiked) {
        int likeReceivedWeight = 2;
        if (isLiked) {
            this.score += likeReceivedWeight;
        } else {
            this.score -= likeReceivedWeight;
        }
    }


    public double calculateRankingScore() {
        int relayReceivedWeight = 3;
        int likesWeight = 2;
        int relayGivenWeight = 1;

        int relayReceivedScore = this.getRelayReceivedCount() * relayReceivedWeight;
        int likesScore = this.getPosts().stream().mapToInt(Post::getLikes).sum() * likesWeight;
        int relayGivenScore = this.getRelayGivenCount() * relayGivenWeight;

        int totalScore = (int) (relayReceivedScore + likesScore + relayGivenScore);
        this.setScore(totalScore); // 실제 score 필드에 값을 저장
        return totalScore;
    }

}
