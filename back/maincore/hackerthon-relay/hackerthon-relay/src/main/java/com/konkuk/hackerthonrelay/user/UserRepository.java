package com.konkuk.hackerthonrelay.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	User findByUserId(String userId);
	User findByEmail(String email);

	List<User> findByUsernameContainingIgnoreCase(String keyword); // Jpa 때문에 메서드 이름 잘 넣어야함 Username
	List<User> findByUserIdContainingIgnoreCase(String userId);

}

