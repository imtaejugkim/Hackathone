package com.konkuk.hackerthonrelay.follow;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class FollowerInfo {

    private Long id;
    private String username;

    public FollowerInfo(Long id, String username) {
        this.id = id;
        this.username = username;
    }
}
