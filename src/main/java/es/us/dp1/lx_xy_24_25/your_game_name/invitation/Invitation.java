package es.us.dp1.lx_xy_24_25.your_game_name.invitation;

import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Column;

@Getter
@Setter
@Entity
public class Invitation extends BaseEntity {

    private boolean isSpectator;

    @Column(columnDefinition = "ENUM('PENDING', 'ACCEPTED', 'REJECTED')")
    private String status = "PENDING";
    
    @ManyToOne
    private GameSession game;

    @ManyToOne
    User sender;

    @ManyToOne
    User receiver;

}
