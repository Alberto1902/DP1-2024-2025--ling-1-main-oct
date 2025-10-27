package es.us.dp1.lx_xy_24_25.your_game_name.decks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import java.util.List;
import jakarta.validation.Valid;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/decks")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Deck", description = "Deck management API")
public class DeckController {

    private final DeckService deckService;
    private final GameSessionService gameSessionService;
    private final PieceService pieceService;

    @Autowired
    public DeckController(DeckService deckService, GameSessionService gameSessionService, PieceService pieceService) {
        this.deckService = deckService;
        this.gameSessionService = gameSessionService;
        this.pieceService = pieceService;
    }

    @Operation(summary = "Get a particular deck", description = "Get a particular deck by its ID. If the ID is not provided, it will return the specified deck for the specified game. This operation can only be performed by users identified as PLAYERS.")
    @Parameter(name = "id", description = "ID of the deck that we want to retrieve", required = false)
    @Parameter(name = "isDiscard", description = "Indication of the type of deck that we want to retrieve", required = true)
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @GetMapping(value = "/deck")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Deck> findDeck(@RequestParam(value="id", required = false) Integer id, @RequestParam(value="isDiscard") Boolean isDiscard, @RequestParam(value="gameSessionId") Integer gameSessionId) {
        if (id == null) {
            GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
            if (gameSession == null) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(this.deckService.findDecksByIsDiscardAndGameSession(isDiscard, gameSession), HttpStatus.OK);
        }
        return new ResponseEntity<>(this.deckService.findDeck(id), HttpStatus.OK);
    }
    @Operation(summary = "Take one card from a deck", description= "Take one card from a deck. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "isDiscard", description = "Indication of the type of deck that we want to retrieve", required = true)
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @PutMapping(value = "/takeOneCard")
    public ResponseEntity<Card> takeOneCard(@RequestParam(value="isDiscard") Boolean isDiscard, @RequestParam(value="gameSessionId") Integer gameSessionId) {
        GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
        if (gameSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Deck deck = this.deckService.findDecksByIsDiscardAndGameSession(isDiscard, gameSession);
        Piece piece = pieceService.findPieceByGameIdAndPlayerOrder(gameSessionId, gameSession.getTurn());
        Card card = deckService.takeCard(deck, piece);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @Operation(summary = "Take one card from a deck after a fight", description= "Take one card from a deck after a fight. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "isDiscard", description = "Indication of the type of deck that we want to retrieve the card from", required = true)
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @PutMapping(value = "/takeOneCardAfterFight")
    public ResponseEntity<Card> takeOneCardAfterFight(@RequestParam(value="isDiscard") Boolean isDiscard, @RequestParam(value="gameSessionId") Integer gameSessionId) {
        GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
        if (gameSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Deck deck = this.deckService.findDecksByIsDiscardAndGameSession(isDiscard, gameSession);
        Card card = deckService.takeCard(deck, null);
        return new ResponseEntity<>(card, HttpStatus.OK);
    }

    @Operation(summary = "Shuffle the deck at the beggining of the game", description= "Shuffle the deck at the beggining of the game. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "isDiscard", description = "Indication of the type of deck that we want to retrieve the cards from", required = true)
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @PutMapping(value = "/shuffle")
    public ResponseEntity<Void> shuffleDeck(@RequestParam(value="isDiscard") Boolean isDiscard, @RequestParam(value="gameSessionId") Integer gameSessionId) {
        GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
        if (gameSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Deck deck = this.deckService.findDecksByIsDiscardAndGameSession(isDiscard, gameSession);
        deckService.shuffle(deck);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Reshuffle the deck", description= "Reshuffle the deck. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @PutMapping(value = "/reshuffle")
    public ResponseEntity<Void> reshuffle(@RequestParam(value = "gameSessionId") Integer gameSessionId) {
        GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
        if (gameSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Deck deck = deckService.findDecksByIsDiscardAndGameSession(false, gameSession);
        Deck discardDeck = deckService.findDecksByIsDiscardAndGameSession(true, gameSession);
        deckService.reshuffle(deck, discardDeck);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Discard cards", description= "Discard cards. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "pieceId", description = "ID of the piece that is discarding the cards", required = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "List of cards to discard", required = true, content = @Content(schema = @Schema(implementation = Card.class)))
    @PutMapping(value = "/discard")
    public ResponseEntity<Void> discardCards(@Valid @RequestBody List<Card> cards, @RequestParam(value="id") Integer pieceId) {
        Piece piece = pieceService.findPiece(pieceId);
        if (piece == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        deckService.discard(piece, cards);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Discard a card after a fight", description= "Discard a card after a fight. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "gameId", description = "ID of the GameSession where the deck belongs", required = true)
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Card to discard", required = true, content = @Content(schema = @Schema(implementation = Card.class)))
    @PutMapping(value = "/discardAfterFight")
    public ResponseEntity<Void> discardCardAfterFight(@Valid @RequestBody Card card, @RequestParam(value = "gameId") Integer gameId) {
        GameSession game = gameSessionService.findGameSession(gameId);
        if (game == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Deck deck = deckService.findDecksByIsDiscardAndGameSession(true, game);
        deckService.discard(deck, card);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Create decks at the beggining of a GameSession", description= "Create decks at the beggining of a GameSession. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @PostMapping
    public ResponseEntity<Void> createDecks(@RequestParam(value="gameSessionId") Integer gameSessionId) {
        GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
        if (gameSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        deckService.createDeck(true, gameSession);
        deckService.createDeck(false, gameSession);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Operation(summary = "Deal initial cards to the players", description= "Deal initial cards to the players. This operation can be only performed by users identified as PLAYERS.")
    @Parameter(name = "gameSessionId", description = "ID of the GameSession where the deck belongs", required = true)
    @PutMapping(value = "/initialCards")
    public ResponseEntity<List<Card>> getInitialCards(@RequestParam(value="gameSessionId") Integer gameSessionId) {
        GameSession gameSession = gameSessionService.findGameSession(gameSessionId);
        if (gameSession == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        List<Card> cards = deckService.getInitialCards(gameSession);
        return new ResponseEntity<>(cards, HttpStatus.OK);
    }
}
