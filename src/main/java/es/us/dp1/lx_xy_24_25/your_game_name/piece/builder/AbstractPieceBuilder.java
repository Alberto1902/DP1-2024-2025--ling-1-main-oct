package es.us.dp1.lx_xy_24_25.your_game_name.piece.builder;

import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceState;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

public abstract class AbstractPieceBuilder implements PieceBuilder {
    protected Integer playerOrder;
    protected String image;
    protected Boolean isCampbell;
    protected Square position;
    protected User user;
    protected GameSession game;
    protected String word;
    protected PieceState state;

    @Override
    public PieceBuilder withPlayerOrder(Integer playerOrder) {
        this.playerOrder = playerOrder;
        return this;
    }

    @Override
    public PieceBuilder withImage(String image) {
        this.image = image;
        return this;
    }

    @Override
    public PieceBuilder withPosition(Square position) {
        this.position = position;
        return this;
    }

    @Override
    public PieceBuilder withUser(User user) {
        this.user = user;
        return this;
    }

    @Override
    public PieceBuilder withGame(GameSession game) {
        this.game = game;
        return this;
    }
}
