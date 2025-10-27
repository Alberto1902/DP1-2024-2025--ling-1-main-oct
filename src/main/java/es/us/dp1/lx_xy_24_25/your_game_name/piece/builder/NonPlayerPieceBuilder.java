package es.us.dp1.lx_xy_24_25.your_game_name.piece.builder;

import org.jpatterns.gof.BuilderPattern;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;

@BuilderPattern.ConcreteBuilder(participants = {Piece.class})
public class NonPlayerPieceBuilder extends AbstractPieceBuilder {

    @Override
    public Piece build() {
        Piece piece = new Piece();
        piece.setGame(game);
        piece.setImage("/piecenpc.png");
        piece.setIsCampbell(false);
        piece.setPosition(null);
        piece.setUser(null);
        return piece;
    }
}
