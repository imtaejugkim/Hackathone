package com.konkuk.hackerthonrelay.pictureupload;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class ImageUploadController {
    private final ImageUploadService uploadService;
    private final PostRepository postRepository;

    public ImageUploadController(ImageUploadService uploadService, PostRepository postRepository) {
        this.uploadService = uploadService;
        this.postRepository = postRepository;
    }

	// 새 게시물 생성
	@PostMapping("/create")
	public ResponseEntity<Long> createPost(@RequestParam("image") MultipartFile multipartFile,
			@RequestParam("theme") String theme) {
		try {
			Post post = new Post();
			post.setTheme(theme); // 게시물 테마 설정
			postRepository.save(post); // 먼저 Post를 저장하여 ID를 생성합니다.

			Image image = uploadService.uploadImage(multipartFile);
			image.setPost(post); // Image에 Post 참조를 설정합니다.
			post.getImages().add(image);
			post.setMainImage(image);

			postRepository.save(post); // Post를 다시 저장하여 변경 사항을 반영합니다.
			return new ResponseEntity<>(post.getId(), HttpStatus.CREATED); // post id 반환
		} catch (IOException e) {
			throw new RuntimeException("Could not upload file. Please try again.", e);
		}
	}


	// 이미지 추가
	@PostMapping("/add")
	public String addImage(@RequestParam("postId") Long postId, @RequestParam("image") MultipartFile multipartFile,
			@RequestParam("position") Integer position) {
		try {
			Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
			if (post.getImages().size() >= 4) {
				return "Maximum number of images reached";
			}
			Image image = uploadService.uploadImage(multipartFile);
			image.setPost(post);
			image.setPosition(position); // 사진 위치 설정
			post.getImages().add(image);

			if (post.getImages().size() == 4) {
				post.setCompleted(true); // 게시물 완성 상태로 설정
			}

            postRepository.save(post);
			return "Image added: " + multipartFile.getOriginalFilename();
		} catch (IOException e) {
			throw new RuntimeException("Could not upload file. Please try again.", e);
        }
    }

	// 릴레이 이어가기 (사용자 언급 또는 공개 설정)
	@PostMapping("/relay")
	public String relayPost(@RequestParam("postId") Long postId,
			@RequestParam(value = "user", required = false) String user) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
		post.setMentionedUser(user); // 사용자를 언급하거나 null로 설정하여 공개
		postRepository.save(post);
		return "Relay set for post: " + postId;
	}
}
