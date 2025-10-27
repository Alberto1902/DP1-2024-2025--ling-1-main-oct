package es.us.dp1.lx_xy_24_25.your_game_name.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.lx_xy_24_25.your_game_name.configuration.jwt.JwtUtils;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.GenreRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.PlatformRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.profile.SagaRepository;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Achievement;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.AchievementService;
import es.us.dp1.lx_xy_24_25.your_game_name.statistics.Metric;

@WebMvcTest(UserRestController.class)
class UserControllerTests {

    private static final Integer TEST_USER_ID = 100;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private AchievementService achievementService;

    @MockBean
    private AuthoritiesService authService;

    @MockBean
    private GameSessionService gameSessionService;

    @MockBean
    private JwtUtils jwtUtils;

     @MockBean
    private GenreRepository genreRepository;

    @MockBean
    private PlatformRepository platformRepository;

    @MockBean
    private SagaRepository sagaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private User testUser;
    private Authorities testAuthority;

    private User user;
    private Authorities authority;
    private Achievement testAchievement;

    @BeforeEach
    void setup() {
        testAuthority = new Authorities();
        testAuthority.setId(1);
        testAuthority.setAuthority("PLAYER");

        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setUsername("testUser");
        testUser.setFirstName("testFirstName");
        testUser.setLastName("testLastName");
        testUser.setPassword("password");
        testUser.setAuthority(testAuthority);
    testUser.setLocation("testLocation");

        authority = new Authorities();
        authority.setId(2);
        authority.setAuthority("ADMIN");

        user = new User();
        user.setId(101);
        user.setUsername("adminUser");
        user.setPassword("adminPassword");
        user.setAuthority(authority);
    user.setLocation("adminLocation");

        testAchievement = new Achievement();
        testAchievement.setId(1);
        testAchievement.setName("Veteran Player");
        testAchievement.setDescription("Play <THRESHOLD> games.");
        testAchievement.setThreshold(10);
        testAchievement.setMetric(Metric.GAMES_PLAYED);
    }

    @Test
    @WithMockUser(username = "adminUser")
    void testFindAllUsers() throws Exception {
        when(userService.findAll()).thenReturn(Collections.singletonList(testUser));

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "adminUser")
    void testFindByUsername() throws Exception {
        when(userService.findUser("testUser")).thenReturn(testUser);

        mockMvc.perform(get("/api/v1/users/testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "adminUser")
    void testCreateUser() throws Exception {
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(post("/api/v1/users").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testUpdateUser() throws Exception {
        when(userService.findUser(1)).thenReturn(testUser);
        when(userService.updateUser(any(User.class), eq(1))).thenReturn(testUser);

        mockMvc.perform(put("/api/v1/users/1").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "adminUser")
    void testDeleteUser() throws Exception {
        when(userService.findCurrentUser()).thenReturn(testUser);
        when(userService.findUser(1)).thenReturn(testUser);
        doNothing().when(userService).deleteUser(1);

        mockMvc.perform(delete("/api/v1/users/1").with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testMakeOnline() throws Exception {
        when(userService.makeOnline("testUser")).thenReturn(testUser);

        mockMvc.perform(put("/api/v1/users/makeOnline").with(csrf())

                .param("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testMakeOffline() throws Exception {
        when(userService.makeOffline("testUser")).thenReturn(testUser);

        mockMvc.perform(put("/api/v1/users/makeOffline").with(csrf())
                .param("username", "testUser"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testClaimAchievementFailureThresholdNotMet() throws Exception {
        when(userService.findCurrentUser()).thenReturn(testUser);
        when(gameSessionService.findTotalGamesByUser(100)).thenReturn(5);
        when(achievementService.getById(1)).thenReturn(testAchievement);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAchievement)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testClaimAchievementSuccess() throws Exception {
        when(userService.findCurrentUser()).thenReturn(testUser);
        when(gameSessionService.findTotalGamesByUser(100)).thenReturn(15);
        when(achievementService.getById(1)).thenReturn(testAchievement);
        when(userService.saveUser(any(User.class))).thenReturn(testUser);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testAchievement)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testUser"));

        verify(userService).saveUser(any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldClaimAchievementForVictories() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setObtainedAchievements(new ArrayList<>());

        Achievement achievement = new Achievement();
        achievement.setId(1);
        achievement.setMetric(Metric.VICTORIES);
        achievement.setThreshold(5);
        achievement.setDescription("Sample");

        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(gameSessionService.findTotalWinsByUser(1)).thenReturn(10);
        when(achievementService.getById(1)).thenReturn(achievement);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
                .andExpect(status().isOk());

        verify(userService).saveUser(Mockito.any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldNotClaimAchievementForVictoriesWhenThresholdNotMet() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setObtainedAchievements(new ArrayList<>());

        Achievement achievement = new Achievement();
        achievement.setId(1);
        achievement.setMetric(Metric.VICTORIES);
        achievement.setThreshold(5);        achievement.setDescription("Sample");


        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(gameSessionService.findTotalWinsByUser(1)).thenReturn(3);
        when(achievementService.getById(1)).thenReturn(achievement);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(Mockito.any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldClaimAchievementForDefeats() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setObtainedAchievements(new ArrayList<>());

        Achievement achievement = new Achievement();
        achievement.setId(2);
        achievement.setMetric(Metric.DEFEATS);
        achievement.setThreshold(3);        achievement.setDescription("Sample");


        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(gameSessionService.findTotalGamesByUser(1)).thenReturn(10);
        when(gameSessionService.findTotalWinsByUser(1)).thenReturn(5);
        when(achievementService.getById(2)).thenReturn(achievement);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
                .andExpect(status().isOk());

        verify(userService).saveUser(Mockito.any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldClaimAchievementForTimePlayed() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setObtainedAchievements(new ArrayList<>());

        Achievement achievement = new Achievement();
        achievement.setId(3);
        achievement.setMetric(Metric.TIME_PLAYED);
        achievement.setThreshold(120);        achievement.setDescription("Sample");


        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(gameSessionService.findMinutesPlayedByUser(1)).thenReturn(150);
        when(achievementService.getById(3)).thenReturn(achievement);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
                .andExpect(status().isOk());

        verify(userService).saveUser(Mockito.any(User.class));
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldNotClaimAchievementForTimePlayedWhenThresholdNotMet() throws Exception {
        User currentUser = new User();
        currentUser.setId(1);
        currentUser.setObtainedAchievements(new ArrayList<>());

        Achievement achievement = new Achievement();
        achievement.setId(3);
        achievement.setMetric(Metric.TIME_PLAYED);
        achievement.setThreshold(200);        achievement.setDescription("Sample");


        when(userService.findCurrentUser()).thenReturn(currentUser);
        when(gameSessionService.findMinutesPlayedByUser(1)).thenReturn(150);
        when(achievementService.getById(3)).thenReturn(achievement);

        mockMvc.perform(put("/api/v1/users/claimAchievement").with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(achievement)))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveUser(Mockito.any(User.class));
    }


    @Test
    @WithMockUser(username = "testUser")
    void shouldCountPlayers() throws Exception {
        when(userService.countPlayers()).thenReturn(100);

        mockMvc.perform(get("/api/v1/users/countPlayers")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "testUser")
    void shouldCountOnlinePlayers() throws Exception {
        when(userService.countOnlinePlayers()).thenReturn(25);

        mockMvc.perform(get("/api/v1/users/countOnlinePlayers")
                .with(csrf()))
                .andExpect(status().isOk());
    }

}
