package es.us.dp1.lx_xy_24_25.your_game_name.decks;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;

@Entity
@Getter
@Setter
public class Deck extends BaseEntity {

    @ManyToMany
    private List<Card> cards;
    
    @NotNull
    private Boolean isDiscard;

    @ManyToOne
    private GameSession gameSession;
    
}
