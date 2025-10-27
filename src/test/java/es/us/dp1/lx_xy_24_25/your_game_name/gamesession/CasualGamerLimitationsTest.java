package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.CasualGamerLimitExceededException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.ProfileType;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.chat.ChatRoomService;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class CasualGamerLimitationsTest {

    @Mock
    private GameSessionRepository gameSessionRepository;
    
    @Mock
    private UserService userService;
    
    @Mock
    private ChatRoomService chatRoomService;

    @InjectMocks
    private GameSessionService gameSessionService;

    private User casualGamer;
    private User hardCoreGamer;
    private GameSession gameSession;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        
        casualGamer = new User();
        casualGamer.setId(1);
        casualGamer.setUsername("casualplayer");
        casualGamer.setProfileType(ProfileType.CASUAL_GAMER);
        casualGamer.setDailyGamesPlayed(0);
        casualGamer.setLastGameDate(null);

        hardCoreGamer = new User();
        hardCoreGamer.setId(2);
        hardCoreGamer.setUsername("hardcore");
        hardCoreGamer.setProfileType(ProfileType.HARD_CORE_GAMER);

        gameSession = new GameSession();
        gameSession.setName("Test Game");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        gameSession.setCreator(casualGamer);
    }

    @Test
    void shouldAllowCasualGamerToCreateFirstGame() {

        when(userService.saveUser(any(User.class))).thenReturn(casualGamer);
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(gameSession);

        assertDoesNotThrow(() -> gameSessionService.createGameSession(gameSession));
        assertEquals(1, casualGamer.getDailyGamesPlayed());
    }

    @Test
    void shouldAllowCasualGamerToCreateSecondGame() {

        casualGamer.setDailyGamesPlayed(1);
        casualGamer.setLastGameDate(LocalDate.now());
        when(userService.saveUser(any(User.class))).thenReturn(casualGamer);
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(gameSession);

        assertDoesNotThrow(() -> gameSessionService.createGameSession(gameSession));
        assertEquals(2, casualGamer.getDailyGamesPlayed());
    }

    @Test
    void shouldPreventCasualGamerFromCreatingThirdGame() {

        casualGamer.setDailyGamesPlayed(2);
        casualGamer.setLastGameDate(LocalDate.now());

        CasualGamerLimitExceededException exception = assertThrows(
            CasualGamerLimitExceededException.class,
            () -> gameSessionService.createGameSession(gameSession)
        );
        
    assertEquals("DAILY_GAMES", exception.getLimitType());
    assertTrue(exception.getMessage().contains("máximo de 2 partidas diarias"));
    }

    @Test
    void shouldResetDailyCountOnNewDay() {

        casualGamer.setDailyGamesPlayed(2);
        casualGamer.setLastGameDate(LocalDate.now().minusDays(1));
        when(userService.saveUser(any(User.class))).thenReturn(casualGamer);
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(gameSession);

        assertDoesNotThrow(() -> gameSessionService.createGameSession(gameSession));

        assertEquals(1, casualGamer.getDailyGamesPlayed());
        assertEquals(LocalDate.now(), casualGamer.getLastGameDate());
    }

    @Test
    void shouldAllowHardCoreGamerToCreateUnlimitedGames() {
        gameSession.setCreator(hardCoreGamer);
        when(gameSessionRepository.save(any(GameSession.class))).thenReturn(gameSession);

        assertDoesNotThrow(() -> gameSessionService.createGameSession(gameSession));
        assertDoesNotThrow(() -> gameSessionService.createGameSession(gameSession));
        assertDoesNotThrow(() -> gameSessionService.createGameSession(gameSession));
    }

    @Test
    void shouldDetectTimeExceededForCasualGamer() {

        GameSession inProgressGame = new GameSession();
        inProgressGame.setStatus("IN_PROGRESS");
        inProgressGame.setStart(LocalDateTime.now().minusMinutes(35));
        
        boolean hasExceeded = gameSessionService.hasExceededMaxGameTime(inProgressGame, casualGamer);
        
        assertTrue(hasExceeded);
    }

    @Test
    void shouldNotDetectTimeExceededForHardCoreGamer() {
        GameSession inProgressGame = new GameSession();
        inProgressGame.setStatus("IN_PROGRESS");
        inProgressGame.setStart(LocalDateTime.now().minusMinutes(35));

        boolean hasExceeded = gameSessionService.hasExceededMaxGameTime(inProgressGame, hardCoreGamer);

        assertFalse(hasExceeded);
    }

    @Test
    void shouldNotDetectTimeExceededWithinLimit() {

        GameSession inProgressGame = new GameSession();
        inProgressGame.setStatus("IN_PROGRESS");
        inProgressGame.setStart(LocalDateTime.now().minusMinutes(25));
        
        boolean hasExceeded = gameSessionService.hasExceededMaxGameTime(inProgressGame, casualGamer);

        assertFalse(hasExceeded);
    }
}
