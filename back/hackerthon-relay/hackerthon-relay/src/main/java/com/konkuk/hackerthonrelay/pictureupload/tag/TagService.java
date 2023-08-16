package com.konkuk.hackerthonrelay.pictureupload.tag;

import com.konkuk.hackerthonrelay.pictureupload.Post;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;


// 이 코드 태그의 정확한 순서 보장 x, 입력 키워드와 일치하는 문자열을 포함하는 태그 찾아서 반환
@Slf4j
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }


    // 해시 태그 만들기
    public Tag createHashTag(String name) {
        Tag hashTag = new Tag();
        hashTag.setName(name);
        return tagRepository.save(hashTag);
    }

    // 태그로 게시물 가져오기
    public Set<Post> getPostsByTag(String tagName) {
        Tag tag = tagRepository.findByName(tagName);
        if (tag == null) {
            return new HashSet<>(); // 태그가 없으면 빈 Set 반환
        }
        return tag.getPosts();
    }

}