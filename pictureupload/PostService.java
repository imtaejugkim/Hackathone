package com.konkuk.hackerthonrelay.pictureupload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class PostService {

	@Autowired
	private PostRepository postRepository;

	// ... other service methods ...

	@Scheduled(fixedDelay = 60000) // This method will be executed every 60 seconds
	public void checkPostRemainingTime() {
		List<Post> posts = postRepository.findAll();
		for (Post post : posts) {
			post.decreaseRemainingTime();
			post.passEditRight();
			postRepository.save(post);
		}
	}
}
