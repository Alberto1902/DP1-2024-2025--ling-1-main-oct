package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import es.us.dp1.lx_xy_24_25.your_game_name.chat.ChatRoomService;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {GameSessionService.class, StatisticsService.class, UserService.class, ChatRoomService.class}))
public class StatisticsSocialServiceTest {

    @Autowired
    private StatisticsService statisticsService;
    @Autowired
    private GameSessionService gameSessionService;

    @Test
    public void shouldCreateStatisticsForUser(){

        Integer userId = 2;

        Statistics statistics = statisticsService.updateUserStatistics(userId);
        assertNotNull(statistics);
        assertEquals(userId, statistics.getUser().getId());
        assertEquals(75, statistics.getGamesPlayed());
        assertEquals(4, statistics.getVictories());
        assertEquals(71, statistics.getDefeats());
        assertEquals(26, statistics.getShortestGame());
        assertEquals(120, statistics.getLongestGame());
    }


    @Test
    public void shouldUpdateStatisticsForUser(){
        Integer userId = 2;

        GameSession gameSession = gameSessionService.findGameSession(10001);
        gameSession.setGameDuration(100);
        gameSession.setWinner(gameSession.getPlayers().get(0));
        gameSession.setStatus("FINISHED");
        gameSessionService.updateGameSession(gameSession, 10001);

        Statistics statistics = statisticsService.updateUserStatistics(userId);

        assertNotNull(statistics);
        assertEquals(userId, statistics.getUser().getId());
        assertEquals(76, statistics.getGamesPlayed());
        assertEquals(5, statistics.getVictories());
        assertEquals(71, statistics.getDefeats());

    }

    @Test
    public void shouldUpdateStatisticsForUserWithNewShortestGame(){
        Integer userId = 2;

        GameSession gameSession = gameSessionService.findGameSession(10001);
        gameSession.setGameDuration(10);
        gameSession.setWinner(gameSession.getPlayers().get(0));
        gameSession.setStatus("FINISHED");
        gameSessionService.updateGameSession(gameSession, 10001);

        Statistics statistics = statisticsService.updateUserStatistics(userId);

        assertNotNull(statistics);
        assertEquals(userId, statistics.getUser().getId());
        assertEquals(76, statistics.getGamesPlayed());
        assertEquals(5, statistics.getVictories());
        assertEquals(71, statistics.getDefeats());
        assertEquals(10, statistics.getShortestGame());
    }

    @Test
    public void shouldUpdateGlobalStatistics(){
        GameSession gameSession = gameSessionService.findGameSession(10001);
        gameSession.setGameDuration(10);
        gameSession.setWinner(gameSession.getPlayers().get(0));
        gameSession.setStatus("FINISHED");
        gameSessionService.updateGameSession(gameSession, 10001);

        Statistics statistics = statisticsService.updateGlobalStatistics();

        assertNotNull(statistics);
        assertEquals(885, statistics.getGamesPlayed());
        assertEquals(null, statistics.getVictories());
        assertEquals(null, statistics.getDefeats());
        assertEquals(10, statistics.getShortestGame());
        assertEquals(120, statistics.getLongestGame());
    }
    
}
