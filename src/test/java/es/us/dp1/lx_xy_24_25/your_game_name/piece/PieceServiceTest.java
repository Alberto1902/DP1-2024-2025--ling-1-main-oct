package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.fight.Fight;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.square.SquareService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@SpringBootTest
@AutoConfigureTestDatabase
public class PieceServiceTest {

    @Autowired
    protected PieceService pieceService;

    @Autowired
    protected SquareService squareService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GameSessionService gameSessionService;

    @Test
    void shouldFindPieceById() {
        Piece piece = pieceService.findPiece(4);
        assertNotNull(piece);
        assertEquals(4, piece.getId());
    }

    @Test
    void shouldReturnNullWhenPieceNotFound() {
        Piece piece = pieceService.findPiece(999);
        assertNull(piece);
    }

    @Test
    void shouldSaveNewPiece() {
        Square square = squareService.findSquare(1);
        User user = this.userService.findUser(3);
        GameSession game = this.gameSessionService.findGameSession(10000);

        Piece piece = new Piece();
        piece.setStrength(1);
        piece.setPosition(square);
        piece.setUser(user);
        piece.setGame(game);
        piece.setImage("image_url");
        piece.setIsCampbell(false);

        Piece newPiece = pieceService.createPiece(piece);
        assertNotNull(newPiece.getId());


        
    }

    @Test
    void shouldUpdatePiece() {
        Piece piece = pieceService.findPiece(4);
        Square square = squareService.findSquare(2);
        piece.setStrength(2);
        piece.setPosition(square);
        piece.setWord("cap");

        Piece updatedPiece = pieceService.updatePiece(piece, 4);
        assertEquals(2, updatedPiece.getStrength());
        assertEquals(square, updatedPiece.getPosition());
        assertEquals("cap", updatedPiece.getWord());
    }

    @Test
    void shouldFindPieceByUserIdAndGameId() {
        Piece piece = pieceService.findPieceByUserIdAndGameId(3, 1);
        assertNotNull(piece);
        assertEquals(3, piece.getUser().getId());
        assertEquals(1, piece.getGame().getId());
    }

    @Test
    void shouldFindPiecesByGameId() {
        List<Piece> piecesInGame = pieceService.findPiecesByGameId(10000);
        assertNotNull(piecesInGame);
        assertNotEquals(0, piecesInGame.size());
    }

    @Test
    @Transactional
    void shouldCreateAllPieces() {
        GameSession game = gameSessionService.findGameSession(10001);
        List<Piece> pieces = pieceService.createAllPieces(game);
        assertNotNull(pieces);
        assertEquals(12, pieces.size());
    }

    @Test
    @Transactional
    void shouldFailCreateAllPiecesNotInProgressGame() {
        GameSession game = gameSessionService.findGameSession(10000);
        game.setStatus("FINISHED");
        assertThrows(IllegalArgumentException.class, () -> pieceService.createAllPieces(game));
    }

    @Test
    @Transactional
    void shouldMovePiece() {
        Piece piece = pieceService.findPiece(1);
        Square square = squareService.findSquare(5);
        User user = userService.findUser(3);
        piece.setActionPoints(3);
        PieceState state = new ActionState();
        piece.setState(state);
        state.setPiece(piece);
        pieceService.movePiece(square, piece, user);
        assertEquals(square, piece.getPosition());
    }

    @Test
    @Transactional
    void shouldFailMovePieceNotEnoughActionPoints() {
        Piece piece = pieceService.findPiece(1);
        Square square = squareService.findSquare(5);
        User user = userService.findUser(3);
        piece.setActionPoints(0);
        PieceState state = new ActionState();
        piece.setState(state);
        state.setPiece(piece);
        assertThrows(IllegalArgumentException.class, () -> pieceService.movePiece(square, piece, user));
    }

    @Test
    @Transactional
    void shouldFailMovePieceNotActionPhase() {
        Piece piece = pieceService.findPiece(1);
        Square square = squareService.findSquare(5);
        User user = userService.findUser(3);
        piece.setActionPoints(3);
        PieceState state = new DrawState();
        piece.setState(state);
        state.setPiece(piece);
        assertThrows(IllegalArgumentException.class, () -> pieceService.movePiece(square, piece, user));
    }

    @Test
    @Transactional
    void shouldFailMovePieceNotYourTurn() {
        Piece piece = pieceService.findPiece(2);
        Square square = squareService.findSquare(5);
        User user = userService.findUser(2);
        piece.setActionPoints(3);
        PieceState state = new ActionState();
        piece.setState(state);
        state.setPiece(piece);
        assertThrows(IllegalArgumentException.class, () -> pieceService.movePiece(square, piece, user));
    }

    @Test
    @Transactional
    void shouldGetPossibleLaunches() {
        Piece piece = pieceService.findPiece(1);
        String word = "room";
        Card r = new Card();
        Card o1 = new Card();
        Card o2 = new Card();
        Card m = new Card();
        r.setLetter("R");
        o1.setLetter("O");
        o2.setLetter("O");
        m.setLetter("M");
        List<Card> cards = List.of(r, o1, o2, m);
        piece.setBag(cards);
        List<Square> squares = pieceService.getPossibleLaunches(piece, word);
        assertNotEquals(0, squares.size());
    }

    @Test
    @Transactional
    void shouldFailToGetPossibleLaunches() {
        Piece piece = pieceService.findPiece(1);
        String word = "room";
        Card r = new Card();
        Card o1 = new Card();
        Card o2 = new Card();
        r.setLetter("R");
        o1.setLetter("O");
        o2.setLetter("O");
        List<Card> cards = List.of(r, o1, o2);
        piece.setBag(cards);
        assertThrows(IllegalArgumentException.class, () -> pieceService.getPossibleLaunches(piece, word));
    }

    @Test
    @Transactional
    void shouldSetInitialPosition() {
        Piece piece = pieceService.findPiece(1);
        Square square = squareService.findSquare(6);
        piece.setPosition(null);
        pieceService.setInitialPosition(piece, square);
        assertEquals(square, piece.getPosition());
    }

    @Test
    @Transactional
    void shouldSetNonPlayerInitialPosition() {
        GameSession game = gameSessionService.findGameSession(2);
        pieceService.createAllPieces(game);
        List<Piece> pieces = pieceService.findPiecesByGameId(2);
        Square square = squareService.findSquare(2);
        Square square2 = squareService.findSquare(3);
        Square square3 = squareService.findSquare(4);
        pieces.get(0).setPosition(square);
        pieces.get(1).setPosition(square2);
        pieces.get(2).setPosition(square3);
        pieceService.setAllNonPlayerInitialPositions(2);
        pieces = pieceService.findPiecesByGameId(2);
        assertNotNull(pieces.get(7).getPosition());
    }

    @Test
    @Transactional
    void shouldReceiveCard() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new DrawState();
        piece.setState(state);
        state.setPiece(piece);
        piece.setState(state);
        state.setPiece(piece);
        Card card = new Card();
        card.setLetter("R");
        pieceService.receiveCard(card, piece);
        assertEquals(1, piece.getHand().size());
    }

    @Test
    @Transactional
    void shouldFailReceiveCardInNotDrawPhase() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new ActionState();
        piece.setState(state);
        state.setPiece(piece);
        Card card = new Card();
        card.setLetter("R");
        assertThrows(IllegalArgumentException.class, () -> pieceService.receiveCard(card, piece));
    }

    @Test
    @Transactional
    void shouldFailReceiveCardInFullHand() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new DrawState();
        piece.setState(state);
        state.setPiece(piece);
        Card card = new Card();
        card.setLetter("R");
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            hand.add(new Card());
        }
        piece.setHand(hand);
        assertThrows(IllegalArgumentException.class, () -> pieceService.receiveCard(card, piece));
    }

    @Test
    @Transactional
    void shouldReceiveInitialCards() {
        Piece piece = pieceService.findPiece(1);
        Card c1 = new Card();
        Card c2 = new Card();
        Card c3 = new Card();
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);
        pieceService.receiveInitialCards(cards, piece);
        assertEquals(3, piece.getHand().size());
    }

    @Test
    @Transactional
    void shouldPutCardsInBag() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new BagState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        Card c2 = new Card();
        Card c3 = new Card();
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        List<Card> hand = new ArrayList<>();
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        piece.setHand(hand);
        pieceService.putCardsInBag(cards, piece);
    }

    @Test
    @Transactional
    void shouldFailPutCardsInBagInNotBagPhase() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new DrawState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        Card c2 = new Card();
        Card c3 = new Card();
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        List<Card> hand = new ArrayList<>();
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        piece.setHand(hand);
        assertThrows(IllegalArgumentException.class, () -> pieceService.putCardsInBag(cards, piece));
    }

    @Test
    @Transactional
    void shouldFailPutCardsInBagInFullHand() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new BagState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        Card c2 = new Card();
        Card c3 = new Card();
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        List<Card> hand = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            hand.add(new Card());
        }
        hand.add(c1);
        hand.add(c2);
        hand.add(c3);
        piece.setHand(hand);
        assertThrows(IllegalArgumentException.class, () -> pieceService.putCardsInBag(cards, piece));
    }

    @Test
    @Transactional
    void shouldFailPutFewCardsInBag() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new BagState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        Card c2 = new Card();
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        List<Card> hand = new ArrayList<>();
        hand.add(c1);
        hand.add(c2);
        assertThrows(IllegalArgumentException.class, () -> pieceService.putCardsInBag(cards, piece));
    }

    @ParameterizedTest
    @ValueSource(strings = { "cap", "pa" })
    @Transactional
    void shouldDiscardCards(String word) {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new WordState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        c1.setLetter("C");
        Card c2 = new Card();
        c2.setLetter("A");
        Card c3 = new Card();
        c3.setLetter("P");
        Card c4 = new Card();
        c4.setLetter("R");
        List<Card> bag = new ArrayList<>();
        bag.add(c1);
        bag.add(c2);
        bag.add(c3);
        bag.add(c4);
        piece.setBag(bag);
        pieceService.discardCards(word, piece);
        assertEquals(word.length(), piece.getBag().size());
    }

    @Test
    @Transactional
    void shouldFailDiscardCards() {
        Piece piece = pieceService.findPiece(1);
        String word = "cap";
        PieceState state = new WordState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        c1.setLetter("C");
        Card c2 = new Card();
        c2.setLetter("A");
        List<Card> bag = new ArrayList<>();
        bag.add(c1);
        bag.add(c2);
        piece.setBag(bag);
        assertThrows(IllegalArgumentException.class, () -> pieceService.discardCards(word, piece));
    }

    @Test
    @Transactional
    void shouldDefineActionPoints() {
        Piece piece = pieceService.findPiece(1);
        PieceState state = new DrawState();
        piece.setState(state);
        state.setPiece(piece);
        Card c1 = new Card();
        Card c2 = new Card();
        Card c3 = new Card();
        List<Card> cards = new ArrayList<>();
        cards.add(c1);
        cards.add(c2);
        cards.add(c3);
        piece.setHand(cards);
        pieceService.defineActionPoints(piece);
        assertEquals(4, piece.getActionPoints());
        ;
    }

    @Test
    @Transactional(rollbackFor = Exception.class)
    void shouldFailEscapeWithInvalidWord() {
        Piece piece = pieceService.findPiece(1);
        piece.setWord("InvalidWord");
        piece.setActionPoints(3);
        assertThrows(ResponseStatusException.class, () -> pieceService.escape(piece));
    }

    @Test
    @Transactional
    void shouldBeCatapultedToNewSquare() {
        Piece piece = pieceService.findPiece(1);
        Piece result = pieceService.catapulted(piece);
        assertNotNull(result.getPosition());
    }

    @Test
    @Transactional
    void shouldFindPiecesInSquare() {
        GameSession game = gameSessionService.findGameSession(1);
        Square square = squareService.findSquare(1);
        List<Piece> pieces = pieceService.findPiecesInSquare(game, square);
        assertNotNull(pieces);
    }

    @Test
    @Transactional
    void shouldFailDefineActionPointsWhenNotYourTurn() {
        Piece piece = pieceService.findPiece(1);
        GameSession game = piece.getGame();
        game.setTurn(2);
        assertThrows(IllegalArgumentException.class, () -> pieceService.defineActionPoints(piece));
    }

    @Test
    @Transactional
    void shouldSetIsFighting() {
        Piece piece = pieceService.findPiece(1);
        Piece result = pieceService.isFighting(piece);
        assertTrue(result.getIsFighting());
    }

    @Test
    @Transactional
    void shouldSetPieceToAttacking() {
        Piece piece = pieceService.findPiece(1);
        Piece updatedPiece = pieceService.isAttacking(piece);
        assertTrue(updatedPiece.getIsAttacking());
    }

    @Test
    @Transactional
    void shouldSetPieceToDefending() {
        Piece piece = pieceService.findPiece(1);
        Piece updatedPiece = pieceService.isDefending(piece);
        assertTrue(updatedPiece.getIsDefending());
    }

    @Test
    @Transactional
    void shouldRetrieveAttacker() {
        GameSession game = gameSessionService.findGameSession(1);
        Piece piece = pieceService.isAttacking(pieceService.findPiece(1));
        Piece attacker = pieceService.getAttacker(game.getId());
        assertEquals(piece, attacker);
    }

    @Test
    @Transactional
    void shouldReceiveCardAfterFight() {
        Piece piece = pieceService.findPiece(1);
        Card card = new Card();
        Piece updatedPiece = pieceService.receiveCardAfterFight(card, piece);
        assertTrue(updatedPiece.getHand().contains(card));
    }

    @Test
    @Transactional
    void shouldResetActionPoints() {
        Piece piece = pieceService.findPiece(1);
        Piece updatedPiece = pieceService.resetActionPoints(piece, piece.getId());
        assertEquals(0, updatedPiece.getActionPoints());
    }

    @Test
    @Transactional
    void shouldStealFromHandSuccessfully() {
        Piece winner = pieceService.findPiece(1);
        Piece loser = pieceService.findPiece(2);
        GameSession game = gameSessionService.findGameSession(1);
        Fight fight = new Fight();
        fight.setAttacker(winner);
        fight.setDefender(loser);
        game.setCurrenFight(fight);
        gameSessionService.updateGameSession(game, game.getId());
    
        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");
        card1.setId(1);
    
        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");
        card2.setId(2);
    
        loser.setHand(new ArrayList<>(List.of(card1, card2)));
        pieceService.updatePiece(loser, loser.getId());
    
        Piece updatedWinner = pieceService.stealFromOtherPlayerHand(winner, loser);
    
        assertTrue(
            updatedWinner.getHand().stream()
                .anyMatch(card -> "A".equals(card.getLetter()) || "B".equals(card.getLetter()))
        );
        assertEquals(1, loser.getHand().size());
    }
    
    @Test
    @Transactional
    void shouldStealFromBagSuccessfully() {
        Piece winner = pieceService.findPiece(1);
        Piece loser = pieceService.findPiece(2);
        GameSession game = gameSessionService.findGameSession(1);
        Fight fight = new Fight();
        fight.setAttacker(winner);
        fight.setDefender(loser);
        game.setCurrenFight(fight);
        gameSessionService.updateGameSession(game, game.getId());
    
        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");
        card1.setId(1);
    
        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");
        card2.setId(2);
    
        Card card3 = new Card();
        card3.setLetter("C");
        card3.setTitle("Card C");
        card3.setId(3);
    
        loser.setBag(new ArrayList<>(List.of(card1, card2, card3)));
        pieceService.updatePiece(loser, loser.getId());
    
        Piece updatedWinner = pieceService.stealFromOtherPlayerBag(winner, loser, card1.getId());
    
        assertTrue(
            updatedWinner.getHand().stream()
                .anyMatch(card -> "A".equals(card.getLetter()))
        );
        assertEquals(2, loser.getBag().size());
    }
    


    @Test
    @Transactional
    void shouldDiscardCardFromHandAfterLosing() {
        Piece loser = pieceService.findPiece(1);
        GameSession game = gameSessionService.findGameSession(1);
        Fight fight = new Fight();
        fight.setAttacker(pieceService.findPiece(2));
        fight.setDefender(loser);
        game.setCurrenFight(fight);
        gameSessionService.updateGameSession(game, game.getId());
    
        Card card = new Card();
        card.setLetter("A");
        card.setTitle("Card A");
        card.setId(1);
    
        loser.setHand(new ArrayList<>(List.of(card)));
        pieceService.updatePiece(loser, loser.getId());
    
        Card discardedCard = pieceService.discardAfterLosing(loser, card.getId(), true);
    
        assertNotNull(discardedCard);
        assertEquals("A", discardedCard.getLetter());
        assertEquals(0, loser.getHand().size());
    }
    
    @Test
    @Transactional
    void shouldDiscardCardFromBagAfterLosing() {
        Piece loser = pieceService.findPiece(1);
        GameSession game = gameSessionService.findGameSession(1);
        Fight fight = new Fight();
        fight.setAttacker(pieceService.findPiece(2));
        fight.setDefender(loser);
        game.setCurrenFight(fight);
        gameSessionService.updateGameSession(game, game.getId());
    
        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");
        card1.setId(1);
    
        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");
        card2.setId(2);
        Card card3 = new Card();
        card3.setLetter("C");
        card3.setTitle("Card C");
        card3.setId(3);
    
        loser.setBag(new ArrayList<>(List.of(card1, card2, card3)));
        pieceService.updatePiece(loser, loser.getId());
    
        Card discardedCard = pieceService.discardAfterLosing(loser, card1.getId(), false);
    
        assertNotNull(discardedCard);
        assertEquals("A", discardedCard.getLetter());
        assertEquals(2, loser.getBag().size());
    }
    @Test
    @Transactional
    void shouldHandleEscapeWithCorrectWord() {
        GameSession game = new GameSession();
        game.setId(1);
        game.setStatus("IN_PROGRESS");

        Square square = new Square();
        square.setEscapeWord("ESCAPE");

        User user = new User();
        user.setId(1);

        Piece piece = new Piece();
        piece.setId(1);
        piece.setStrength(3);
        piece.setActionPoints(2);
        piece.setUser(user);
        piece.setGame(game);

        String bag = "ESCAPE";
        Integer diceResult = pieceService.handleEscapeWithEscapeWord(piece, game, square, bag);

        assertNotNull(diceResult);
        assertTrue(diceResult >= 1 && diceResult <= 6);
        if (diceResult < piece.getStrength()) {
            assertEquals("FINISHED", game.getStatus());
            assertNotNull(game.getEnd());
            assertEquals(piece.getUser(), game.getWinner());
        } else {
            assertEquals(0, piece.getActionPoints());
            assertEquals(4, piece.getStrength());
        }
    }

    @Test
    @Transactional

    void shouldThrowExceptionForIncorrectEscapeWord() {
        GameSession game = new GameSession();
        game.setId(1);
        game.setStatus("IN_PROGRESS");

        Square square = new Square();
        square.setEscapeWord("ESCAPE");

        User user = new User();
        user.setId(1);

        Piece piece = new Piece();
        piece.setId(1);
        piece.setStrength(3);
        piece.setActionPoints(2);
        piece.setUser(user);
        piece.setGame(game);

        String bag = "WRONGWORD";
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> {
            pieceService.handleEscapeWithEscapeWord(piece, game, square, bag);
        });
        assertEquals("You cannot escape from this square", exception.getReason());
    }

    @Test
    @Transactional

    void shouldProcessEscapeAttemptSuccessfully() {
        GameSession game = new GameSession();
        game.setId(1);
        game.setStatus("IN_PROGRESS");

        User user = new User();
        user.setId(1);

        Piece piece = new Piece();
        piece.setId(1);
        piece.setStrength(3);
        piece.setActionPoints(2);
        piece.setUser(user);
        piece.setGame(game);

        Integer diceResult = pieceService.processEscapeAttempt(piece, game);

        assertNotNull(diceResult);
        assertTrue(diceResult >= 1 && diceResult <= 6);
        if (diceResult < piece.getStrength()) {
            assertEquals("FINISHED", game.getStatus());
            assertNotNull(game.getEnd());
            assertEquals(piece.getUser(), game.getWinner());
        } else {
            assertEquals(0, piece.getActionPoints());
            assertEquals(4, piece.getStrength());
        }
    }

    @Test
    @Transactional

    void shouldEndGameSuccessfully() {
        GameSession game = new GameSession();
        game.setId(1);
        game.setStatus("IN_PROGRESS");

        User user = new User();
        user.setId(1);

        Piece piece = new Piece();
        piece.setId(1);
        piece.setStrength(3);
        piece.setActionPoints(2);
        piece.setUser(user);
        piece.setGame(game);

        pieceService.endGameSuccessfully(game, piece);

        assertEquals("FINISHED", game.getStatus());
        assertNotNull(game.getEnd());
        assertEquals(piece.getUser(), game.getWinner());
    }

    @Test
    void testDiscardAfterLosing_ThrowsIfLoserNotInFight() {
        Piece loser = new Piece();
        loser.setId(1);
        loser.setHand(new ArrayList<>());

        Fight fight = new Fight();
        fight.setAttacker(new Piece());
        fight.setDefender(new Piece());

        GameSession game = new GameSession();
        game.setCurrenFight(fight);
        loser.setGame(game);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pieceService.discardAfterLosing(loser, 1, true);
        });

        assertEquals("Loser must be either attacker or defender", exception.getMessage());
    }


    @Test
    void testDiscardAfterLosing_ThrowsIfHandHasNoCards() {
        Piece loser = new Piece();
        loser.setId(1);
        loser.setHand(new ArrayList<>());

        Fight fight = new Fight();
        fight.setAttacker(loser);
        fight.setDefender(new Piece());

        GameSession game = new GameSession();
        game.setCurrenFight(fight);
        loser.setGame(game);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pieceService.discardAfterLosing(loser, 1, true);
        });

        assertEquals("Hand must have at least one card", exception.getMessage());
    }

    @Test
    void testStealFromOtherPlayerBag_ThrowsIfWinnerNotInFight() {
        Piece winner = new Piece();
        winner.setId(1);

        Piece loser = new Piece();
        loser.setId(2);
        loser.setBag(new ArrayList<>());

        Fight fight = new Fight();
        fight.setAttacker(new Piece());
        fight.setDefender(new Piece());

        GameSession game = new GameSession();
        game.setCurrenFight(fight);
        winner.setGame(game);
        loser.setGame(game);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pieceService.stealFromOtherPlayerBag(winner, loser, 1);
        });

        assertEquals("Winner must be either attacker or defender", exception.getMessage());
    }



    @Test
    void testStealFromOtherPlayerHand_ThrowsIfWinnerNotInFight() {
        Piece winner = new Piece();
        winner.setId(1);

        Piece loser = new Piece();
        loser.setId(2);
        loser.setHand(new ArrayList<>());

        Fight fight = new Fight();
        fight.setAttacker(new Piece());
        fight.setDefender(new Piece());

        GameSession game = new GameSession();
        game.setCurrenFight(fight);
        winner.setGame(game);
        loser.setGame(game);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            pieceService.stealFromOtherPlayerHand(winner, loser);
        });

        assertEquals("Winner must be either attacker or defender", exception.getMessage());
    }
}