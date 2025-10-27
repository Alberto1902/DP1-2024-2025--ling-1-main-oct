package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Metric;

class AchievementTest {

    @Test
    void testEqualsAndHashCode() {
        Achievement achievement1 = new Achievement();
        achievement1.setId(1);
        achievement1.setName("Achievement 1");
        achievement1.setDescription("Description 1");
        achievement1.setBadgeImage("image1.png");
        achievement1.setThreshold(10);
        achievement1.setMetric(Metric.VICTORIES);
        achievement1.setProfilePictureUri("profile1.png");

        Achievement achievement2 = achievement1;

        Achievement achievement3 = new Achievement();
        achievement3.setId(2); // ID diferente
        achievement3.setName("Achievement 2");
        achievement3.setDescription("Description 2");
        achievement3.setBadgeImage("image2.png");
        achievement3.setThreshold(20);
        achievement3.setMetric(Metric.GAMES_PLAYED);
        achievement3.setProfilePictureUri("profile2.png");

        assertThat(achievement1).isEqualTo(achievement2);
        assertThat(achievement1.hashCode()).isEqualTo(achievement2.hashCode());

        assertThat(achievement1).isNotEqualTo(achievement3);
        assertThat(achievement1.hashCode()).isNotEqualTo(achievement3.hashCode());

        assertThat(achievement1).isNotEqualTo(null);
        assertThat(achievement1).isNotEqualTo("Some String");
    }

    @Test
    void testEqualsAndHashCodeWithNullFields() {
        Achievement achievement1 = new Achievement();
        achievement1.setId(1);
        achievement1.setName(null);
        achievement1.setDescription(null);
        achievement1.setBadgeImage(null);
        achievement1.setThreshold(0);
        achievement1.setMetric(null);
        achievement1.setProfilePictureUri(null);

        Achievement achievement2 = achievement1;
        assertThat(achievement1).isEqualTo(achievement2);
        assertThat(achievement1.hashCode()).isEqualTo(achievement2.hashCode());
    }
}
