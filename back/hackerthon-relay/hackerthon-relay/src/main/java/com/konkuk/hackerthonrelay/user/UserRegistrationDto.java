package com.konkuk.hackerthonrelay.user;

import lombok.Getter;
import lombok.Setter;

@Setter @Getter
public class UserRegistrationDto {

    private String username;
    private String userId;
    private String email;

    public UserRegistrationDto() {
    }

}
