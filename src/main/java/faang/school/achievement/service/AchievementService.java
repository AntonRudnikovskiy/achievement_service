package faang.school.achievement.service;

import faang.school.achievement.dto.achievement.AchievementEvent;
import faang.school.achievement.messaging.AchievementPublisher;
import faang.school.achievement.model.Achievement;
import faang.school.achievement.model.UserAchievement;
import faang.school.achievement.repository.AchievementProgressRepository;
import faang.school.achievement.repository.AchievementRepository;
import faang.school.achievement.repository.UserAchievementRepository;
import faang.school.achievement.util.exception.AchievementNotCreatedException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AchievementService {
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementProgressRepository achievementProgressRepository;
    private final AchievementPublisher achievementPublisher;

    @Cacheable(cacheNames = "achievements_by_title", key = "#title")
    public Achievement getAchievementByTitle(String title) {
        return achievementRepository.findByTitle(title).orElseThrow(() ->
                new AchievementNotCreatedException(String.format("Achievement with title %s not found", title)));
    }

    @Transactional
    @CachePut(cacheNames = "achievements_progress", key = "#userId")
    public void updateAchievementProgress(long userId, Achievement achievement) {
        if (userAchievementRepository.existsByUserIdAndAchievementId(userId, achievement.getId())) return;

        achievementProgressRepository.createProgressIfNecessary(userId, achievement.getId());
        var progress = achievementProgressRepository.findByUserIdAndAchievementId(userId, achievement.getId()).get();
        progress.increment();

        if (progress.getCurrentPoints() >= achievement.getPoints()) {
            UserAchievement userAchievement = UserAchievement.builder().userId(userId).achievement(achievement).build();
            userAchievementRepository.save(userAchievement);
            achievementPublisher.publish(new AchievementEvent(userId, achievement.getTitle(),achievement.getDescription()));
            log.info("User with id: " + userId + " has received achievement: " + achievement.getTitle());
        }
    }
}
