package com.konkuk.hackerthonrelay.search;

import com.konkuk.hackerthonrelay.user.User;

import java.util.List;

public interface UserService {
	List<User> searchUsers(String keyword);

	User findByUserId(String userId);
}