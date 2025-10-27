package es.us.dp1.lx_xy_24_25.your_game_name.decks;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.CardService;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;

@Service
public class DeckService {

    private DeckRepository deckRepository;
    private CardService cardService;
    private PieceService pieceService;
    private GameSessionService gameSessionService;

    @Autowired
    public DeckService(DeckRepository deckRepository, CardService cardService, PieceService pieceService, GameSessionService gameSessionService) {
        this.deckRepository = deckRepository;
        this.cardService = cardService;
        this.pieceService = pieceService;
        this.gameSessionService = gameSessionService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Deck createDeck(Boolean isDiscard, GameSession gameSession) {
        Deck existingDeck = findDecksByIsDiscardAndGameSession(isDiscard, gameSession);
        if(existingDeck != null) {
            throw new IllegalArgumentException("Deck already exists for that game session");
        }
        Deck newDeck = deckCreation(isDiscard, gameSession);
        deckRepository.save(newDeck);
        return newDeck;
    }

    @Transactional
    public Deck updateDeck(Deck deck, Integer id) {
        Deck updatedDeck = deckRepository.findById(id).get();
        updatedDeck.setCards(deck.getCards());
        updatedDeck.setIsDiscard(deck.getIsDiscard());
        updatedDeck.setGameSession(deck.getGameSession());
        deckRepository.save(updatedDeck);
        return updatedDeck;
    }

    @Transactional(readOnly = true)
    public Deck findDeck(Integer id) {
        return deckRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public Deck findDecksByIsDiscardAndGameSession(Boolean isDiscard, GameSession gameSession) {
        List<Deck> obtainedDecks = deckRepository.findByIsDiscardAndGameSession(isDiscard, gameSession);
        return obtainedDecks.size() > 0 ? obtainedDecks.get(0) : null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Card takeCard(Deck deck, Piece piece) {
        List<Card> cards = deck.getCards();
        Card card;
        List<Card> updatedCards;
        if (cards.size() == 0 || (piece!=null && !piece.getState().getStateType().equals("DRAW"))) {
            throw new IllegalArgumentException("No cards in deck or piece is not in DRAW state");
        }
        if(deck.getIsDiscard()) {
            card = cards.get(cards.size() - 1);
            updatedCards = cards.subList(0, cards.size() - 1);
        } else {
            card = cards.get(0);
            updatedCards = cards.subList(1, cards.size());
        }
        deck.setCards(updatedCards);
        updateDeck(deck, deck.getId());
        return card;
    }

    @Transactional
    public void shuffle(Deck deck) {
        List<Card> cards = deck.getCards();
        shuffleFunction(cards);
        deck.setCards(cards);
        updateDeck(deck, deck.getId());
    }

    @Transactional
    public void reshuffle(Deck deck, Deck discardDeck) {
        List<Card> cards = new ArrayList<>();
        List<Card> emptyList = new ArrayList<>();
        cards.addAll(deck.getCards());
        cards.addAll(discardDeck.getCards());
        shuffleFunction(cards);
        discardDeck.setCards(emptyList);
        updateDeck(discardDeck, discardDeck.getId());
        deck.setCards(cards);
        updateDeck(deck, deck.getId());
    }

    @Transactional
    public void discard(Piece piece, List<Card> cards) {
        GameSession game = piece.getGame();
        Deck deck = findDecksByIsDiscardAndGameSession(true, game);
        List<Card> discardedCards = deck.getCards();
        discardedCards.addAll(cards);
        deck.setCards(cards);
        updateDeck(deck, deck.getId());
        Piece newPiece = piece.getState().discard();
        pieceService.updatePiece(newPiece, piece.getId());
        game.setTurn((game.getTurn() + 1) % game.getPlayers().size());
        gameSessionService.updateGameSession(game, game.getId());
    }

    @Transactional
    public void discard(Deck deck, Card card) {
        List<Card> cards = deck.getCards();
        cards.add(card);
        deck.setCards(cards);
        updateDeck(deck, deck.getId());
    }

    @Transactional
    public List<Card> getInitialCards(GameSession gameSession) {
        List<Card> cards = new ArrayList<>();
        Deck deck = findDecksByIsDiscardAndGameSession(false, gameSession);
        cards.add(deck.getCards().get(0));
        cards.add(deck.getCards().get(1));
        cards.add(deck.getCards().get(2));
        List<Card> updatedCards = deck.getCards().subList(3, deck.getCards().size());
        deck.setCards(updatedCards);
        updateDeck(deck, deck.getId());
        return cards;
    }

    private Deck deckCreation(Boolean isDiscard, GameSession gameSession) {
        Deck newDeck = new Deck();
        newDeck.setCards(isDiscard ? new ArrayList<>() : cardService.findCards());
        newDeck.setIsDiscard(isDiscard);
        newDeck.setGameSession(gameSession);
        return newDeck;
    }

    private void shuffleFunction(List<Card> cards) {
        for (int i = 0; i < cards.size(); i++) {
            int randomIndex = (int) (Math.random() * cards.size());
            Card temp = cards.get(i);
            cards.set(i, cards.get(randomIndex));
            cards.set(randomIndex, temp);
        }
    }
    
}
