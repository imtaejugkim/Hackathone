package com.konkuk.hackerthonrelay.pictureupload.tag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    Tag findByName(String name);

    //List<Post> findByTagsIn(Tag tags);
}