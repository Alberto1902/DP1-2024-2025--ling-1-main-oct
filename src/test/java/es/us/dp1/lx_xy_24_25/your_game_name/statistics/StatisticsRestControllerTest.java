package es.us.dp1.lx_xy_24_25.your_game_name.statistics;


import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(StatisticsRestController.class)
public class StatisticsRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    private final String BASE_URL = "/api/v1/statistics";

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testGetGlobalStatistics() throws Exception {
        Statistics globalStats = new Statistics();
        when(statisticsService.getGlobalStatistics()).thenReturn(globalStats);

        mockMvc.perform(get(BASE_URL + "/global")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(statisticsService, times(1)).getGlobalStatistics();
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testPostGlobalStatistics() throws Exception {
        Statistics globalStats = new Statistics();
        when(statisticsService.updateGlobalStatistics()).thenReturn(globalStats);

        mockMvc.perform(post(BASE_URL + "/global")
                .with(csrf()))
                .andExpect(status().isCreated());

        verify(statisticsService, times(1)).updateGlobalStatistics();
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testPutGlobalStatistics() throws Exception {
        Statistics globalStats = new Statistics();
        when(statisticsService.updateGlobalStatistics()).thenReturn(globalStats);

        mockMvc.perform(put(BASE_URL + "/global")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(statisticsService, times(1)).updateGlobalStatistics();
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testFindUserStatistics() throws Exception {
        Statistics userStats = new Statistics();
        when(statisticsService.getUserStatistics(anyInt())).thenReturn(userStats);

        mockMvc.perform(get(BASE_URL + "/user")
                .param("userId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(statisticsService, times(1)).getUserStatistics(1);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testFindUserStatisticsNotFound() throws Exception {
        when(statisticsService.getUserStatistics(anyInt())).thenReturn(null);

        mockMvc.perform(get(BASE_URL + "/user")
                .param("userId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(statisticsService, times(1)).getUserStatistics(1);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testUpdateUserStatistics() throws Exception {
        Statistics updatedStats = new Statistics();
        when(statisticsService.updateUserStatistics(anyInt())).thenReturn(updatedStats);

        mockMvc.perform(put(BASE_URL + "/user")
                .param("userId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(statisticsService, times(1)).updateUserStatistics(1);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void testCreateUserStatistics() throws Exception {
        Statistics createdStats = new Statistics();
        when(statisticsService.updateUserStatistics(anyInt())).thenReturn(createdStats);

        mockMvc.perform(post(BASE_URL + "/user")
                .param("userId", "1")
                .with(csrf()))
                .andExpect(status().isCreated());

        verify(statisticsService, times(1)).updateUserStatistics(1);
    }
}
