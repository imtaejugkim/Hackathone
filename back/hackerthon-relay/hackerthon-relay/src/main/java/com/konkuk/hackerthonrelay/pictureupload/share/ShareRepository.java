package com.konkuk.hackerthonrelay.pictureupload.share;

import com.konkuk.hackerthonrelay.pictureupload.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShareRepository extends JpaRepository<Post, Long> {
}
