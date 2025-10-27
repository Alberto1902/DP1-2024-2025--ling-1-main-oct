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
@DiscriminatorValue("ACTION")
@StatePattern.ConcreteState
public class ActionState extends PieceState {

    public Piece receiveCard(Card card) {
        throw new IllegalArgumentException("You are not allowed to receive a card in this state");
    }

    public Piece defineActionPoints() {
        throw new IllegalArgumentException("You are not allowed to define action points in this state");
    }

    public Piece movePiece(Square square, Piece movedPiece) {
        movedPiece.setPosition(square);
        piece.setActionPoints(piece.getActionPoints() - 1);
        if(piece.getActionPoints() == 0){
            PieceState ns = nextState();
            piece.setState(ns);
        }
        return movedPiece;
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
        PieceState ns = nextState();
        piece.setState(ns);
        return piece;
    }

    public PieceState nextState() {
        if(piece.getHand().size() > 0) {
            return new BagState();
        } else {
            return new WordState();
        }
    }   
}
