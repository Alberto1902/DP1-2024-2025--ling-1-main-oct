package es.us.dp1.lx_xy_24_25.your_game_name.fight;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/fights")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Fight", description = "Fight management API")
public class FightRestController {

    private final FightService fightService;
    private final GameSessionService gameSessionService;
    private final PieceService pieceService;


    @Autowired
    public FightRestController(FightService fightService, PieceService pieceService, GameSessionService gameSessionService) {
        this.fightService = fightService;
        this.pieceService = pieceService;
        this.gameSessionService = gameSessionService;
    }

    @Operation(summary = "Start a fight", description = "Starts a fight between two pieces. It is only available for users with the role of PLAYER")
    @Parameter(name = "attackerId", description = "Attacker piece id")
    @Parameter(name = "defenderId", description = "Defender piece id")
    @Parameter(name = "gameId", description = "Game session id")
    @PostMapping(value = "/{attackerId}-{defenderId}")
    public ResponseEntity<?> startFight(@PathVariable Integer attackerId, @PathVariable Integer defenderId, @RequestParam(value = "gameId") Integer gameId) {
        try {
            Piece attacker = pieceService.findPiece(attackerId);
            Piece defender = pieceService.findPiece(defenderId);
            GameSession game = gameSessionService.findGameSession(gameId);
            Fight savedFight = fightService.fight(attacker, defender, game);
            return ResponseEntity.ok(savedFight);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }

    @Operation(summary = "Get the name of the most fought square", description = "Returns the name of the square where the most fights have taken place. It is only available for users with the role of PLAYER")
    @GetMapping(value = "/mostFoughtSquare")
    public ResponseEntity<?> getMostFoughtSquare() {
        try {
            Square mostFoughtSquare = fightService.getMostFoughtSquare();
            return new ResponseEntity(mostFoughtSquare, HttpStatus.OK);
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred.");
        }
    }
    
}
