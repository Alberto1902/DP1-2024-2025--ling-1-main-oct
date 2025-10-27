package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import java.util.List;
import java.util.Map;
import org.jpatterns.gof.StatePattern;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("DRAW")
@StatePattern.ConcreteState
public class DrawState extends PieceState {

    public Piece receiveCard(Card card) {
        List<Card> cards = piece.getHand();
        if (cards.size() >= 7) {
            throw new IllegalArgumentException("Hand is full");
        } else {
            List<Card> updatedCards = cards;
            updatedCards.add(card);
            piece.setHand(updatedCards);
        }
        return piece;
    }

    public Piece defineActionPoints() {
        Integer numberOfCards = piece.getHand().size();
        Integer actionPoints = 7 - numberOfCards;
        piece.setActionPoints(actionPoints);
        PieceState ns = nextState();
        piece.setState(ns);
        return piece;
    }

    public Piece movePiece(Square square, Piece movedPiece) {
        throw new IllegalArgumentException("You are not allowed to move a piece in this state");
    }

    public Piece putCardsInBag(List<Card> cards) {
        throw new IllegalArgumentException("You are not allowed to put cards in the bag in this state");
    }

    public Map<Piece, List<Card>>  discardCards(String word) {
        throw new IllegalArgumentException("You are not allowed to discard cards in this state");
    }

    public Piece discard() {
        throw new IllegalArgumentException("You are not allowed to discard in this state");
    }

    public Piece endActionPhase() {
        throw new IllegalArgumentException("You are not allowed to end the action phase in this state");
    }

    public PieceState nextState() {
        if(piece.getHand().size() < 7) {
            return new ActionState();
        } else {
            return new BagState();
        }
    }
}
