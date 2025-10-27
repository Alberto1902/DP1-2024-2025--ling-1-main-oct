package es.us.dp1.lx_xy_24_25.your_game_name.piece.builder;

import org.jpatterns.gof.BuilderPattern;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.DrawState;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceState;

@BuilderPattern.ConcreteBuilder(participants = {Piece.class})
public class PlayerPieceBuilder extends AbstractPieceBuilder{

    @Override
    public Piece build() {
        Piece piece = new Piece();
        piece.setGame(game);
        piece.setImage(image);
        piece.setIsCampbell(false);
        piece.setUser(user);
        piece.setPlayerOrder(playerOrder);
        piece.setWord("");
        piece.setPosition(null);
        PieceState initialState = new DrawState();
        piece.setState(initialState);
        initialState.setPiece(piece);
        return piece;
    }
}
