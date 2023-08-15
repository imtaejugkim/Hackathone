package com.konkuk.hackerthonrelay.user;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter @Getter
public class UserDto {

    private Long userId;
    private String email;
    private String username;
    private String userIdString; // String 타입의 userId 필드
    private int relayReceivedCount;
    private Integer score;
    private String profilePictureUrl;
    private String backgroundPictureUrl;
    private List<Long> createdPostIds;
    private List<Long> followerIds;
    private List<Long> followingIds;
    private boolean success;
    private boolean selfProfile = false;
    private boolean editProfile = false;
    private boolean following = false;

    // 기본 생성자
    public UserDto() {
    }

    // 모든 필드를 인자로 받는 생성자
    public UserDto(Long userId, String email, String username, Integer score, String profilePictureUrl,
                   String backgroundPictureUrl) {
        this.userId = userId;
        this.email = email;
        this.username = username;
        this.score = score;
        this.profilePictureUrl = profilePictureUrl;
        this.backgroundPictureUrl = backgroundPictureUrl;
    }

}