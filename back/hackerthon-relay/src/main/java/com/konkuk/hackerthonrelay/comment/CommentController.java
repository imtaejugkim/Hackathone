package com.konkuk.hackerthonrelay.comment;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

	@Autowired
	private CommentService commentService;

	// 댓글 작성
	@PostMapping("/")
	public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
		Comment comment = new Comment();
		comment.setContent(commentDto.getContent());
		Comment createdComment = commentService.createComment(comment, commentDto.getPostId(),
				commentDto.getAuthorId());
		CommentDto responseDto = commentService.convertToDto(createdComment);
		return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
	}

	// 댓글 수정
	@PutMapping("/{commentId}")
	public ResponseEntity<CommentDto> updateComment(@PathVariable Long commentId, @RequestBody String newContent) {
		Comment updatedComment = commentService.updateComment(commentId, newContent);
		if (updatedComment == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		}
		return ResponseEntity.ok(commentService.convertToDto(updatedComment));
	}

	// 댓글 삭제
	@DeleteMapping("/{commentId}")
	public ResponseEntity<String> deleteComment(@PathVariable Long commentId) {
		commentService.deleteComment(commentId);
		return ResponseEntity.ok("Comment deleted successfully");
	}

	// 특정 게시물의 모든 댓글 가져오기
	@GetMapping("/post/{postId}")
	public ResponseEntity<List<CommentDto>> getCommentsByPostId(@PathVariable Long postId) {
		List<Comment> comments = commentService.getCommentsByPostId(postId);
		List<CommentDto> commentDtos = comments.stream().map(commentService::convertToDto).collect(Collectors.toList());
		return ResponseEntity.ok(commentDtos);
	}
}
