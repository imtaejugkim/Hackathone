package com.konkuk.hackerthonrelay.pictureupload.tag;

import com.konkuk.hackerthonrelay.pictureupload.Post;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter @Getter
public class TagDto {

    private Long tagId; // 태그 번호
    private String tagName; // 태그 이름
    private List<Long> createdPostIds; // 만든 게시글 Id

    public TagDto(Long tagid, String tagName) {
        this.tagId = tagid;
        this.tagName = tagName;
    }

}
