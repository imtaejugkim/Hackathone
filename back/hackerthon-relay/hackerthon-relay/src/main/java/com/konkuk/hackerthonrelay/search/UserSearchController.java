package com.konkuk.hackerthonrelay.search;

import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/user/search")
public class UserSearchController {
	private static final Logger log = LoggerFactory.getLogger(UserSearchController.class);

	private final UserService userService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private SearchHistoryRepository searchHistoryRepository;

	@Autowired
	public UserSearchController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping
	public ResponseEntity<?> searchUsers(@RequestParam("q") String keyword, @RequestParam Long Id) {
		log.info("keyword = {}", keyword);
		if (keyword == null || keyword.trim().isEmpty()) {
			return ResponseEntity.badRequest().body("Keyword is missing or empty.");
		}

		User currentUser = userRepository.findById(Id).orElse(null);
		if (currentUser == null) {
			return ResponseEntity.badRequest().body("User with given ID not found.");
		}

		SearchHistory history = new SearchHistory();
		history.setUser(currentUser);
		history.setKeyword(keyword);
		searchHistoryRepository.save(history);

		List<User> searchResults = userService.searchUsers(keyword);

		if (searchResults.isEmpty()) {
			return ResponseEntity.ok().body("No users found for given keyword.");
		}

		return ResponseEntity.ok(searchResults);
	}

	@GetMapping("/history")
	public ResponseEntity<?> getSearchHistory(@RequestParam Long Id) {
		User currentUser = userRepository.findById(Id).orElse(null);
		if (currentUser == null) {
			return ResponseEntity.badRequest().body("User with given ID not found.");
		}

		List<SearchHistory> histories = searchHistoryRepository.findByUser(currentUser);
		return ResponseEntity.ok(histories);
	}

}

