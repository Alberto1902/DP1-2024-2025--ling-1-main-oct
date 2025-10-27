package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import java.util.ArrayList;
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
@DiscriminatorValue("WORD")
@StatePattern.ConcreteState
public class WordState extends PieceState {

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
        throw new IllegalArgumentException("You are not allowed to put cards in the bag in this state");
    }

    public Map<Piece, List<Card>>  discardCards(String word) {
        String w = "";
        List<Card> cards = piece.getBag();
        List<Card> cardsInBag = new ArrayList<>();
        for(int j = 0; j < word.length(); j++){
            for (int i = 0; i < cards.size(); i++) {
                if (cards.get(i).getLetter().toLowerCase().equals(Character.toString(word.charAt(j)))) {
                    cardsInBag.add(cards.get(i));
                    cards.remove(i);
                    w = w + word.charAt(j);                   
                    break;
                }
            }
        }
        if(!w.equals(word)) {
            throw new IllegalArgumentException("Word cannot be formed with the letters in the bag");
        }
        piece.setBag(cardsInBag);
        piece.setWord(word);
        if(cards.size() == 0){
            PieceState ns = new DrawState();
            piece.setState(ns);
        }
        else{
            PieceState ns = nextState();
            piece.setState(ns);
        }
        return Map.of(piece, cards);
    }

    public Piece discard() {
        throw new IllegalArgumentException("You are not allowed to discard in this state");
    }

    public Piece endActionPhase() {
        throw new IllegalArgumentException("You are not allowed to end the action phase in this state");
    }

    public PieceState nextState() {
        return new DiscardState();
    }
}
