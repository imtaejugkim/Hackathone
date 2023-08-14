package com.konkuk.hackerthonrelay.search;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.konkuk.hackerthonrelay.user.User;

public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
	List<SearchHistory> findByUser(User user);
}
