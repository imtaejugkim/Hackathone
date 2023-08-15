package com.konkuk.hackerthonrelay.pictureupload;

import java.time.LocalDateTime;
import java.util.List;

import com.konkuk.hackerthonrelay.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
	
	List<Post> findByIsCompletedFalse();

	List<Post> findByThemeAndUserId(Integer theme, Long Id);

	List<Post> findByThemeAndCreatorIdInOrderByCreatedAtDesc(Integer theme, List<Long> creatorIds, Pageable pageable);

	List<Post> findByThemeAndCreatorIdInAndCreatedAtBeforeOrderByCreatedAtDesc(Integer theme, List<Long> creatorIds,
			LocalDateTime createdAt, Pageable pageable);

	List<Post> findByCreator(User user);
	
	List<Post> findByCreatedAtAfter(LocalDateTime tenMinutesAgo);
}
