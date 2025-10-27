package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.chat.ChatRoomService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;



@SpringBootTest
@Transactional
class GameSessionServiceTests {

    @Autowired
    private GameSessionService gameSessionService;
    @Test
    void shouldFindAllGameSessions() {
        List<GameSession> gameSessions = gameSessionService.findGameSessions();
        assertNotNull(gameSessions);
        assertNotEquals(0, gameSessions.size());
    }

    @Test
    void shouldFindGameSessionById() {
        GameSession gameSession = gameSessionService.findGameSession(10000);
        assertNotNull(gameSession);
        assertEquals(10000, gameSession.getId());
    }

    @Test
    void shouldThrowExceptionWhenGameSessionNotFoundById() {
        assertThrows(ResourceNotFoundException.class, () -> gameSessionService.findGameSession(999));
    }

    @Test
    @Transactional
    void shouldCreateGameSession() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Test Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);

        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        GameSession savedGameSession = gameSessionService.createGameSession(gameSession);
        assertNotNull(savedGameSession.getId());
        assertEquals("Test Game Session", savedGameSession.getName());
    }

    @Test
    @Transactional
    void shouldUpdateGameSession() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Original Game Session");
        gameSession.setMaxPlayers(4);
        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        GameSession createdGameSession = gameSessionService.createGameSession(gameSession);
        GameSession updateData = new GameSession();
        updateData.setName("Updated Game Session");
        updateData.setMaxPlayers(6);

        GameSession updatedGameSession = gameSessionService.updateGameSession(updateData, createdGameSession.getId());

        assertNotNull(updatedGameSession);
        assertEquals("Updated Game Session", updatedGameSession.getName());
        assertEquals(6, updatedGameSession.getMaxPlayers());
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonexistentGameSession() {
        GameSession updateData = new GameSession();
        updateData.setName("Nonexistent Game");
        assertThrows(ResourceNotFoundException.class, () -> gameSessionService.updateGameSession(updateData, 999));
    }

    @Test
    void shouldFindGameSessionsByStatus() {
        List<GameSession> activeSessions = gameSessionService.findGameSessionsByStatus("IN_PROGRESS");
        assertNotNull(activeSessions);
        for (GameSession gameSession : activeSessions) {
            assertEquals("IN_PROGRESS", gameSession.getStatus());
        }
    }


    @Test
    void shouldFindGameSessionsByCreator() {
        List<GameSession> createdSessions = gameSessionService.findGameSessionsByCreator(1);
        assertNotNull(createdSessions);
        for (GameSession gameSession : createdSessions) {
            assertEquals(1, gameSession.getCreator().getId());
        }
    }

    @Test
    void shouldFindGameSessionsByCreatorAndStatus() {
        List<GameSession> filteredSessions = gameSessionService.findGameSessionsByCreatorAndStatus("IN_PROGRESS", 1);
        assertNotNull(filteredSessions);
        for (GameSession gameSession : filteredSessions) {
            assertEquals("IN_PROGRESS", gameSession.getStatus());
            assertEquals(1, gameSession.getCreator().getId());
        }
    }
    @Test
    @Transactional
    void shouldFailToCreatePrivateGameSessionWithoutPin() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Private Game Without PIN");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(true);

        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        assertThrows(IllegalArgumentException.class, () -> gameSessionService.createGameSession(gameSession));
    }

    @Test
    @Transactional
    void shouldFailToCreatePublicGameSessionWithPin() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Public Game With PIN");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        gameSession.setPin("1234");

        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        assertThrows(IllegalArgumentException.class, () -> gameSessionService.createGameSession(gameSession));
    }

    @Test
    @Transactional
    void shouldCreatePrivateGameSessionWithPin() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Private Game With PIN");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(true);
        gameSession.setPin("5678");

        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        GameSession savedGameSession = gameSessionService.createGameSession(gameSession);
        assertNotNull(savedGameSession.getId());
        assertEquals("Private Game With PIN", savedGameSession.getName());
        assertEquals("5678", savedGameSession.getPin());
    }

    @Test
    @Transactional
    void shouldCreatePublicGameSessionWithoutPin() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Public Game Without PIN");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        gameSession.setPin(null);

        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        GameSession savedGameSession = gameSessionService.createGameSession(gameSession);
        assertNotNull(savedGameSession.getId());
        assertEquals("Public Game Without PIN", savedGameSession.getName());
        assertEquals(null, savedGameSession.getPin());
    }

    @Test
@Transactional
void shouldFailToCreateGameSessionWithLongPin() {
    GameSession gameSession = new GameSession();
    gameSession.setName("Game With Long PIN");
    gameSession.setMaxPlayers(4);
    gameSession.setIsPrivate(true);
    gameSession.setPin("12345");

    User creator = new User();
    creator.setId(1);
    gameSession.setCreator(creator);

    assertThrows(IllegalArgumentException.class, () -> gameSessionService.createGameSession(gameSession));
}

    @Test
    @Transactional
    void shouldFailToCreateGameSessionWithShortPin() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Game With Short PIN");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(true);
        gameSession.setPin("123");

        User creator = new User();
        creator.setId(1);
        gameSession.setCreator(creator);

        assertThrows(IllegalArgumentException.class, () -> gameSessionService.createGameSession(gameSession));
    }

    @Test
    @Transactional
    void shouldFailToJoinGameSessionWithInvalidPin(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game With PIN");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(true);
        gameSession.setPin("1234");
        User creator = new User();
        creator.setId(7);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(7).get(0);

        User joinuser = new User();
        joinuser.setId(2);
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.joinGameSession(newGameSession.getId(), joinuser, "4321"));
    }

    @Test
    @Transactional
    void shouldFailToJoinFullGameSession(){
        GameSession gameSession = createFullGameSession();
        User joiUser = new User();
        joiUser.setId(4);
        joiUser.setUsername("user4");
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.joinGameSession(gameSession.getId(), joiUser, null));

    }

    @Test
    @Transactional
    void shouldFailToJoinFinishedGameSession(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Finished Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);
        newGameSession.setStatus("FINISHED");

        User joinuser = new User();
        joinuser.setId(2);
        joinuser.setUsername("user2");
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.joinGameSession(newGameSession.getId(), joinuser, null));
    }

    @Test
    @Transactional
    void shouldFailToJoinInProgressGameSession(){
        GameSession gameSession = new GameSession();
        gameSession.setName("In Progress Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);
        newGameSession.setStatus("IN_PROGRESS");

        User joinuser = new User();
        joinuser.setId(2);
        joinuser.setUsername("user2");
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.joinGameSession(newGameSession.getId(), joinuser, null));
    }

    @Test
    @Transactional
    void shouldFailToJoinPublicGameWithCode(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Public Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);

        User joinuser = new User();
        joinuser.setId(2);
        joinuser.setUsername("user2");
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.joinGameSession(newGameSession.getId(), joinuser, "1234"));
    }

    @Test
    @Transactional	
    void shouldFailToJoinAgainGameSession(){
        GameSession gameSession = createGameSessionWithPlayers();
        User joinuser = gameSession.getPlayers().get(1);
        assertThrows(IllegalArgumentException.class, () -> gameSessionService.joinGameSession(gameSession.getId(), joinuser, null));
    }

    @Test
    @Transactional
    void shouldJoinGameSession(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);

        User joinuser = new User();
        joinuser.setId(2);
        joinuser.setUsername("user2");
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser, null);

        GameSession updatedGameSession = gameSessionService.findGameSession(newGameSession.getId());
        assertEquals(2, updatedGameSession.getPlayers().size());
    }

    @Test
    @Transactional
    void shouldChangeGameSessionStatusToInProgress(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);

        gameSessionService.changeStatus(newGameSession);

        GameSession updatedGameSession = gameSessionService.findGameSession(newGameSession.getId());
        assertEquals("IN_PROGRESS", updatedGameSession.getStatus());
        assertNotNull(updatedGameSession.getStart());
    }

    @Test
    @Transactional
    void shouldChangeGameSessionStatusToFinished(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);
        gameSessionService.changeStatus(newGameSession);
        gameSessionService.changeStatus(newGameSession);
        GameSession updatedGameSession = gameSessionService.findGameSession(newGameSession.getId());
        assertEquals("FINISHED", updatedGameSession.getStatus());
        assertNotNull(updatedGameSession.getEnd());


    }

    @Test
    @Transactional
    void shouldFailToChangeStatus(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);
        gameSessionService.changeStatus(newGameSession);
        gameSessionService.changeStatus(newGameSession);
        assertThrows(IllegalArgumentException.class,() -> gameSessionService.changeStatus(newGameSession));

    }

    @Test
    @Transactional
    void shouldFailToChangeTurnInAWaitingGame(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);
        assertThrows(IllegalArgumentException.class,() -> gameSessionService.nextTurn(newGameSession));
    }

    @Test
    @Transactional
    void shouldFailToChangeTurnInAFinishedGame(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(5);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(5).get(0);
        gameSessionService.changeStatus(newGameSession);
        gameSessionService.changeStatus(newGameSession);
        assertThrows(IllegalArgumentException.class,() -> gameSessionService.nextTurn(newGameSession));
    }

    @Test
    @Transactional
    void shouldChangeTurn(){
        GameSession gameSession = createFullGameSession4Players();
        gameSessionService.changeStatus(gameSession);
        gameSessionService.nextTurn(gameSession);
        GameSession updatedGameSession = gameSessionService.findGameSession(gameSession.getId());
        assertEquals(1, updatedGameSession.getTurn());
        gameSessionService.nextTurn(gameSession);
        updatedGameSession = gameSessionService.findGameSession(gameSession.getId());
        assertEquals(2, updatedGameSession.getTurn());
        gameSessionService.nextTurn(gameSession);
        updatedGameSession = gameSessionService.findGameSession(gameSession.getId());
        assertEquals(3, updatedGameSession.getTurn());
        gameSessionService.nextTurn(gameSession);
        updatedGameSession = gameSessionService.findGameSession(gameSession.getId());
        assertEquals(0, updatedGameSession.getTurn());
    }
    
    @Test
    void shouldCorrectlyRetrieveShortestGameByUser(){
        Integer shortestGame = gameSessionService.findShortestGameByUser(2);
        assertNotEquals(0, shortestGame);
        assertEquals(26, shortestGame);

    }

    @Test
    void shouldNotRetrieveShortestGameByUser(){
        Integer shortestGame = gameSessionService.findShortestGameByUser(1);
        assertEquals(0, shortestGame);
    }

    @Test
    void shouldCorrectlyRetrieveLongestGameByUser(){
        Integer longestGame = gameSessionService.findLongestGameByUser(2);
        assertNotEquals(0, longestGame);
        assertEquals(120, longestGame);
    }

    @Test
    void shouldNotRetrieveLongestGameByUser(){
        Integer longestGame = gameSessionService.findLongestGameByUser(1);
        assertEquals(0, longestGame);
    }
    
    @Test
    void shouldCorrectlyRetrieveAverageGameByUser(){
        Double averageGame = gameSessionService.findAverageGameByUser(2);
        Long averageGame2 = Math.round(averageGame);
        assertNotEquals(0, averageGame2);
        assertEquals(69, averageGame2);
    }

    @Test
    void shouldNotRetrieveAverageGameByUser(){
        Double averageGame = gameSessionService.findAverageGameByUser(1);
        assertEquals(0, averageGame);
    }

    @Test
    void shouldCorrectlyRetrieveSmallestGameByUser(){
        Integer smallestGame = gameSessionService.findSmallestGameByUser(2);
        assertNotEquals(0, smallestGame);
        assertEquals(2, smallestGame);
    }

    @Test
    void shouldNotRetrieveSmallestGameByUser(){
        Integer smallestGame = gameSessionService.findSmallestGameByUser(1);
        assertEquals(0, smallestGame);
    }

    @Test
    void shouldCorrectlyRetrieveBiggestGameByUser(){
        Integer biggestGame = gameSessionService.findBiggestGameByUser(2);
        assertNotEquals(0, biggestGame);
        assertEquals(6, biggestGame);
    }

    @Test
    void shouldNotRetrieveBiggestGameByUser(){
        Integer biggestGame = gameSessionService.findBiggestGameByUser(1);
        assertEquals(0, biggestGame);
    }

    @Test
    void shouldCorrectlyRetrieveAveragePlayersByUser(){
        Double averagePlayers = gameSessionService.findAveragePlayersByUser(2);
        Long averagePlayers2 = Math.round(averagePlayers);
        assertNotEquals(0, averagePlayers2);
        assertEquals(5, averagePlayers2);
    }

    @Test 
    void shouldNotRetrieveAveragePlayersByUser(){
        Double averagePlayers = gameSessionService.findAveragePlayersByUser(1);
        assertEquals(0, averagePlayers);
    }

    @Test
    void shouldRetrieveTotalGamesByUser(){
        Integer totalGames = gameSessionService.findTotalGamesByUser(2);
        assertNotEquals(0, totalGames);
        assertEquals(75, totalGames);
    }

    @Test
    void shouldNotRetrieveTotalGamesByUser(){
        Integer totalGames = gameSessionService.findTotalGamesByUser(1);
        assertEquals(0, totalGames);
    }

    @Test
    void shouldRetrieveTotalWinsByUser(){
        Integer totalWins = gameSessionService.findTotalWinsByUser(2);
        assertNotEquals(0, totalWins);
        assertEquals(4, totalWins);
    }

    @Test
    void shouldNotRetrieveTotalWinsByUser(){
        Integer totalWins = gameSessionService.findTotalWinsByUser(1);
        assertEquals(0, totalWins);
    }

    @Test
    void shouldRetrieveShortestGame(){
        Integer shortestGame = gameSessionService.findShortestGame();
        assertNotEquals(0, shortestGame);
        assertEquals(20, shortestGame);
    }

    @Test
    void shouldRetrieveLongestGame(){
        Integer longestGame = gameSessionService.findLongestGame();
        assertNotEquals(0, longestGame);
        assertEquals(120, longestGame);
    }

    @Test
    void shouldRetrieveAverageGame(){
        Double averageGame = gameSessionService.findAverageGame();
        Long averageGame2 = Math.round(averageGame);
        assertNotEquals(0, averageGame2);
        assertEquals(68, averageGame2);
    }

    @Test
    void shouldRetrieveAverageGameSize(){
        Double averageGameSize = gameSessionService.findAverageGameSize();
        Long averageGameSize2 = Math.round(averageGameSize);
        assertNotEquals(0, averageGameSize2);
        assertEquals(4, averageGameSize2);
    }

    @Test
    void shouldRetrieveMinutesPlayed(){
        Integer minutesPlayed = gameSessionService.findMinutesPlayed();
        assertNotEquals(0, minutesPlayed);
        assertEquals(60453, minutesPlayed);
    }

    @Test
    void shouldRetrieveTotalGames(){
        Integer totalGames = gameSessionService.findTotalFinishedGames();
        assertNotEquals(0, totalGames);
        assertEquals(884, totalGames);
    }

    @Test
    void shouldRetrieveMinutesPlayedByUser(){
        Integer minutesPlayed = gameSessionService.findMinutesPlayedByUser(2);
        assertNotEquals(0, minutesPlayed);
        assertEquals(5200, minutesPlayed);
    }

    @Test
    void shouldFindTotalActiveGames(){
        Integer totalActiveGames = gameSessionService.findTotalActiveGames();
        assertNotEquals(0, totalActiveGames);
        assertEquals(3, totalActiveGames);
    }



    private GameSession createFullGameSession() {
        GameSession gameSession = new GameSession();
        gameSession.setName("Full Game Session");
        gameSession.setMaxPlayers(3);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(6);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(6).get(0);

        User joinuser = new User();
        joinuser.setId(2);
        joinuser.setUsername("user2");
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser, null);

        User joinuser2 = new User();
        joinuser2.setId(3);
        joinuser2.setUsername("user3");
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser2, null);

        return newGameSession;
    }

    private GameSession createGameSessionWithPlayers(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Full Game Session");
        gameSession.setMaxPlayers(3);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setUsername("creator");
        creator.setId(6);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(6).get(0);

        User joinuser = new User();
        joinuser.setId(2);
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser, null);

        return newGameSession;


    }

    private GameSession createFullGameSession4Players(){
        GameSession gameSession = new GameSession();
        gameSession.setName("Full Game Session");
        gameSession.setMaxPlayers(4);
        gameSession.setIsPrivate(false);
        User creator = new User();
        creator.setId(6);
        gameSession.setCreator(creator);
        gameSessionService.createGameSession(gameSession);

        GameSession newGameSession = gameSessionService.findGameSessionsByCreator(6).get(0);

        User joinuser = new User();
        joinuser.setId(2);
        joinuser.setUsername("user2");
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser, null);

        User joinuser2 = new User();
        joinuser2.setId(3);
        joinuser2.setUsername("user3");
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser2, null);
        User joinuser3 = new User();
        joinuser2.setId(4);
        joinuser2.setUsername("user4");
        gameSessionService.joinGameSession(newGameSession.getId(), joinuser3, null);
        
        return newGameSession;

    }
}
