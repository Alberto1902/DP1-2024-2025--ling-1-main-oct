package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.websocket.server.PathParam;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.fight.Fight;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/gamesessions")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "GameSession", description = "Game session management API")
public class GameSessionRestController {
    private final GameSessionService gameSessionService;
    private final UserService userService;

    @Autowired
    public GameSessionRestController(GameSessionService gameSessionService, UserService userService) {
        this.gameSessionService = gameSessionService;
        this.userService = userService;
    }

    @Operation(summary = "Create a game session", description = "Creates a new game session. It is only available for users with the role of PLAYER")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Game session object to be published", required = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<GameSession> createGameSession(@Valid @RequestBody GameSession game) {
        GameSession newGame = gameSessionService.createGameSession(game);
        return new ResponseEntity<>(newGame, HttpStatus.CREATED);
    }

    @Operation(summary = "Find a game session by its id", description = "Returns a game session given its id if the user is allowed. It is only available for users with the role of PLAYER")
    @Parameter(name = "id", description = "Game session id")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/game")
    public ResponseEntity<GameSession> findGameSession(@PathParam("id") Integer id, @RequestParam(value="userId") Integer userId) {
        User user = userService.findUser(userId);
        GameSession game = gameSessionService.findGameSessionIfAllowed(id, user);
        return new ResponseEntity<>(game, HttpStatus.OK);
    }

    @Operation(summary = "Find game sessions", description = "Returns a list of game sessions depending on a series of parameters. It is only available for users with the role of PLAYER")
    @Parameter(name = "creatorId", description = "Game session creator id")
    @Parameter(name = "status", description = "Game session status")
    @GetMapping
    public ResponseEntity<List<GameSession>> findGameSessions(@RequestParam(value="creatorId", required=false) Integer creatorId, @RequestParam(value="status", required=false) String status) {
        if(creatorId != null && status != null) {
            return new ResponseEntity<>(gameSessionService.findGameSessionsByCreatorAndStatus(status, creatorId), HttpStatus.OK);
        }
        else if(creatorId != null) {
            return new ResponseEntity<>(gameSessionService.findGameSessionsByCreator(creatorId), HttpStatus.OK);
        }
        else if (status != null) {
            return new ResponseEntity<>(gameSessionService.findGameSessionsByStatus(status), HttpStatus.OK);
        }
        return new ResponseEntity<>(gameSessionService.findGameSessions(), HttpStatus.OK);
    }


    @Operation(summary = "Join a game session", description = "Joins a user to a game session. It is only available for users with the role of PLAYER")
    @Parameter(name = "id", description = "Game session id")
    @Parameter(name = "userId", description = "User id")
    @Parameter(name = "code", description = "Game session code")
    @PutMapping(value = "/game/join")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GameSession> joinGameSession(@RequestParam(value="id") Integer id, @RequestParam(value="userId") Integer userId, @RequestParam(value="code", required=false) String code) {
        User user = userService.findUser(userId);
        return new ResponseEntity<>(this.gameSessionService.joinGameSession(id, user, code), HttpStatus.OK);
    }

    @Operation(summary = "Change game session status", description = "Changes the status of a game session. It is only available for users with the role of PLAYER")
    @Parameter(name = "id", description = "Game session id")
    @PutMapping(value = "/game/changeStatus")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GameSession> changeGameSessionStatus(@RequestParam(value="id") Integer id){
        GameSession game = gameSessionService.findGameSession(id);
        return new ResponseEntity<>(this.gameSessionService.changeStatus(game), HttpStatus.OK);
    }

    @Operation(summary = "Changes the turn of a gameSession", description = "Changes the turn of a gameSession. It is only available for users with the role of PLAYER")
    @Parameter(name = "id", description = "Game session id")
    @PutMapping(value = "game/nextTurn")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<GameSession> nextTurn(@RequestParam(value="id") Integer id){
        GameSession game = gameSessionService.findGameSession(id);
        return new ResponseEntity<>(this.gameSessionService.nextTurn(game), HttpStatus.OK);
    }

    @Operation(summary = "Find active game sessions by user", description = "Returns a list of active game sessions where the user is taking part. It is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/active")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<GameSession>> findActiveGameSessions(@RequestParam(value="userId") Integer userId) {
        User user = userService.findUser(userId);
        List<GameSession> activeSessions = gameSessionService.findActiveGameSessionsByUser(user);
        return new ResponseEntity<>(activeSessions, HttpStatus.OK);
    }

    @Operation(summary = "Find the current fight in the gameSession", description = "Returns the fight that is occurring in the gameSession. It is only available for users with the role of PLAYER")
    @Parameter(name = "gameId", description = "Game session id")
    @GetMapping(value = "/currentFight")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Fight> findCurrentFight(@RequestParam(value="gameId") Integer gameId){
        GameSession game = gameSessionService.findGameSession(gameId);
        return new ResponseEntity<Fight>(game.getCurrenFight(), HttpStatus.OK);
    }

    @Operation(summary = "Remove the current fight in the gameSession", description = "Removes the fight that is occurring in the gameSession. It is only available for users with the role of PLAYER")
    @Parameter(name = "gameId", description = "Game session id")
    @PutMapping(value = "/removeCurrentFight")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Void> removeCurrentFight(@RequestParam(value="gameId") Integer gameId){
        GameSession game = gameSessionService.findGameSession(gameId);
        game.setCurrenFight(null);
        gameSessionService.updateGameSession(game, game.getId());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Find the number of games played by a user", description = "Finds the number of games played by a user. It is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/totalGames")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> findTotalGames(@RequestParam(value="userId") Integer userId){
        return new ResponseEntity<Integer>(gameSessionService.findTotalGamesByUser(userId), HttpStatus.OK);
    }

    @Operation(summary = "Find the number of games won by a user", description = "Finds the number of games won by a user. It is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/totalWins")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> findTotalWins(@RequestParam(value="userId") Integer userId){
        return new ResponseEntity<Integer>(gameSessionService.findTotalWinsByUser(userId), HttpStatus.OK);
    }

    @Operation(summary = "Find the number of minutes played by all users", description = "Finds the number of minutes played by all users. It is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/minutesPlayed")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> findMinutesPlayed(){
        return new ResponseEntity<Integer>(gameSessionService.findMinutesPlayed(), HttpStatus.OK);
    }

    @Operation(summary = "Find the number of minutes played by a user", description = "Finds the number of minutes played by a user. It is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/minutesPlayedUser")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> findMinutesPlayedByUser(@RequestParam(value="userId") Integer userId){
        return new ResponseEntity<Integer>(gameSessionService.findMinutesPlayedByUser(userId), HttpStatus.OK);
    }

    @Operation(summary = "Check player time status", description = "Verifica si un jugador Casual Gamer ha excedido el tiempo máximo y lo expulsa si es necesario. It is only available for users with the role of PLAYER")
    @Parameter(name = "gameSessionId", description = "Game session id")
    @Parameter(name = "userId", description = "User id")
    @GetMapping(value = "/checkPlayerTime")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> checkPlayerTimeStatus(@RequestParam(value="gameSessionId") Integer gameSessionId, 
                                                       @RequestParam(value="userId") Integer userId){
        String status = gameSessionService.checkPlayerTimeStatus(gameSessionId, userId);
        return new ResponseEntity<String>(status, HttpStatus.OK);
    }
}
