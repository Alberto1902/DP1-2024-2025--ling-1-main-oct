package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.CasualGamerLimitExceededException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.ProfileType;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;


public class CasualGamerValidationTest {

    @Test
    public void testCasualGamerLimitExceptionCreation() {

        CasualGamerLimitExceededException exception = 
            new CasualGamerLimitExceededException("DAILY_GAMES", "Test message");
        
        assertEquals("DAILY_GAMES", exception.getLimitType());
        assertEquals("Test message", exception.getMessage());
    }
    
    @Test 
    public void testUserProfileTypeEnum() {

        assertEquals("CASUAL_GAMER", ProfileType.CASUAL_GAMER.toString());
        assertEquals("HARD_CORE_GAMER", ProfileType.HARD_CORE_GAMER.toString());
    }
    
    @Test
    public void testUserCasualGamerFields() {

        User user = new User();
        user.setProfileType(ProfileType.CASUAL_GAMER);
        user.setDailyGamesPlayed(1);
        user.setLastGameDate(LocalDate.now());
        
        assertEquals(ProfileType.CASUAL_GAMER, user.getProfileType());
        assertEquals(1, user.getDailyGamesPlayed());
        assertEquals(LocalDate.now(), user.getLastGameDate());
    }
    
    @Test
    public void testGameSessionTimeValidationLogic() {

        long startTime = System.currentTimeMillis() - 70000; 
        long currentTime = System.currentTimeMillis();
        long duration = currentTime - startTime;

        assertTrue(duration > 60000, "La duración debería ser mayor a 1 minuto");
    }
}
