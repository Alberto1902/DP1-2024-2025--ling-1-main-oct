package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.CasualGamerLimitExceededException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.ProfileType;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@SpringBootTest
@Transactional
public class CasualGamerTest {

    @Autowired
    private GameSessionService gameSessionService;
    
    @Autowired 
    private UserService userService;
    
    private User casualGamer;
    private User hardCoreGamer;
    
    @BeforeEach
    void setUp() {
        casualGamer = userService.findUser("aaron");
        hardCoreGamer = userService.findUser("player1");
        
        assertNotNull(casualGamer, "Usuario casual gamer 'aaron' debe existir");
        assertNotNull(hardCoreGamer, "Usuario hardcore gamer 'player1' debe existir");
        
        assertEquals(ProfileType.CASUAL_GAMER, casualGamer.getProfileType());
        assertEquals(ProfileType.HARD_CORE_GAMER, hardCoreGamer.getProfileType());

        casualGamer.setDailyGamesPlayed(0);
        casualGamer.setLastGameDate(null);
        userService.saveUser(casualGamer);
    }
    
    @Test
    void testCasualGamerCanPlayFirstGame() {
        assertDoesNotThrow(() -> {
            gameSessionService.validateCasualGamerLimitations(casualGamer);
        });
    }
    
    @Test 
    void testCasualGamerCanPlaySecondGame() {
        casualGamer.setDailyGamesPlayed(1);
        casualGamer.setLastGameDate(LocalDate.now());
        userService.saveUser(casualGamer);

        assertDoesNotThrow(() -> {
            gameSessionService.validateCasualGamerLimitations(casualGamer);
        });
    }
    
    @Test
    void testCasualGamerCannotPlayThirdGame() {
        casualGamer.setDailyGamesPlayed(2);
        casualGamer.setLastGameDate(LocalDate.now());
        userService.saveUser(casualGamer);

        CasualGamerLimitExceededException exception = assertThrows(
            CasualGamerLimitExceededException.class,
            () -> gameSessionService.validateCasualGamerLimitations(casualGamer)
        );
        
        assertEquals("DAILY_GAMES", exception.getLimitType());
        assertTrue(exception.getMessage().contains("máximo de 2 partidas diarias"));
    }
    
    @Test
    void testHardCoreGamerHasNoLimits() {
        assertDoesNotThrow(() -> {
            gameSessionService.validateCasualGamerLimitations(hardCoreGamer);
        });

        hardCoreGamer.setDailyGamesPlayed(10);
        hardCoreGamer.setLastGameDate(LocalDate.now());
        userService.saveUser(hardCoreGamer);
        
        assertDoesNotThrow(() -> {
            gameSessionService.validateCasualGamerLimitations(hardCoreGamer);
        });
    }
    
    @Test
    void testCasualGamerCounterResetsNextDay() {
        casualGamer.setDailyGamesPlayed(2);
        casualGamer.setLastGameDate(LocalDate.now().minusDays(1));
        userService.saveUser(casualGamer);

        assertDoesNotThrow(() -> {
            gameSessionService.validateCasualGamerLimitations(casualGamer);
        });
    }
}
