package com.konkuk.hackerthonrelay.comment;

import com.konkuk.hackerthonrelay.pictureupload.Post;
import com.konkuk.hackerthonrelay.pictureupload.PostRepository;
import com.konkuk.hackerthonrelay.user.User;
import com.konkuk.hackerthonrelay.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    // Comment를 CommentDto로 변환하는 별도의 메서드
    public CommentDto convertToDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        if (comment.getAuthor() != null) {
            dto.setAuthorId(comment.getAuthor().getId()); // 작성자 ID 설정
            dto.setAuthorName(comment.getAuthor().getUsername()); // 작성자 이름 설정
        }
        if (comment.getPost() != null) { // postId 설정
            dto.setPostId(comment.getPost().getId());
        }
        return dto;
    }

    // 댓글 작성 메서드
    public Comment createComment(Comment comment, Long postId, Long authorId) {
        Post post = postRepository.findById(postId).orElse(null);
        User author = userRepository.findById(authorId).orElse(null);

        if (post != null && author != null) {
            comment.setPost(post);
            comment.setAuthor(author);

            // 댓글 수 증가
            post.setCommentCount(post.getCommentCount() + 1);
            postRepository.save(post); // 게시물 업데이트

            return commentRepository.save(comment);
        }
        return null;
    }


    // 댓글 수정 메서드
    public Comment updateComment(Long commentId, String newContent) {
        Comment comment = getComment(commentId);
        if (comment != null) {
            comment.setContent(newContent);
            return commentRepository.save(comment);
        }
        return null;
    }

    // 댓글 삭제 메서드
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    // 특정 게시물의 모든 댓글 가져오기
    public List<Comment> getCommentsByPostId(Long postId) {
        return commentRepository.findByPostId(postId);
    }

    // 댓글 가져오기 메서드
    public Comment getComment(Long commentId) {
        return commentRepository.findById(commentId).orElse(null);
    }

}


