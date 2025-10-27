package es.us.dp1.lx_xy_24_25.your_game_name.achievements;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.AchievementRestController;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Metric;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

@WebMvcTest(controllers = AchievementRestController.class)
class AchievementRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AchievementService achievementService;

    @InjectMocks
    private AchievementRestController achievementRestController;

    @Autowired
    private ObjectMapper objectMapper;

    private Achievement testAchievement;

    @BeforeEach
    void setUp() {
        testAchievement = new Achievement();
        testAchievement.setId(1);
        testAchievement.setName("Test Achievement");
        testAchievement.setDescription("Description <THRESHOLD>");
        testAchievement.setThreshold(100);
        testAchievement.setMetric(Metric.GAMES_PLAYED);
        testAchievement.setBadgeImage("badge.png");
        testAchievement.setProfilePictureUri("profile.png");
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testFindAllAchievements() throws Exception {
        when(achievementService.getAchievements(any())).thenReturn(new PageImpl<>(List.of(testAchievement)));

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/achievements")
                .with(csrf())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Test Achievement"));
        verify(achievementService, times(1)).getAchievements(any());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")

    void testFindAchievementById() throws Exception {
        when(achievementService.getById(1)).thenReturn(testAchievement);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/achievements/1").with(csrf())

                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Achievement"));

        verify(achievementService, times(1)).getById(1);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")

    void testCreateAchievement() throws Exception {
        when(achievementService.saveAchievement(any())).thenReturn(testAchievement);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/achievements").with(csrf())

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAchievement)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Test Achievement"));

        verify(achievementService, times(1)).saveAchievement(any());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")

    void testModifyAchievement() throws Exception {
        Achievement updatedAchievement = new Achievement();
        updatedAchievement.setId(1);
        updatedAchievement.setName("Updated Achievement");
        updatedAchievement.setDescription("Updated Description <THRESHOLD>");
        updatedAchievement.setThreshold(200);
        updatedAchievement.setMetric(Metric.GAMES_PLAYED);
        updatedAchievement.setBadgeImage("updated_badge.png");
        updatedAchievement.setProfilePictureUri("updated_profile.png");

        when(achievementService.getById(1)).thenReturn(testAchievement);
        when(achievementService.saveAchievement(any())).thenReturn(updatedAchievement);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/achievements/1").with(csrf())

                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedAchievement)))
                .andExpect(status().isNoContent());

        verify(achievementService, times(1)).getById(1);
        verify(achievementService, times(1)).saveAchievement(any());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")

    void testDeleteAchievement() throws Exception {
        doNothing().when(achievementService).deleteAchievementById(1);
        when(achievementService.getById(1)).thenReturn(testAchievement);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/achievements/1").with(csrf()))

                .andExpect(status().isNoContent());

        verify(achievementService, times(1)).deleteAchievementById(1);
    }
}
