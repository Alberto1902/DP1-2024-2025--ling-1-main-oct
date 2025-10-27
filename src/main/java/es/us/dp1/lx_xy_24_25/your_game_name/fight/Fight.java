package es.us.dp1.lx_xy_24_25.your_game_name.fight;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Fight extends BaseEntity {

    @NotNull
    @ManyToOne
    private Piece attacker;

    private Integer AttackerDiceValue = null;

    private Integer DefenderDiceValue = null;

    @NotNull
    @ManyToOne
    private Piece defender;

    @ManyToOne
    private Piece winner = null;

    @NotNull
    private Boolean canTakeDiscardPile = false;

    // TRUE = attacker takes, FALSE = defender takes, NULL = not applicable
    public Boolean takeFromOtherPlayer;

    public void setWinner(Piece winner) {
        if (winner != null && winner != attacker && winner != defender) {
            throw new IllegalArgumentException("Winner must be either attacker, defender, or null");
        }
        this.winner = winner;
    }

    public Piece getLoser() {
        if (winner == null) {
            return null;
        } else if (winner.equals(attacker)) {
            return defender;
        } else {
            return attacker;
        }
    }

}
