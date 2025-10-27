package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Metric;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

@SpringBootTest
@AutoConfigureTestDatabase
class AchievementServiceTests {

    @Autowired
    private AchievementService achievementService;

    @Test
    void shouldFindAllAchievements() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Achievement> achievements = achievementService.getAchievements(pageable);
        assertNotNull(achievements);
        assertNotEquals(0, achievements.getTotalElements());
    }

    @Test
    void shouldFindAchievementById() {
        Achievement achievement = achievementService.getById(1);
        assertNotNull(achievement);
        assertEquals(1, achievement.getId());
    }

    @Test
    void shouldReturnNullWhenAchievementNotFoundById() {
        Achievement achievement = achievementService.getById(999);
        assertEquals(null, achievement);
    }

    @Test
    void shouldSaveNewAchievement() {
        Achievement achievement = new Achievement();
        achievement.setName("Legendary Victory");
        achievement.setMetric(Metric.VICTORIES);
        achievement.setThreshold(20);
        achievement.setDescription("Dummy description");

        Achievement savedAchievement = achievementService.saveAchievement(achievement);
        assertNotNull(savedAchievement.getId());
        assertEquals("Legendary Victory", savedAchievement.getName());
        assertEquals(Metric.VICTORIES, savedAchievement.getMetric());
    }

    @Test
    @Transactional
    void shouldDeleteAchievementById() {
        Achievement achievement = new Achievement();
        achievement.setName("Temporary Achievement");
        achievement.setMetric(Metric.DEFEATS);
        Achievement savedAchievement = achievementService.saveAchievement(achievement);

        achievementService.deleteAchievementById(savedAchievement.getId());
        assertEquals(null, achievementService.getById(savedAchievement.getId()));
    }

    @Test
    @Transactional
    void shouldEditAchievement() {
        Achievement achievement = achievementService.getById(1);
        achievement.setDescription("New description");
        achievement.setName("New name");
        Achievement savedAchievement = achievementService.saveAchievement(achievement);
        assertEquals("New name", savedAchievement.getName());
        assertEquals("New description", savedAchievement.getDescription());
    }

    
}
