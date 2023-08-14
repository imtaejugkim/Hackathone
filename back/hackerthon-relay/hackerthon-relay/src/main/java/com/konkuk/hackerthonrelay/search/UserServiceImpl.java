package com.konkuk.hackerthonrelay.search;

import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;

	@Override
	public List<User> searchUsers(String keyword) {
		List<User> usersByUsername = userRepository.findByUsernameContainingIgnoreCase(keyword);
		List<User> usersByUserId = userRepository.findByUserIdContainingIgnoreCase(keyword);

		// 합치기 전 중복 사용자 제거
		Set<User> resultSet = new HashSet<>(usersByUsername);
		resultSet.addAll(usersByUserId);

		return new ArrayList<>(resultSet);
	}

	@Override
	public User findByUserId(String userId) {
		return userRepository.findByUserId(userId);
	}
}

