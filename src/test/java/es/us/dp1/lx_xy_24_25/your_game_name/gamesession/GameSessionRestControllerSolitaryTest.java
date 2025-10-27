package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.never;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import java.util.Arrays;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import org.springframework.security.test.context.support.WithMockUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

@WebMvcTest(value = {GameSessionRestController.class}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class))
public class GameSessionRestControllerSolitaryTest {
    @Autowired
    MockMvc mvc;
    private final String BASE_URL = "/api/v1/gamesessions";

    @MockBean
    GameSessionService gameSessionService;

    @MockBean
    UserService userService;

    @MockBean
    PieceService pieceService;

    @Test
    @WithMockUser(username ="player", authorities = "PLAYER")
    public void unfeasibleGameSessionCreationTest() throws JsonProcessingException, Exception {
        GameSession g = new GameSession();
        g.setMaxPlayers(0);
        ObjectMapper objectMapper = new ObjectMapper();
        reset(gameSessionService);

        mvc.perform(post(BASE_URL).with(csrf()).contentType("application/json").content(objectMapper.writeValueAsString(g)))
            .andExpect(status().isBadRequest());

            verify(gameSessionService, never()).createGameSession((any(GameSession.class)));
    }

    @Test
    @WithMockUser(username ="player", authorities = "PLAYER")
    public void feasibleGameSessionCreationTest() throws JsonProcessingException, Exception {
        GameSession g = createValidGameSession();
        ObjectMapper objectMapper = new ObjectMapper();
        reset(gameSessionService);

        mvc.perform(post(BASE_URL).with(csrf()).contentType("application/json").content(objectMapper.writeValueAsString(g)))
            .andExpect(status().isCreated());

            verify(gameSessionService).createGameSession((any(GameSession.class)));
    }
    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void findGameSessionsWithParametersTest() throws Exception {
        GameSession game1 = new GameSession();
        game1.setId(1);
        game1.setName("Game 1");
        GameSession game2 = new GameSession();
        game2.setId(2);
        game2.setName("Game 2");
        List<GameSession> games = Arrays.asList(game1, game2);

        reset(gameSessionService);
        when(gameSessionService.findGameSessionsByCreatorAndStatus("active", 1)).thenReturn(games);
    
        mvc.perform(get(BASE_URL)
                .param("creatorId", "1")
                .param("status", "active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Game 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Game 2"));
    
        verify(gameSessionService).findGameSessionsByCreatorAndStatus("active", 1);

        reset(gameSessionService);
        when(gameSessionService.findGameSessionsByCreator(1)).thenReturn(games);
    
        mvc.perform(get(BASE_URL)
                .param("creatorId", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Game 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Game 2"));
    
        verify(gameSessionService).findGameSessionsByCreator(1);

        reset(gameSessionService);
        when(gameSessionService.findGameSessionsByStatus("active")).thenReturn(games);
    
        mvc.perform(get(BASE_URL)
                .param("status", "active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Game 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Game 2"));
    
        verify(gameSessionService).findGameSessionsByStatus("active");
    
        reset(gameSessionService);
        when(gameSessionService.findGameSessions()).thenReturn(games);
    
        mvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Game 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Game 2"));
    
        verify(gameSessionService).findGameSessions();
    }
    

    @Test
    @WithMockUser(username ="player", authorities = "PLAYER")
    public void findGameSessionsTest() throws Exception {
        GameSession game1 = new GameSession();
        game1.setId(1);
        game1.setName("Game 1");
        GameSession game2 = new GameSession();
        game2.setId(2);
        game2.setName("Game 2");
        List<GameSession> games = Arrays.asList(game1, game2);

        reset(gameSessionService);
        when(gameSessionService.findGameSessions()).thenReturn(games);

        mvc.perform(get(BASE_URL))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[0].name").value("Game 1"))
            .andExpect(jsonPath("$[1].id").value(2))
            .andExpect(jsonPath("$[1].name").value("Game 2"));

        verify(gameSessionService).findGameSessions();
    }

    @Test
    @WithMockUser(username ="player", authorities = "PLAYER")
    public void joinGameSessionTest() throws Exception {
        GameSession gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setName("Game 1");
        User user = new User();
        user.setId(1);

        reset(gameSessionService, userService);
        when(userService.findUser(1)).thenReturn(user);
        when(gameSessionService.joinGameSession(eq(1), eq(user), any())).thenReturn(gameSession);

        mvc.perform(put(BASE_URL + "/game/join").param("id", "1").param("userId", "1").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Game 1"));

        verify(userService).findUser(1);
        verify(gameSessionService).joinGameSession(1, user, null);
    }

    @Test
    @WithMockUser(username ="player", authorities = "PLAYER")
    public void changeGameSessionStatusTest() throws Exception {
        GameSession gameSession = new GameSession();
        gameSession.setId(1);
        gameSession.setName("Game 1");

        reset(gameSessionService);
        when(gameSessionService.findGameSession(1)).thenReturn(gameSession);
        when(gameSessionService.changeStatus(gameSession)).thenReturn(gameSession);

        mvc.perform(put(BASE_URL + "/game/changeStatus").param("id", "1").with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1))
            .andExpect(jsonPath("$.name").value("Game 1"));

        verify(gameSessionService).findGameSession(1);
        verify(gameSessionService).changeStatus(gameSession);
    }
    private GameSession createValidGameSession() {
        GameSession g = new GameSession();
        g.setCurrentPlayers(1);
        g.setName("Valid game");
        g.setMaxPlayers(3);
        return g;
    }
}
