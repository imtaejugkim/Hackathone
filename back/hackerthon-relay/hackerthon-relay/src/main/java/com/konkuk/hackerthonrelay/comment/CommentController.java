package com.konkuk.hackerthonrelay.comment;

import com.konkuk.hackerthonrelay.notification.Notification;
import com.konkuk.hackerthonrelay.notification.NotificationRepository;
import com.konkuk.hackerthonrelay.pictureupload.Post;
import com.konkuk.hackerthonrelay.pictureupload.PostRepository;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @Autowired
    private NotificationRepository notificationRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    // 댓글 작성
    @PostMapping("/")
    public ResponseEntity<CommentDto> createComment(@RequestBody CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        Comment createdComment = commentService.createComment(comment, commentDto.getPostId(),
                commentDto.getAuthorId());
        CommentDto responseDto = commentService.convertToDto(createdComment);

        // 알림 생성
        Post post = postRepository.findById(commentDto.getPostId()).orElse(null);
        if (post != null) {
            User recipient = post.getCreator(); // 게시물의 작성자
            Notification notification = new Notification();
            notification.setRecipient(recipient); // 게시물의 작성자를 알림의 수신자로 설정

            User author = userRepository.findById(commentDto.getAuthorId()).orElse(null); // 댓글 작성자 정보 가져오기
            if (author != null) {
                notification.setMessage(author.getUsername() + "님이 댓글을 남겼습니다.");
                notification.setUserId(author.getId());
                notification.setUserIdString(author.getUserId()); // String 형태의 userId 설정
                notification.setUserName(author.getUsername());
                notification.setUserProfileUrl(author.getProfilePictureUrl()); // 작성자의 프로필 사진 URL 설정
            } else {
                notification.setMessage("Unknown user left a comment.");
                notification.setUserId(null);
                notification.setUserIdString(null); // String 형태의 userId 설정
                notification.setUserName(null);
                notification.setUserProfileUrl(null);
            }

            notification.setPostId(commentDto.getPostId());
            notification.setType(Notification.NotificationType.COMMENT);
            notificationRepository.save(notification);
        }

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

