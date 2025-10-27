package es.us.dp1.lx_xy_24_25.your_game_name.fight;

import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.BagState;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.DrawState;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceState;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FightService {

    private final FightRepository fightRepository;
    private final PieceService pieceService;
    private final GameSessionService gameSessionService;

    @Autowired
    public FightService(FightRepository fightRepository, PieceService pieceService,
                         GameSessionService gameSessionService) {
        this.fightRepository = fightRepository;
        this.pieceService = pieceService;
        this.gameSessionService = gameSessionService;
    }

    @Transactional(readOnly = true)
    public Integer countFightsWonByPiece(Piece piece) {
        return fightRepository.countByWinner(piece);
    }

    @Transactional(readOnly = true)
    public Integer countFightsLostByPiece(Piece piece) {
        return fightRepository.countByLoser(piece);
    }

    @Transactional(readOnly = true)
    public Square getMostFoughtSquare() {
        return fightRepository.mostFoughtSquare();
    }

    @Transactional
    public Fight saveFight(Fight fight) {
        return fightRepository.save(fight);
    }

    @Transactional(rollbackFor = Exception.class)
    public Fight fight(Piece attacker, Piece defender, GameSession game) {
        validateFightConditions(attacker, defender);

        Fight fight = initializeFight(attacker, defender);

        int attackerDice, defenderDice, attackerScore, defenderScore;
        do {
            attackerDice = rollDice();
            defenderDice = rollDice();
            attackerScore = calculateScore(attackerDice, attacker);
            defenderScore = calculateScore(defenderDice, defender);
        } while (attackerScore == defenderScore);

        Piece winner = attackerScore > defenderScore ? attacker : defender;
        fight.setAttackerDiceValue(attackerDice);
        fight.setDefenderDiceValue(defenderDice);
        fight.setWinner(winner);

        resolveFightOutcome(attacker, defender, winner, game, fight);
        resetPieceStates(attacker, defender);

        Fight savedFight = saveFight(fight);
        updateGameSession(savedFight, game);

        return savedFight;
    }

    protected void validateFightConditions(Piece attacker, Piece defender) {
        if (!attacker.getPosition().equals(defender.getPosition())) {
            throw new IllegalArgumentException("A fight takes place only in one square");
        }
        if ("Safe Area".equals(defender.getPosition().getName())) {
            throw new IllegalArgumentException("A fight cannot take place in the Safe Area");
        }
        if (attacker.getUser() == null && defender.getUser() == null) {
            throw new IllegalArgumentException("Two NPCs cannot fight each other");
        }
    }

    protected Fight initializeFight(Piece attacker, Piece defender) {
        Fight fight = new Fight();
        fight.setAttacker(attacker);
        fight.setDefender(defender);
        return fight;
    }

    protected int rollDice() {
        return (int) (Math.random() * 6) + 1;
    }

    protected int calculateScore(int diceValue, Piece piece) {
        return diceValue + piece.getStrength();
    }

    protected void resolveFightOutcome(Piece attacker, Piece defender, Piece winner, GameSession game, Fight fight) {
        if (attacker.getUser() != null && defender.getUser() != null) {
            handlePlayerVsPlayer(attacker, defender, winner, game, fight);
        } else if (defender.getIsCampbell()) {
            handlePlayerVsCampbell(attacker, defender, winner, game, fight);
        } else if (attacker.getIsCampbell()) {
            handleCampbellVsPlayer(attacker, defender, winner, game);
        } else if (defender.getUser() == null) {
            handlePlayerVsNpc(attacker, defender, winner, game);
        } else {
            handleNpcVsPlayer(attacker, defender, winner, game);
        }
    }

    protected void handlePlayerVsPlayer(Piece attacker, Piece defender, Piece winner, GameSession game, Fight fight) {
        if (winner.equals(defender)) {
            adjustAttackerStateAfterLoss(attacker, game);
            fight.setTakeFromOtherPlayer(false);
        } else {
            defender.setStrength(defender.getStrength() + 1);
            fight.setTakeFromOtherPlayer(true);
        }
    }

    protected void handlePlayerVsCampbell(Piece attacker, Piece defender, Piece winner, GameSession game, Fight fight) {
        if (winner.equals(defender)) {
            adjustAttackerStateAfterLoss(attacker, game);
        } else {
            defender.setStrength(defender.getStrength() + 1);
            fight.setCanTakeDiscardPile(true);
        }
    }

    protected void handleCampbellVsPlayer(Piece attacker, Piece defender, Piece winner, GameSession game) {
        if (winner.equals(attacker)) {
            adjustDefenderStateAfterLoss(defender, game);
        } else {
            attacker.setStrength(attacker.getStrength() + 1);
        }
    }

    protected void handlePlayerVsNpc(Piece attacker, Piece defender, Piece winner, GameSession game) {
        if (winner.equals(defender)) {
            adjustAttackerStateAfterLoss(attacker, game);
        } else {
            defender.setStrength(defender.getStrength() + 1);
        }
    }

    protected void handleNpcVsPlayer(Piece attacker, Piece defender, Piece winner, GameSession game) {
        if (winner.equals(attacker)) {
            adjustDefenderStateAfterLoss(defender, game);
        } else {
            attacker.setStrength(attacker.getStrength() + 1);
        }
    }

    protected void adjustAttackerStateAfterLoss(Piece attacker, GameSession game) {
        attacker.setActionPoints(0);
        attacker.setStrength(attacker.getStrength() + 1);
        setNextStateAndTurn(attacker, game);
    }

    protected void adjustDefenderStateAfterLoss(Piece defender, GameSession game) {
        defender.setActionPoints(0);
        defender.setStrength(defender.getStrength() + 1);
        setNextStateAndTurn(defender, game);
    }

    protected void setNextStateAndTurn(Piece piece, GameSession game) {
        if (piece.getHand().size() + piece.getBag().size() >= 2) {
            changePieceState(piece, new BagState());
        } else {
            changePieceState(piece, new DrawState());
            advanceTurn(game);
        }
    }

    protected void changePieceState(Piece piece, PieceState newState) {
        piece.setState(newState);
        newState.setPiece(piece);
    }

    protected int advanceTurn(GameSession game) {
        game.setTurn((game.getTurn() + 1) % game.getCurrentPlayers());
        return game.getTurn();
    }

    protected void resetPieceStates(Piece attacker, Piece defender) {
        attacker.setIsFighting(false);
        attacker.setIsAttacking(false);

        defender.setIsFighting(false);
        defender.setIsDefending(false);

        pieceService.updatePiece(attacker, attacker.getId());
        pieceService.updatePiece(defender, defender.getId());
    }

    protected void updateGameSession(Fight savedFight, GameSession game) {
        game.setCurrenFight(savedFight);
        gameSessionService.updateGameSession(game, game.getId());
    }
}
