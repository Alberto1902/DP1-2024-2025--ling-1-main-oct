package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.chat.ChatRoom;
import es.us.dp1.lx_xy_24_25.your_game_name.chat.ChatRoomService;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.CasualGamerLimitExceededException;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.ProfileType;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;

@Service
public class GameSessionService {

    private GameSessionRepository repo;
    private ChatRoomService chatRoomService;
    private UserService userService;

    @Autowired
    public GameSessionService(GameSessionRepository repo, ChatRoomService chatRoomService, UserService userService) {
        this.repo = repo;
        this.chatRoomService = chatRoomService;
        this.userService = userService;
    }

    public List<GameSession> findActiveGameSessionsByUser(User user) {
        return repo.findActiveSessionsByUser(user.getId(), "IN_PROGRESS");
    }
    

    private GameSession gameCreation(GameSession game) {
        GameSession newGame = new GameSession();
        newGame.setMaxPlayers(game.getMaxPlayers());
        newGame.setIsPrivate(game.getIsPrivate());
        newGame.setPin(game.getPin());
        newGame.setCreator(game.getCreator());
        newGame.getPlayers().add(game.getCreator());
        newGame.setCurrentPlayers(newGame.getPlayers().size());
        newGame.setName(game.getName());
        newGame.setStart(game.getStart());
        newGame.setEnd(game.getEnd());
        ChatRoom chat = chatRoomService.createChatRoom();
        newGame.setChatRoom(chat);
        return newGame;
    }

    @Transactional(rollbackFor = Exception.class)
    public GameSession createGameSession(GameSession game) throws DataAccessException, IllegalArgumentException {

        if(!game.getIsPrivate() && game.getPin() != null) {
            throw new IllegalArgumentException("Public games must not have a pin");
        }
        else if(game.getIsPrivate() && game.getPin() == null) {
            throw new IllegalArgumentException("Private games must have a pin");
        }
         else if(game.getIsPrivate() && game.getPin().length() != 4) {
            throw new IllegalArgumentException("Pin must have 4 digits");
        }
        
        validateCasualGamerLimitations(game.getCreator());
        
        GameSession newGame = gameCreation(game);
        repo.save(newGame);
        
        updateDailyGamesCount(game.getCreator());
        
        return newGame;
    }

    @Transactional
    public GameSession updateGameSession(@Valid GameSession game, Integer id) {
        GameSession updatedGame = findGameSession(id);
        BeanUtils.copyProperties(game, updatedGame, "id");
        repo.save(updatedGame);
        return updatedGame;
    }

    @Transactional(readOnly = true)
    public GameSession findGameSession(Integer id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", id));
    }

    @Transactional(readOnly = true)
    public GameSession findGameSessionIfAllowed(Integer id, User user) {
        GameSession game = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("GameSession", "id", id));
        if(!game.getPlayers().contains(user) && !game.getSpectators().contains(user)) {
            throw new IllegalArgumentException("User is not allowed to access this game");
        }
        return game;
    }

    @Transactional(readOnly = true)
    public List<GameSession> findGameSessions() {
        return repo.findAll();
    }
    @Transactional(readOnly = true)
    public List<GameSession> findGameSessionsByStatus(String status) {
        return repo.findByStatus(status);
    }

    @Transactional(readOnly = true)
    public List<GameSession> findGameSessionsByCreator(Integer creatorId) {
        return findGameSessions().stream().filter(game -> game.getCreator().getId().equals(creatorId)).toList();
    }

    @Transactional(readOnly = true)
    public List<GameSession> findGameSessionsByCreatorAndStatus(String status, Integer creatorId) {
        return findGameSessions().stream().filter(game -> game.getCreator().getId().equals(creatorId) && game.getStatus().equals(status)).toList();
    }

    @Transactional(rollbackFor = Exception.class)
    public GameSession joinGameSession(Integer id, User user, String code) {
        GameSession game = findGameSession(id);
        if (game.getIsPrivate() && !game.getPin().equals(code)) {
            throw new IllegalArgumentException("Invalid code");
        }
        if (game.getStatus().equals("FINISHED") || game.getStatus().equals("IN_PROGRESS")) {
            throw new IllegalArgumentException("Game is not joinable");
        }
        if (game.getCurrentPlayers() == game.getMaxPlayers()) {
            throw new IllegalArgumentException("Game is full");
        }
        if (!game.getIsPrivate() && code != null) {
            throw new IllegalArgumentException("Public games must not have a code");
        }
        if(game.getPlayers().contains(user)){
            throw new IllegalArgumentException("User is already in the game");
        }
        GameSession updatedGame = findGameSession(id);
        updatedGame.getPlayers().add(user);
        updatedGame.setCurrentPlayers(updatedGame.getCurrentPlayers() + 1);
        return updateGameSession(updatedGame, updatedGame.getId());
    }

    @Transactional(rollbackFor = Exception.class)
    public GameSession changeStatus(GameSession game){
        if(game.getStatus().equals("WAITING")){
            game.setStart(LocalDateTime.now());
            game.setStatus("IN_PROGRESS");
           GameSession newGame = updateGameSession(game, game.getId());
           return newGame;
        } 
        else if(game.getStatus().equals("IN_PROGRESS")){
            game.setEnd(LocalDateTime.now());
            game.setStatus("FINISHED");
            GameSession newGame = updateGameSession(game, game.getId());
            return newGame;
        }
        else{
            throw new IllegalArgumentException("A finished game cannot change its status again");
        }

    }
    @Transactional(rollbackFor = Exception.class)
    public GameSession nextTurn(GameSession game){
        if(game.getStatus().equals("WAITING")){
            throw new IllegalArgumentException("Game is not in progress");
        }
        else if(game.getStatus().equals("FINISHED")){
            throw new IllegalArgumentException("Game is finished");
        }
        else{
            checkForTimeExceededPlayers(game);
            
            if (game.getTurn() == game.getCurrentPlayers() - 1) {
                game.setTurn(0);
            }
            else {
                game.setTurn(game.getTurn() + 1);
            }
            return updateGameSession(game, game.getId());
        }

    }
@Transactional(readOnly = true)
public Integer findShortestGameByUser(Integer userId) {
    Optional<Integer> res =  repo.findShortestGameByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();

}
@Transactional(readOnly = true)
public Integer findLongestGameByUser(Integer userId) {
    Optional<Integer> res =  repo.findLongestGameByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}
@Transactional(readOnly = true)
public Double findAverageGameByUser(Integer userId) {
   Optional<Double> res=  repo.findAverageGameByUser(userId);
   if(res.isEmpty()){
    return 0.;
}
return res.get();
}

@Transactional(readOnly = true)
public Integer findSmallestGameByUser(Integer userId) {
    Optional<Integer> res=  repo.findSmallestGameByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findBiggestGameByUser(Integer userId) {
    Optional<Integer> res=  repo.findBiggestGameByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();

}

@Transactional(readOnly = true)
public Double findAveragePlayersByUser(Integer userId) {
    Optional<Double> res=  repo.findAveragePlayersByUser(userId);
    if(res.isEmpty()){
        return 0.;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findTotalGamesByUser(Integer userId) {
    Optional<Integer> res=  repo.findTotalGamesByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();

}

@Transactional(readOnly = true)
public Integer findTotalWinsByUser(Integer userId) {
    Optional<Integer> res=  repo.findTotalWinsByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findShortestGame() {
    Optional<Integer> res=  repo.findShortestGame();
    if(res.isEmpty()){
        return 0;
    }
    return res.get();

}

@Transactional(readOnly = true)
public Integer findLongestGame() {
    Optional<Integer> res=  repo.findLongestGame();
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Double findAverageGame() {
    Optional<Double> res=  repo.findAverageGame();
    if(res.isEmpty()){
        return 0.;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Double findAverageGameSize() {
    Optional<Double> res=  repo.findAverageGameSize();
    if(res.isEmpty()){
        return 0.0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findMinutesPlayed() {
    Optional<Integer> res=  repo.findMinutesPlayed();
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findTotalFinishedGames() {
    Optional<Integer> res=  repo.findTotalFinishedGames();
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findMinutesPlayedByUser(Integer userId) {
    Optional<Integer> res=  repo.findMinutesPlayedByUser(userId);
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

@Transactional(readOnly = true)
public Integer findTotalActiveGames() {
    Optional<Integer> res=  repo.findTotalActiveGames();
    if(res.isEmpty()){
        return 0;
    }
    return res.get();
}

public void validateCasualGamerLimitations(User creator) {
    if (creator.getProfileType() == ProfileType.CASUAL_GAMER) {
        LocalDate today = LocalDate.now();
        if (creator.getLastGameDate() == null || !creator.getLastGameDate().equals(today)) {
            creator.setDailyGamesPlayed(0);
            creator.setLastGameDate(today);
            userService.saveUser(creator);
        }
        if (creator.getDailyGamesPlayed() >= 2) {
            throw new CasualGamerLimitExceededException("DAILY_GAMES", 
                "Los jugadores Casual Gamer pueden crear un máximo de 2 partidas diarias. Has alcanzado tu límite diario.");
        }
    }
}

private void updateDailyGamesCount(User creator) {
    if (creator.getProfileType() == ProfileType.CASUAL_GAMER) {
        creator.setDailyGamesPlayed(creator.getDailyGamesPlayed() + 1);
        userService.saveUser(creator);
    }
}

private void checkForTimeExceededPlayers(GameSession gameSession) {
    if (gameSession.getStatus().equals("IN_PROGRESS") && gameSession.getStart() != null) {
        List<User> playersToRemove = new ArrayList<>();
        
        for (User player : gameSession.getPlayers()) {
            if (hasExceededMaxGameTime(gameSession, player)) {
                playersToRemove.add(player);
            }
        }

        for (User player : playersToRemove) {
            kickPlayerForTimeExceeded(gameSession, player);
        }
    }
}

public boolean hasExceededMaxGameTime(GameSession gameSession, User user) {
    if (user.getProfileType() == ProfileType.CASUAL_GAMER && 
        "IN_PROGRESS".equals(gameSession.getStatus()) &&
        gameSession.getStart() != null) {

        // Prefer a per-game configured casual max duration when provided; otherwise default to 30 minutes
        Integer configured = gameSession.getCasualMaxDurationMinutes();
        long maxDurationMinutes = (configured != null && configured > 0) ? configured.longValue() : 30L;
        LocalDateTime now = LocalDateTime.now();
        long minutesPlayed = java.time.Duration.between(gameSession.getStart(), now).toMinutes();

        return minutesPlayed > maxDurationMinutes;
    }
    return false;
}

@Transactional
public void kickPlayerForTimeExceeded(GameSession gameSession, User user) {
    gameSession.getPlayers().remove(user);
    gameSession.setCurrentPlayers(gameSession.getCurrentPlayers() - 1);

    if (gameSession.getCurrentPlayers() == 0) {
        gameSession.setStatus("FINISHED");
        gameSession.setEnd(LocalDateTime.now());
    }
    
    repo.save(gameSession);
}

public String checkPlayerTimeStatus(Integer gameSessionId, Integer userId) {
    GameSession gameSession = findGameSession(gameSessionId);
    User user = userService.findUser(userId);
    
    if (hasExceededMaxGameTime(gameSession, user)) {
        kickPlayerForTimeExceeded(gameSession, user);
        return "KICKED_TIME_EXCEEDED";
    }
    
    return "OK";
}
}
