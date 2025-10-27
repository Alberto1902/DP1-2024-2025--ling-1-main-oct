package es.us.dp1.lx_xy_24_25.your_game_name.decks;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceState;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
public class DeckServiceSolitaryTest {

    private DeckService deckService;

    @Mock
    private DeckRepository deckRepository;

    @Mock
    private CardService cardService;

    @Mock
    private PieceService pieceService;

    @Mock
    private GameSessionService gameSessionService;

    private Deck deck;
    private GameSession gameSession;

    @BeforeEach
    public void setUp() {
        deckService = new DeckService(deckRepository, cardService, pieceService, gameSessionService);

        deck = new Deck();
        deck.setId(1);
        deck.setIsDiscard(false);
        deck.setCards(new ArrayList<>());

        gameSession = new GameSession();
        gameSession.setId(1);
        deck.setGameSession(gameSession);
        MockitoAnnotations.openMocks(this);
        deckService = new DeckService(deckRepository, cardService, pieceService, gameSessionService);
    }

    @Test
    public void updateExistingDeck() {
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenReturn(deck);

        deck.setIsDiscard(true);
        Deck updatedDeck = deckService.updateDeck(deck, deck.getId());

        assertNotNull(updatedDeck);
        assertTrue(updatedDeck.getIsDiscard());
        verify(deckRepository, times(1)).findById(deck.getId());
        verify(deckRepository, times(1)).save(deck);
    }

    @Test
    public void failToUpdateNonExistentDeck() {
        when(deckRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> deckService.updateDeck(deck, 999));
        verify(deckRepository, times(1)).findById(999);
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    public void findDeckByIdSuccessfully() {
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));

        Deck foundDeck = deckService.findDeck(deck.getId());

        assertNotNull(foundDeck);
        assertEquals(deck.getId(), foundDeck.getId());
        verify(deckRepository, times(1)).findById(deck.getId());
    }

    @Test
    public void findDeckByIdReturnsNullIfNotFound() {
        when(deckRepository.findById(anyInt())).thenReturn(Optional.empty());

        Deck foundDeck = deckService.findDeck(999);

        assertNull(foundDeck);
        verify(deckRepository, times(1)).findById(999);
    }

    @Test
    void shouldCreateDeckSuccessfully() {
        GameSession gameSession = new GameSession();
        gameSession.setId(1);

        when(deckRepository.findByIsDiscardAndGameSession(anyBoolean(), any(GameSession.class)))
                .thenReturn(new ArrayList<>());
        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));

        Deck createdDeck = deckService.createDeck(false, gameSession);

        assertNotNull(createdDeck);
        assertEquals(gameSession, createdDeck.getGameSession());
        verify(deckRepository, times(1)).save(any(Deck.class));
    }

    @Test
    void shouldFailToCreateDuplicateDeck() {
        GameSession gameSession = new GameSession();
        gameSession.setId(1);

        Deck existingDeck = new Deck();
        existingDeck.setId(1);
        existingDeck.setIsDiscard(false);
        existingDeck.setGameSession(gameSession);

        when(deckRepository.findByIsDiscardAndGameSession(false, gameSession)).thenReturn(List.of(existingDeck));

        assertThrows(IllegalArgumentException.class, () -> deckService.createDeck(false, gameSession));
        verify(deckRepository, never()).save(any(Deck.class));
    }

    @Test
    void shouldShuffleDeckSuccessfully() {
        Deck deck = new Deck();
        deck.setId(1);

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

        deck.setCards(new ArrayList<>(List.of(card1, card2, card3)));

        // Mock findById
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        // Mock save
        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));

        // Llamar al método
        deckService.shuffle(deck);

        // Verificar
        assertNotNull(deck.getCards());
        assertEquals(3, deck.getCards().size());
        verify(deckRepository, times(1)).save(deck);
    }

    @Test
    void shouldTakeCardSuccessfully() {
        Deck deck = new Deck();
        deck.setId(1);
        deck.setIsDiscard(false);

        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");
        card1.setId(1);

        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");
        card2.setId(2);

        deck.setCards(new ArrayList<>(List.of(card1, card2)));

        Piece piece = new Piece();
        PieceState state = mock(PieceState.class);
        when(state.getStateType()).thenReturn("DRAW");
        piece.setState(state);

        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));

        Card takenCard = deckService.takeCard(deck, piece);

        assertNotNull(takenCard);
        assertEquals("A", takenCard.getLetter());
        assertEquals(1, deck.getCards().size());
        verify(deckRepository, times(1)).save(deck);
    }

    @Test
    void shouldReshuffleDeckSuccessfully() {
        Deck deck = new Deck();
        deck.setId(1);

        Deck discardDeck = new Deck();
        discardDeck.setId(2);

        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");
        card1.setId(1);

        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");
        card2.setId(2);

        deck.setCards(new ArrayList<>(List.of(card1)));
        discardDeck.setCards(new ArrayList<>(List.of(card2)));

        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.findById(discardDeck.getId())).thenReturn(Optional.of(discardDeck));

        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));

        deckService.reshuffle(deck, discardDeck);

        assertEquals(2, deck.getCards().size());
        assertEquals(0, discardDeck.getCards().size());
        verify(deckRepository, times(2)).save(any(Deck.class));
    }

    @Test
    void shouldDiscardCardsSuccessfully() {
        GameSession game = new GameSession();
        game.setId(1);
        game.setTurn(0);
        game.setPlayers(List.of(new User(), new User()));

        Piece piece = new Piece();
        piece.setId(1);
        piece.setGame(game);

        PieceState state = mock(PieceState.class);
        when(state.discard()).thenReturn(piece);
        piece.setState(state);

        Deck deck = new Deck();
        deck.setId(1);
        deck.setIsDiscard(true);
        deck.setCards(new ArrayList<>());

        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");

        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");

        List<Card> cardsToDiscard = List.of(card1, card2);

        when(deckRepository.findByIsDiscardAndGameSession(true, game)).thenReturn(List.of(deck));
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));
        when(pieceService.updatePiece(any(Piece.class), anyInt())).thenReturn(piece);
        when(gameSessionService.updateGameSession(any(GameSession.class), anyInt())).thenReturn(game);

        deckService.discard(piece, cardsToDiscard);

        assertEquals(2, deck.getCards().size());
        assertTrue(deck.getCards().contains(card1));
        assertTrue(deck.getCards().contains(card2));
        verify(deckRepository, times(1)).save(deck);
        verify(pieceService, times(1)).updatePiece(any(Piece.class), eq(piece.getId()));
        verify(gameSessionService, times(1)).updateGameSession(game, game.getId());
    }

    @Test
    void shouldDiscardCardToDeckSuccessfully() {
        Deck deck = new Deck();
        deck.setId(1);
        deck.setCards(new ArrayList<>());

        Card card = new Card();
        card.setLetter("A");
        card.setTitle("Card A");

        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));

        deckService.discard(deck, card);

        assertEquals(1, deck.getCards().size());
        assertEquals("A", deck.getCards().get(0).getLetter());
        verify(deckRepository, times(1)).save(deck);
    }

    @Test
    void shouldGetInitialCardsSuccessfully() {
        GameSession gameSession = new GameSession();
        gameSession.setId(1);

        Deck deck = new Deck();
        deck.setId(1);

        Card card1 = new Card();
        card1.setLetter("A");
        card1.setTitle("Card A");

        Card card2 = new Card();
        card2.setLetter("B");
        card2.setTitle("Card B");

        Card card3 = new Card();
        card3.setLetter("C");
        card3.setTitle("Card C");

        Card card4 = new Card();
        card4.setLetter("D");
        card4.setTitle("Card D");

        deck.setCards(new ArrayList<>(List.of(card1, card2, card3, card4)));

        when(deckRepository.findByIsDiscardAndGameSession(false, gameSession)).thenReturn(List.of(deck));
        when(deckRepository.findById(deck.getId())).thenReturn(Optional.of(deck));
        when(deckRepository.save(any(Deck.class))).thenAnswer(i -> i.getArgument(0));

        List<Card> initialCards = deckService.getInitialCards(gameSession);

        assertEquals(3, initialCards.size());
        assertEquals("A", initialCards.get(0).getLetter());
        assertEquals(1, deck.getCards().size());
        verify(deckRepository, times(1)).save(deck);
    }

}
