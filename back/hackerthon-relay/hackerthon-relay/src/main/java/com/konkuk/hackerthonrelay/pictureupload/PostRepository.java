package com.konkuk.hackerthonrelay.pictureupload;

import java.time.LocalDateTime;
import java.util.List;

import com.konkuk.hackerthonrelay.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByThemeAndUserIdInOrderByCreatedAtDesc(Integer theme, List<Long> userIds, Pageable pageable);

	List<Post> findByThemeAndUserIdInAndCreatedAtBeforeOrderByCreatedAtDesc(Integer theme, List<Long> userIds,
																			LocalDateTime lastPostDate, Pageable pageable);

	List<Post> findByUser(User user);

	List<Post> findByCreatedAtAfter(LocalDateTime tenMinutesAgo);
}
