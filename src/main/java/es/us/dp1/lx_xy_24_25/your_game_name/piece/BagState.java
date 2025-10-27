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
@DiscriminatorValue("BAG")
@StatePattern.ConcreteState
public class BagState extends PieceState {

    public Piece receiveCard(Card card) {
        throw new IllegalArgumentException("You are not allowed to receive a card in this state");
    }

    public Piece defineActionPoints() {
        throw new IllegalArgumentException("You are not allowed to define action points in this state");
    }

    public Piece movePiece(Square square, Piece movedPiece) {
        throw new IllegalArgumentException("You are not allowed to move a piece in this state");
    }

    public Piece putCardsInBag(List<Card> cards) {
        List<Card> bag = piece.getBag();
        List<Card> hand = piece.getHand();
        List<Card> updatedHand = hand;
        List<Card> updatedBag = bag;
        if(cards.size() > 0){
            for (int i = 0; i < cards.size(); i++) {
                if(hand.contains(cards.get(i))){
                updatedBag.add(cards.get(i));
                updatedHand.remove(cards.get(i));
                }
            }
        }
        if(updatedHand.size() > 7){
            throw new IllegalArgumentException("You cannot have more than 7 cards in your hand");
        }
        if(updatedBag.size() < 2){
            throw new IllegalArgumentException("You must have at least 2 cards in your bag");
        }
        piece.setBag(updatedBag);
        piece.setHand(updatedHand);
        PieceState ns = nextState();
        piece.setState(ns);
        return piece;
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
        return new WordState();
    }
}
