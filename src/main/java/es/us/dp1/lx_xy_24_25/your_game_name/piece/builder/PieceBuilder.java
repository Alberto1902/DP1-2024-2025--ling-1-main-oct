package es.us.dp1.lx_xy_24_25.your_game_name.piece.builder;

import org.jpatterns.gof.BuilderPattern;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;

@BuilderPattern.Builder
public interface PieceBuilder {
    PieceBuilder withPlayerOrder(Integer playOrder);
    PieceBuilder withImage(String image);
    PieceBuilder withPosition(Square position);
    PieceBuilder withUser(User user);
    PieceBuilder withGame(GameSession game);
    Piece build();
}
