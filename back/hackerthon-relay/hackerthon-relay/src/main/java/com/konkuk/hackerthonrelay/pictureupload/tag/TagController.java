package com.konkuk.hackerthonrelay.pictureupload.tag;

import com.konkuk.hackerthonrelay.pictureupload.Post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/tags")
public class TagController {
    @Autowired
    private TagService tagService;

    // 게시물에 연결된 해시태그를 검색하여 관련된 게시물을 찾는 기능
    @GetMapping("/{tagName}")
    public ResponseEntity<Set<Post>> getPostsByTag(@PathVariable String tagName) {
        Set<Post> posts = tagService.getPostsByTag(tagName);
        return ResponseEntity.ok(posts);
    }
}