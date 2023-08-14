package com.konkuk.hackerthonrelay.pictureupload.share;

import java.time.LocalDateTime;
import java.util.List;

public class ShareDto {

    private String userId;
    private LocalDateTime sharedAt;

    private List<String> photoUrls;
    private String originalPostId;
}
