package es.us.dp1.lx_xy_24_25.your_game_name.piece.builder;

import org.jpatterns.gof.BuilderPattern;
import org.springframework.stereotype.Component;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@Component
@BuilderPattern.Director(participants = {Piece.class, NonPlayerPieceBuilder.class, PlayerPieceBuilder.class, CampbellPieceBuilder.class})
public class PieceBuilderDirector {

    public PieceBuilder ofType(Boolean isCampbell, User user) {
        if(isCampbell) {
            return new CampbellPieceBuilder();
        } else if (user != null) {
            return new PlayerPieceBuilder();
        } else {
            return new NonPlayerPieceBuilder();
        }
    }  
}
