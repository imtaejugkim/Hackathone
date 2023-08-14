package com.konkuk.hackerthonrelay.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.naming.Name;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/{userId}")
    public User viewProfile(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    @PostMapping("/{userId}/upload-profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());
            // 파일을 저장하고 사용자 엔터티에 파일 경로를 업데이트하는 로직을 여기에 구현

            // 저장할 파일 경로 설정 (예시: 내 컴퓨터의 "C:/uploads" 폴더에 저장)
            Path uploadDir = Paths.get("src/main/resources/static/profiles");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 사용자 엔터티의 프로필 사진 경로 업데이트
            user.setProfilePictureUrl(filePath.toString());
            userRepository.save(user);


            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }

    @PostMapping("/{userId}/upload-background-image")
    public ResponseEntity<String> uploadBackgroundImage(
            @PathVariable Long userId,
            @RequestParam("file") MultipartFile file
    ) {
        User user = userRepository.findById(userId).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Please select a file to upload");
        }

        try {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            // 저장할 파일 경로 설정 (예시: 내 컴퓨터의 "C:/uploads" 폴더에 저장)
            Path uploadDir = Paths.get("src/main/resources/static/profiles");
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            Path filePath = uploadDir.resolve(fileName);

            // 파일 저장
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 사용자 엔터티의 프로필 사진 경로 업데이트
            user.setBackgroundPictureUrl(filePath.toString());
            userRepository.save(user);


            return ResponseEntity.ok("File uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to upload file");
        }
    }
}