package com.konkuk.hackerthonrelay.pictureupload.share;

import com.konkuk.hackerthonrelay.pictureupload.Post;
import com.konkuk.hackerthonrelay.pictureupload.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ShareController {

    private PostRepository postRepository;

    @Autowired
    public ShareController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @PostMapping("/share/{postId}")
    public ResponseEntity<String> sharePost(@PathVariable Long postId, @RequestParam String userId, @RequestParam String shareComment) {
        Post originalPost = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Original post not found"));

        // Increment the share count of the original post
        originalPost.incrementShareCount();
        postRepository.save(originalPost);


        return ResponseEntity.ok("Post shared successfully");
    }
}