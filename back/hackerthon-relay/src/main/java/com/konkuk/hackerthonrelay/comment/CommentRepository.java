package com.konkuk.hackerthonrelay.comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	// 특정 게시물의 모든 댓글 가져오기
	List<Comment> findByPostId(Long postId);
}
