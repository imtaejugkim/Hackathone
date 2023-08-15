package com.konkuk.hackerthonrelay.user;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class RankingScheduler {

    private final UserRepository userRepository;

    public RankingScheduler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Scheduled(fixedRate = 600000) // 매 10분마다 실행 (밀리초 단위) 600000
    @Transactional
    public void updateRanking() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            user.updateScoreForInitialization(); // score를 초기화하는 메소드 호출 추가
            int score = (int) user.calculateRankingScore();
            user.setScore(score);
        }
    }
}