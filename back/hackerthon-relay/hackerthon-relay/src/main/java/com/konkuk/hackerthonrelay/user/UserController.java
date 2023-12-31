package com.konkuk.hackerthonrelay.user;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.konkuk.hackerthonrelay.follow.FollowService;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserRepository userRepository;
	private UserService userService;
	private FollowService followService;

	@Autowired // 생성자 주입
	public UserController(UserRepository userRepository, UserService userService, FollowService followService) {
		this.userRepository = userRepository;
		this.userService = userService;
		this.followService = followService;
	}

	@PostMapping("/check-email")
	public ResponseEntity<Map<String, Object>> checkEmailExistence(@RequestBody EmailRequest email) {
		log.info("email = {}", email);
		User existingUserByEmail = userRepository.findByEmail(email.getEmail());
		Map<String, Object> response = new HashMap<>();

		if (existingUserByEmail != null) {
			response.put("exists", true);
			response.put("id", existingUserByEmail.getId());
			response.put("userId", existingUserByEmail.getUserId());
			return ResponseEntity.ok(response);
		} else {
			response.put("exists", false);
			return ResponseEntity.ok(response);
		}
	}

	@PostMapping("/register")
	public ResponseEntity<Map<String, Object>> registerUser(@RequestBody UserRegistrationDto dto) {
		Map<String, Object> response = new HashMap<>();

		// 유효성 검사: userId는 영어로만 구성되어야 함
		if (!dto.getUserId().matches("^[A-Za-z]*$")) {
			response.put("success", false);
			response.put("id", -2L); // userId가 영어로만 구성되지 않은 경우
			return ResponseEntity.badRequest().body(response);
		}

		// 유효성 검사: userId는 10자까지 username은 5글자까지여야 한다.
		if (dto.getUserId().length() > 10 || dto.getUsername().length() > 5) {
			response.put("success", false);
			response.put("id", -3L); // userId 또는 username이 5글자를 초과한 경우
			return ResponseEntity.badRequest().body(response);
		}



		User existingUserByUserId = userRepository.findByUserId(dto.getUserId());
		if (existingUserByUserId != null) {
			response.put("success", false);
			response.put("id", -1L); // userId가 존재하는 경우
			return ResponseEntity.badRequest().body(response);
		}

		User user = new User();
		user.setUserId(dto.getUserId());
		user.setUsername(dto.getUsername());
		user.setEmail(dto.getEmail());
		user.setProfilePictureUrl("/profiles/basicProfile.jpg");

		try {
			User savedUser = userRepository.save(user);
			response.put("success", true);
			response.put("id", savedUser.getId());
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			log.error("Error occurred while registering user: {}", e.getMessage());
			response.put("success", false);
			response.put("id", 0L); // 그외의 문제가 생긴 경우
			return ResponseEntity.badRequest().body(response);
		}
	}

	@GetMapping("/{targetUserId}/{currentUserId}")
	public ResponseEntity<UserDto> getUserById(@PathVariable Long targetUserId, @PathVariable Long currentUserId) {
		try {
			User targetUser = userService.getUserById(targetUserId);

			UserDto userDto = targetUser.toDto();

			// 현재 사용자와 조회하려는 사용자가 동일한 경우
			if (targetUserId.equals(currentUserId)) {
				userDto.setSelfProfile(true);
				userDto.setFollowing(false); // 이 부분은 이제 무의미해집니다.
				userDto.setEditProfile(true); // 이 사용자는 프로필을 편집할 수 있습니다.
			} else {
				userDto.setSelfProfile(false);
				userDto.setEditProfile(false); // 다른 사용자의 프로필은 편집할 수 없습니다.

				// 팔로우 상태 확인
				boolean isFollowing = followService.isFollowing(currentUserId, targetUserId).getBody();

				userDto.setFollowing(isFollowing);
			}

			return ResponseEntity.ok(userDto);
		} catch (EntityNotFoundException e) {
			log.error("User not found with id: {}", targetUserId, e);
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		} catch (Exception e) {
			log.error("Error occurred while fetching user with id: {}", targetUserId, e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
	}

	@PostMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, Object>> updateUser(@PathVariable Long id,
			@RequestPart(value = "username", required = false) String username,
			@RequestPart(value = "userId", required = false) String userId,
			@RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
			@RequestPart(value = "backgroundImage", required = false) MultipartFile backgroundImage) {

		Map<String, Object> response = new HashMap<>();

		try {
			log.info("profileImage = {}", profileImage);
			log.info("backgroundImage = {}", backgroundImage);
			User user = userRepository.findById(id).orElse(null);

			if (username != null) {
				user.setUsername(username);
			}
			if (userId != null) {
				User existingUserWithSameUserId = userRepository.findByUserId(userId);

				if (existingUserWithSameUserId != null && !existingUserWithSameUserId.getId().equals(id)) {
					response.put("success", false);
					response.put("message", "exist");
					return ResponseEntity.ok(response);
				}
				user.setUserId(userId);
			}

			if (user == null) {
				response.put("success", false);
				response.put("message", "User not found");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			if (profileImage != null && !profileImage.isEmpty()) {
				userService.saveProfileImage(id, profileImage);
			}
			if (backgroundImage != null && !backgroundImage.isEmpty()) {
				userService.saveBackgroundImage(id, backgroundImage);
			}

			userRepository.save(user);

			response.put("success", true);
			response.put("message", "User updated successfully.");
			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Failed to update user.");
			response.put("details", e.getMessage()); // Optionally provide more details about the error
			return ResponseEntity.status(500).body(response);
		}
	}


	@DeleteMapping("/delete/{id}")
	public ResponseEntity<String> deleteUser(@PathVariable Long id) {
		User user = userRepository.findById(id).orElse(null);
		if (user == null) {
	
			return ResponseEntity.notFound().build();
		}
		userRepository.delete(user);
		return ResponseEntity.ok("User deleted successfully.");
	}

	@GetMapping("/ranking")
	public ResponseEntity<List<UserRankingDto>> getRanking() {
		List<User> users = userRepository.findAll();
		List<UserRankingDto> ranking = users.stream()
				.map(user -> new UserRankingDto(user.getId(), user.getUserId(), user.getUsername(), user.getScore(),
						user.getProfilePictureUrl()))
				.sorted(Comparator.comparing(UserRankingDto::getScore).reversed()).collect(Collectors.toList());

		return ResponseEntity.ok(ranking);
	}


}
