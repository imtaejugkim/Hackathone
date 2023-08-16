package com.konkuk.hackerthonrelay.user;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;

// 사용자 프로필 사진 업로드 및 엔티티 업데이트
@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public void saveProfileImage(Long userId, MultipartFile profileImage) throws IOException {
        User user = getUserById(userId);

        String profileImageFileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
        Path profileImageUploadDir = Paths.get("src/main/resources/static/profiles");
        if (!Files.exists(profileImageUploadDir)) {
            Files.createDirectories(profileImageUploadDir);
        }
        Path profileImageFilePath = profileImageUploadDir.resolve(profileImageFileName);
        Files.copy(profileImage.getInputStream(), profileImageFilePath, StandardCopyOption.REPLACE_EXISTING);

        //user.setProfilePictureUrl(profileImageFilePath.toString());
        //userRepository.save(user);

        // 변경된 부분: 프론트엔드에서 사용할 웹 경로를 설정
        user.setProfilePictureUrl("/profiles/" + profileImageFileName);
        userRepository.save(user);
    }

    public void saveBackgroundImage(Long userId, MultipartFile backgroundImage) throws IOException {
        User user = getUserById(userId);

        String backgroundImageFileName = StringUtils.cleanPath(backgroundImage.getOriginalFilename());
        Path backgroundImageUploadDir = Paths.get("src/main/resources/static/profiles");
        if (!Files.exists(backgroundImageUploadDir)) {
            Files.createDirectories(backgroundImageUploadDir);
        }
        Path backgroundImageFilePath = backgroundImageUploadDir.resolve(backgroundImageFileName);
        Files.copy(backgroundImage.getInputStream(), backgroundImageFilePath, StandardCopyOption.REPLACE_EXISTING);

        //user.setBackgroundPictureUrl(backgroundImageFilePath.toString());
        //userRepository.save(user);

        // 변경된 부분: 프론트엔드에서 사용할 웹 경로를 설정
        user.setBackgroundPictureUrl("/profiles/" + backgroundImageFileName);
        userRepository.save(user);
    }

    public void updateProfilePictures(Long userId, String profilePictureUrl, String backgroundPictureUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setProfilePictureUrl(profilePictureUrl);
        user.setBackgroundPictureUrl(backgroundPictureUrl);

        userRepository.save(user);
    }
}
