package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.ManyToMany;
import lombok.Getter;
import lombok.Setter;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.ArrayList;
import java.util.List;
import org.jpatterns.gof.BuilderPattern;
import org.jpatterns.gof.StatePattern;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;

@Getter
@Setter
@Entity
@BuilderPattern.Product
@StatePattern.Context
public class Piece extends BaseEntity{

    @Min(0)
    @Max(6)
    private Integer playerOrder;

    private String word;

    @NotNull
    @Min(1)
    @Max(6)
    private Integer strength = 1;

    String image;

    @NotNull
    private Boolean isCampbell;

    @ManyToOne
    @JoinColumn(name = "position_id")
    private Square position;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "game_id")
    private GameSession game;

    @OneToOne(cascade = CascadeType.ALL)
    PieceState state;

    private Integer actionPoints = null;

    @ManyToMany
    List<Card> hand = new ArrayList<>();

    @ManyToMany
    List<Card> bag = new ArrayList<>();

    private Boolean isFighting = false;

    private Boolean isAttacking = false;

    private Boolean isDefending = false; 
}
