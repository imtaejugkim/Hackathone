package com.konkuk.hackerthonrelay.search;

import com.konkuk.hackerthonrelay.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
	List<SearchHistory> findByUser(User user);
}
