package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import java.util.List;
import java.util.Map;
import org.jpatterns.gof.StatePattern;
import com.fasterxml.jackson.annotation.JsonIgnore;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
@StatePattern.State
public abstract class PieceState{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @OneToOne(mappedBy = "state")
    @JsonIgnore
    protected Piece piece;

    public String getStateType() {
        return this.getClass().getAnnotation(DiscriminatorValue.class).value();
    }
    public abstract Piece receiveCard(Card card);
    public abstract Piece defineActionPoints();
    public abstract Piece movePiece(Square square, Piece movedPiece);
    public abstract Piece endActionPhase();
    public abstract Piece putCardsInBag(List<Card> cards);
    public abstract Map<Piece, List<Card>> discardCards(String word);
    public abstract Piece discard();

    public abstract PieceState nextState();
}
