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

    @Scheduled(fixedRate = 10000) // 매 10분마다 실행 (밀리초 단위) 600000
    @Transactional
    public void updateRanking() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            int score = (int)user.calculateRankingScore();
            user.setScore(score);
        }
    }
}