package com.konkuk.hackerthonrelay.pictureupload.tag;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


// 이 코드 태그의 정확한 순서 보장 x, 입력 키워드와 일치하는 문자열을 포함하는 태그 찾아서 반환
@Slf4j
@Service
public class TagService {

    private final TagRepository tagRepository;

    @Autowired
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createHashTag(String name) {
        Tag hashTag = new Tag();
        hashTag.setName(name);
        return tagRepository.save(hashTag);
    }

//    public Tag findHashTagByName(String name) {
//        return tagRepository.findByName(name);
//    }
}