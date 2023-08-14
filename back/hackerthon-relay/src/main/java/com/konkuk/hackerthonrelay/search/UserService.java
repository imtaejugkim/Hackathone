package com.konkuk.hackerthonrelay.search;

import java.util.List;

import com.konkuk.hackerthonrelay.user.User;

public interface UserService {
	List<User> searchUsers(String keyword);

	User findByUserId(String userId);
}