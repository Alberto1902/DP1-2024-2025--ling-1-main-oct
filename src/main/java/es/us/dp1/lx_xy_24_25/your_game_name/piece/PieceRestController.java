package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.fight.Fight;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.square.SquareService;

@RestController
@RequestMapping("/api/v1/pieces")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Piece", description = "Piece management API")
public class PieceRestController {

    private final PieceService pieceService;
    private final GameSessionService gameSessionService;
    private final SquareService squareService;
    private final UserService userService;

    @Autowired
    public PieceRestController(PieceService pieceService, GameSessionService gameSessionService,
            SquareService squareService, UserService userService) {
        this.pieceService = pieceService;
        this.gameSessionService = gameSessionService;
        this.squareService = squareService;
        this.userService = userService;
    }

    @Operation(summary = "Create all pieces", description = "Create all pieces for a game. This operation will be performed once for game by users with the role of PLAYER, more precisely, the creator of the game.")
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<Piece>> createAllPieces(@RequestParam(value = "gameId") Integer gameId) {
        GameSession game = gameSessionService.findGameSession(gameId);
        List<Piece> pieces = this.pieceService.createAllPieces(game);
        return new ResponseEntity<>(pieces, HttpStatus.CREATED);
    }

    @Operation(summary = "Find all pieces", description = "Find all pieces for a game given its ID. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @GetMapping(value = "/{gameId}")
    public ResponseEntity<List<Piece>> findPiecesByGameId(@PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(pieceService.findPiecesByGameId(gameId), HttpStatus.OK);
    }

    @Operation(summary = "Find piece by ID and user ID", description = "Find a piece given its ID and user ID. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "userId", description = "The user identifier", required = true)
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @GetMapping(value = "/{userId}/{gameId}")
    public ResponseEntity<Piece> findPieceByUserIdAndGameId(@PathVariable("userId") Integer userId,
            @PathVariable("gameId") Integer gameId) {
        return new ResponseEntity<>(pieceService.findPieceByUserIdAndGameId(userId, gameId), HttpStatus.OK);
    }

    @Operation(summary = "Move the given piece to a given square", description = "Find a piece given its ID, then find the square given the ID, finally, move it This will be available for all users with the role of PLAYER.")
    @Parameter(name = "position", description = "The square identifier", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/move")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> movePiece(@RequestParam(value = "position") Integer positionId,
            @RequestParam(value = "id") Integer pieceId) {
        Piece piece = this.pieceService.findPiece(pieceId);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Square square = this.squareService.findSquare(positionId);
        if (square == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        User user = this.userService.findCurrentUser();
        return new ResponseEntity<>(this.pieceService.movePiece(square, piece, user), HttpStatus.OK);
    }

    @Operation(summary = "Get possible launches", description = "Get possible launches for a given piece and word. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "word", description = "The word to be launched", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @GetMapping(value = "/possibleLaunches")
    public ResponseEntity<List<Square>> possibleLaunches(@RequestParam(value = "word") String word,
            @RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Square> squares = this.pieceService.getPossibleLaunches(piece, word);
        return new ResponseEntity<>(squares, HttpStatus.OK);
    }

    @PutMapping(value = "/setWord/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> setBag(@PathVariable Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = pieceService.resetActionPoints(piece, id);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Set initial position", description = "Set the initial position for a given piece. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "blackDice", description = "The value of the black dice, used to compute the needed square", required = true)
    @Parameter(name = "whiteDice", description = "The value of the white dice, used to compute the needed square", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/InitialPosition/{id}/{blackDice}/{whiteDice}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> InitialPosition(@PathVariable Integer id, @PathVariable Integer blackDice,
            @PathVariable Integer whiteDice) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Square square = this.squareService.findSquare(blackDice, whiteDice);
        if (square == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Piece updatedPiece = this.pieceService.setInitialPosition(piece, square);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Set initial position for non-player pieces", description = "Set the initial position for all non-player pieces")
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @PutMapping(value = "/initialPositionNonPlayer/{gameId}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Piece>> initialPositionNonPlayer(@PathVariable Integer gameId) {
        List<Piece> pieces = this.pieceService.setAllNonPlayerInitialPositions(gameId);
        return new ResponseEntity<>(pieces, HttpStatus.OK);
    }

    @Operation(summary = "Receive card", description = "Receive a card for a given piece. This will be available for all users with the role of PLAYER.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The card to be received", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/receiveCard")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> receiveCard(@Valid @RequestBody Card card, @RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = this.pieceService.receiveCard(card, piece);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Receive initial cards", description = "Receive initial cards for a given piece. This will be available for all users with the role of PLAYER.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The cards to be received", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/receiveInitialCards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> receiveIntialCards(@Valid @RequestBody List<Card> cards,
            @RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = this.pieceService.receiveInitialCards(cards, piece);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Receive card after fight", description = "Receive a card after a fight for a given piece. This will be available for all users with the role of PLAYER.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The card to be received", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/receiveCardAfterFight")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> receiveCardAfterFight(@Valid @RequestBody Card card,
            @RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = this.pieceService.receiveCardAfterFight(card, piece);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Put cards in bag", description = "Put cards in bag for a given piece. This will be available for all users with the role of PLAYER.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The cards to be put in the bag", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/putCardsInBag")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> putCardsInBag(@Valid @RequestBody List<Card> cards,
            @RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = this.pieceService.putCardsInBag(cards, piece);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Discard cards", description = "Discard cards for a given piece. This will be available for all users with the role of PLAYER.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The word to be discarded", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/discardCards")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<List<Card>> discardCard(@Valid @RequestBody String word,
            @RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Card> cards = this.pieceService.discardCards(word, piece);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }

    @Operation(summary = "Discard cards after fight", description = "Discard cards after a fight for a given piece. This will be available for all users with the role of PLAYER.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "The word to be discarded", required = true)
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/defineActionPoints")
    public ResponseEntity<Piece> defineActionPoints(@RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = this.pieceService.defineActionPoints(piece);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "End action phase", description = "End action phase for a given piece. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/endActionPhase")
    public ResponseEntity<Piece> nextPhase(@RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        Piece newPiece = piece.getState().endActionPhase();
        return new ResponseEntity<>(this.pieceService.updatePiece(newPiece, id), HttpStatus.OK);
    }

    @Operation(summary = "Allows the user to try to escape to win the game", description = "Allows the user to try to escape to win the game. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/escape")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Integer> escape(@RequestParam(value = "id") Integer id) {
        Piece piece = pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Integer dice = pieceService.escape(piece);
        return new ResponseEntity<>(dice, HttpStatus.OK);
    }

    @Operation(summary = "Lets the user to get catapulted", description = "Lets the user to get catapulted. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "id", description = "The piece identifier", required = true)
    @PutMapping(value = "/catapulted")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Piece> catapulted(@RequestParam(value = "id") Integer id) {
        Piece piece = this.pieceService.findPiece(id);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        Piece updatedPiece = this.pieceService.catapulted(piece);
        return new ResponseEntity<>(updatedPiece, HttpStatus.OK);
    }

    @Operation(summary = "Allows to obtain the pieces that are in a given square", description = "Allows to obtain the pieces that are in a given square. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "gameSessionId", description = "The game session identifier", required = true)
    @Parameter(name = "squareId", description = "The square identifier", required = true)
    @GetMapping(value = "/piecesInSquare")
    public ResponseEntity<List<Piece>> findPiecesInSquare(@RequestParam(value = "gameSessionId") Integer gameSessionId,
            @RequestParam(value = "squareId") Integer squareId) {
        Square square = this.squareService.findSquare(squareId);
        if (square == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        GameSession game = this.gameSessionService.findGameSession(gameSessionId);
        if (game == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        List<Piece> pieces = this.pieceService.findPiecesInSquare(game, square);
        return new ResponseEntity<>(pieces, HttpStatus.OK);
    }

    @PutMapping(value = "/fighting")
    public ResponseEntity<Piece> fighting(@RequestParam(value = "pieceId") Integer pieceId) {
        Piece piece = pieceService.findPiece(pieceId);
        return ResponseEntity.ok(pieceService.isFighting(piece));
    }

    @PutMapping(value = "/attacking")
    public ResponseEntity<Piece> attacking(@RequestParam(value = "pieceId") Integer pieceId) {
        Piece piece = pieceService.findPiece(pieceId);
        return ResponseEntity.ok(pieceService.isAttacking(piece));
    }

    @PutMapping(value = "/defending")
    public ResponseEntity<Piece> defending(@RequestParam(value = "pieceId") Integer pieceId) {
        Piece piece = pieceService.findPiece(pieceId);
        return ResponseEntity.ok(pieceService.isDefending(piece));
    }

    @GetMapping(value = "/attacker")
    public ResponseEntity<Piece> getAttacker(@RequestParam(value = "gameId") Integer gameId) {
        Piece attacker = pieceService.getAttacker(gameId);
        return ResponseEntity.ok(attacker);
    }

    @GetMapping(value = "/defender")
    public ResponseEntity<Piece> getDefender(@RequestParam(value = "gameId") Integer gameId) {
        Piece defender = pieceService.getDefender(gameId);
        return ResponseEntity.ok(defender);
    }

    @Operation(summary = "Steal a card from the hand of the loser of the current fight", description = "Steal a card from the loser of the current fight. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @PutMapping(value = "/stealFromPlayerHand")
    public ResponseEntity<Piece> stealFromPlayerHand(@RequestParam(value = "gameId") Integer gameId) {
        GameSession game = gameSessionService.findGameSession(gameId);
        Fight fight = game.getCurrenFight();
        return ResponseEntity.ok(pieceService.stealFromOtherPlayerHand(fight.getWinner(), fight.getLoser()));
    }

    @Operation(summary = "Steal a card from the bag of the loser of the current fight", description = "Steal a card from the loser of the current fight. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @PutMapping(value = "/stealFromPlayerBag")
    public ResponseEntity<Piece> stealFromPlayerBag(@RequestParam(value = "gameId") Integer gameId,
            @RequestParam(value = "cardId") Integer cardId) {
        GameSession game = gameSessionService.findGameSession(gameId);
        Fight fight = game.getCurrenFight();
        return ResponseEntity.ok(pieceService.stealFromOtherPlayerBag(fight.getWinner(), fight.getLoser(), cardId));
    }

    @Operation(summary = "Discard a card after losing a fight", description = "Discard a card after losing a fight. This will be available for all users with the role of PLAYER.")
    @Parameter(name = "gameId", description = "The game identifier", required = true)
    @Parameter(name = "cardId", description = "The card identifier", required = true)
    @Parameter(name = "fromHand", description = "If the card is from the hand", required = true)
    @PutMapping(value = "/discardAfterLosing")
    public ResponseEntity<Card> discardAfterLosing(@RequestParam(value = "gameId") Integer gameId,
            @RequestParam(value = "cardId") Integer cardId, @RequestParam(value = "fromHand") Boolean fromHand) {
        GameSession game = gameSessionService.findGameSession(gameId);
        Fight fight = game.getCurrenFight();
        Card card = pieceService.discardAfterLosing(fight.getLoser(), cardId, fromHand);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }
}
