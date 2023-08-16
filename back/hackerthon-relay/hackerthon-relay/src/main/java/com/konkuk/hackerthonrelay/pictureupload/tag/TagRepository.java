package com.konkuk.hackerthonrelay.pictureupload.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    // 이름으로 태그 찾기
    Tag findByName(String name);
}