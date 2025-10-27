package es.us.dp1.lx_xy_24_25.your_game_name.fight;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@ExtendWith(MockitoExtension.class)
class FightServiceTests {

    @Mock
    private FightRepository fightRepository;

    @Mock
    private PieceService pieceService;

    @Mock
    private GameSessionService gameSessionService;

    @InjectMocks
    private FightService fightService;

    private Piece npc;
    private Piece campbell;
    private Piece player, player2;
    private GameSession game;
    private User userPlayer, userPlayer2;

    @BeforeEach
    void setUpSpy() {
        fightService = spy(fightService);
    }

    @BeforeEach
    void setUp() {
        npc = new Piece();
        npc.setId(1);
        npc.setStrength(5);
        npc.setPosition(new Square("TestSquare", 1, 1));
        npc.setIsCampbell(false);

        campbell = new Piece();
        campbell.setId(2);
        campbell.setStrength(4);
        campbell.setPosition(new Square("TestSquare", 1, 1));
        campbell.setIsCampbell(true);

        userPlayer = new User();
        userPlayer.setId(1);

        userPlayer2 = new User();
        userPlayer2.setId(2);

        player = new Piece();
        player.setId(3);
        player.setUser(userPlayer);
        player.setStrength(6);
        player.setPosition(new Square("TestSquare", 1, 1));
        player.setIsCampbell(false);

        player2 = new Piece();
        player2.setId(4);
        player2.setUser(userPlayer2);
        player2.setStrength(5);
        player2.setPosition(new Square("TestSquare", 1, 1));
        player2.setIsCampbell(false);

        game = new GameSession();
        game.setId(1);
    }

    @Test
    void testFightThrowsExceptionWhenPiecesAreNotInSamePosition() {
        campbell.setPosition(new Square("DifferentSquare", 2, 2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fightService.fight(npc, campbell, game);
        });

        assertEquals("A fight takes place only in one square", exception.getMessage());
    }

    @Test
    void testFightThrowsExceptionWhenPiecesAreInSafeArea() {
        npc.setPosition(new Square("Safe Area", 1, 1));
        campbell.setPosition(new Square("Safe Area", 1, 1));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fightService.fight(npc, campbell, game);
        });

        assertEquals("A fight cannot take place in the Safe Area", exception.getMessage());
    }

    @Test
    void testSetWinnerWithInvalidPieceThrowsException() {
        Fight fight = new Fight();
        fight.setAttacker(npc);
        fight.setDefender(campbell);

        Piece invalidPiece = new Piece();
        invalidPiece.setId(4);

        assertThrows(IllegalArgumentException.class, () -> fight.setWinner(invalidPiece));
    }

    @Test
    void testSetWinnerWithValidPiece() {
        Fight fight = new Fight();
        fight.setAttacker(npc);
        fight.setDefender(campbell);

        fight.setWinner(npc);
        assertEquals(npc, fight.getWinner());
    }

    @Test
    void testGetLoserWhenWinnerIsAttacker() {
        Fight fight = new Fight();
        fight.setAttacker(npc);
        fight.setDefender(campbell);
        fight.setWinner(npc);

        assertEquals(campbell, fight.getLoser());
    }

    @Test
    void testGetLoserWhenWinnerIsDefender() {
        Fight fight = new Fight();
        fight.setAttacker(npc);
        fight.setDefender(campbell);
        fight.setWinner(campbell);

        assertEquals(npc, fight.getLoser());
    }

    @Test
    void testGetLoserWhenNoWinner() {
        Fight fight = new Fight();
        fight.setAttacker(npc);
        fight.setDefender(campbell);

        assertNull(fight.getLoser());
    }

    @Test
    void testValidateFightConditions() {

        assertDoesNotThrow(() -> fightService.validateFightConditions(npc, player));
    }

    @Test
    void testRollDiceGeneratesValidValue() {
        int diceValue = fightService.rollDice();
        assertTrue(diceValue >= 1 && diceValue <= 6);
    }

    @Test
    void testResetPieceStates() {
        npc.setIsFighting(true);
        npc.setIsAttacking(true);

        campbell.setIsFighting(true);
        campbell.setIsDefending(true);

        fightService.resetPieceStates(npc, campbell);

        assertFalse(npc.getIsFighting(), "NPC should not be fighting");
        assertFalse(npc.getIsAttacking(), "NPC should not be attacking");

        assertFalse(campbell.getIsFighting(), "Campbell should not be fighting");
        assertFalse(campbell.getIsDefending(), "Campbell should not be defending");

        verify(pieceService, times(1)).updatePiece(npc, npc.getId());
        verify(pieceService, times(1)).updatePiece(campbell, campbell.getId());
    }

    @Test
    void testAdjustDefenderStateAfterLoss() {
        game.setTurn(1);
        game.setCurrentPlayers(4);
        npc.setActionPoints(3);
        player.setStrength(5);

        fightService.adjustDefenderStateAfterLoss(player, game);

        assertEquals(0, player.getActionPoints(), "Defender's action points should be set to 0");
        assertEquals(6, player.getStrength(), "Defender's strength should increase by 1");

        verify(fightService, times(1)).setNextStateAndTurn(player, game);
    }

    @Test
    void testAdjustAttackerStateAfterLoss() {
        game.setTurn(1);
        game.setCurrentPlayers(4);
        npc.setActionPoints(3);
        player.setStrength(5);

        fightService.adjustAttackerStateAfterLoss(player, game);

        assertEquals(0, player.getActionPoints(), "Defender's action points should be set to 0");
        assertEquals(6, player.getStrength(), "Defender's strength should increase by 1");

        verify(fightService, times(1)).setNextStateAndTurn(player, game);
    }

    @Test
    void testHandleNpcVsPlayerWhenAttackerWins() {
        game.setTurn(1);
        npc.setStrength(5);
        player.setStrength(6);

        fightService.handleNpcVsPlayer(npc, player, npc, game);

        assertEquals(5, npc.getStrength(), "La fuerza del atacante no debe cambiar cuando gana");
        verify(fightService, times(1)).adjustDefenderStateAfterLoss(player, game);
    }

    @Test
    void testHandleNpcVsPlayerWhenDefenderWins() {
        game.setTurn(1);
        npc.setStrength(5);
        player.setStrength(6);

        fightService.handleNpcVsPlayer(npc, player, player, game);

        assertEquals(6, npc.getStrength(), "La fuerza del atacante debe incrementarse cuando el defensor gana");
        verify(fightService, times(0)).adjustDefenderStateAfterLoss(player, game);
    }

    @Test
    void testHandlePlayerVsNpcWhenDefenderWins() {
        game.setTurn(1);
        npc.setStrength(4);
        player.setStrength(5);

        fightService.handlePlayerVsNpc(player, npc, npc, game);

        assertEquals(4, npc.getStrength(), "La fuerza del defensor no debe cambiar cuando gana");
        verify(fightService, times(1)).adjustAttackerStateAfterLoss(player, game);
    }

    @Test
    void testHandlePlayerVsNpcWhenAttackerWins() {
        game.setTurn(1);
        npc.setStrength(4);
        player.setStrength(5);

        fightService.handlePlayerVsNpc(player, npc, player, game);

        assertEquals(5, npc.getStrength(), "La fuerza del defensor debe incrementarse cuando el atacante gana");
        verify(fightService, times(0)).adjustAttackerStateAfterLoss(player, game);
    }

    @Test
    void testHandleCampbellVsPlayerWhenAttackerWins() {
        game.setTurn(1);
        campbell.setStrength(5);
        player.setStrength(6);

        fightService.handleCampbellVsPlayer(campbell, player, campbell, game);

        assertEquals(5, campbell.getStrength(), "La fuerza del atacante no debe cambiar cuando gana");
        verify(fightService, times(1)).adjustDefenderStateAfterLoss(player, game);
    }

    @Test
    void testHandleCampbellVsPlayerWhenDefenderWins() {
        game.setTurn(1);
        campbell.setStrength(5);
        player.setStrength(6);

        fightService.handleCampbellVsPlayer(campbell, player, player, game);

        assertEquals(6, campbell.getStrength(), "La fuerza del atacante debe incrementarse cuando el defensor gana");
        verify(fightService, times(0)).adjustDefenderStateAfterLoss(player, game);
    }

    @Test
    void testHandlePlayerVsCampbellWhenDefenderWins() {
        game.setTurn(1);
        player.setStrength(6);
        campbell.setStrength(5);

        Fight fight = new Fight();
        fight.setAttacker(player);
        fight.setDefender(campbell);

        fightService.handlePlayerVsCampbell(player, campbell, campbell, game, fight);

        assertEquals(5, campbell.getStrength(), "La fuerza del defensor no debe cambiar cuando gana");
        assertFalse(fight.getCanTakeDiscardPile(), "No debería poder tomar el descarte si el defensor gana");
        verify(fightService, times(1)).adjustAttackerStateAfterLoss(player, game);
    }

    @Test
    void testHandlePlayerVsCampbellWhenAttackerWins() {
        game.setTurn(1);
        player.setStrength(6);
        campbell.setStrength(5);

        Fight fight = new Fight();
        fight.setAttacker(player);
        fight.setDefender(campbell);

        fightService.handlePlayerVsCampbell(player, campbell, player, game, fight);

        assertEquals(6, campbell.getStrength(), "La fuerza del defensor debe incrementarse cuando el atacante gana");
        assertTrue(fight.getCanTakeDiscardPile(), "Debería poder tomar el descarte si el atacante gana");
        verify(fightService, times(0)).adjustAttackerStateAfterLoss(player, game);
    }

    @Test
    void testHandlePlayerVsPlayerWhenDefenderWins() {
        game.setTurn(1);
        player.setStrength(6);
        player2.setStrength(5);

        Fight fight = new Fight();
        fight.setAttacker(player);
        fight.setDefender(player2);

        fightService.handlePlayerVsPlayer(player, player2, player2, game, fight);

        assertEquals(5, player2.getStrength(), "La fuerza del defensor no debe cambiar cuando gana");
        assertFalse(fight.getTakeFromOtherPlayer(),
                "La bandera takeFromOtherPlayer debería ser false cuando el defensor gana");
        verify(fightService, times(1)).adjustAttackerStateAfterLoss(player, game);
    }

    @Test
    void testHandlePlayerVsPlayerWhenAttackerWins() {
        game.setTurn(1);
        player.setStrength(6);
        player2.setStrength(5);

        Fight fight = new Fight();
        fight.setAttacker(player);
        fight.setDefender(player2);

        fightService.handlePlayerVsPlayer(player, player2, player, game, fight);

        assertEquals(6, player2.getStrength(),
                "La fuerza del defensor debe incrementarse en 1 cuando el atacante gana");
        assertTrue(fight.getTakeFromOtherPlayer(),
                "La bandera takeFromOtherPlayer debería ser true cuando el atacante gana");
        verify(fightService, times(0)).adjustAttackerStateAfterLoss(player, game);
    }

    @Test
    void testResolveFightOutcomeWhenPlayerVsPlayer() {
        player.setUser(userPlayer);
        player2.setUser(userPlayer2);
        Fight fight = new Fight();

        fightService.resolveFightOutcome(player, player2, player, game, fight);

        verify(fightService, times(1)).handlePlayerVsPlayer(player, player2, player, game, fight);
        verify(fightService, times(0)).handlePlayerVsCampbell(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handleCampbellVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handlePlayerVsNpc(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handleNpcVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testResolveFightOutcomeWhenPlayerVsCampbell() {
        player.setUser(userPlayer);
        campbell.setIsCampbell(true);
        Fight fight = new Fight();

        fightService.resolveFightOutcome(player, campbell, campbell, game, fight);

        verify(fightService, times(1)).handlePlayerVsCampbell(player, campbell, campbell, game, fight);
        verify(fightService, times(0)).handlePlayerVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handleCampbellVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handlePlayerVsNpc(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handleNpcVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testResolveFightOutcomeWhenCampbellVsPlayer() {
        campbell.setIsCampbell(true);
        player.setUser(userPlayer);
        Fight fight = new Fight();

        fightService.resolveFightOutcome(campbell, player, campbell, game, fight);

        verify(fightService, times(1)).handleCampbellVsPlayer(campbell, player, campbell, game);
        verify(fightService, times(0)).handlePlayerVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handlePlayerVsCampbell(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handlePlayerVsNpc(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handleNpcVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testResolveFightOutcomeWhenPlayerVsNpc() {
        player.setUser(userPlayer);
        npc.setUser(null);
        Fight fight = new Fight();

        fightService.resolveFightOutcome(player, npc, player, game, fight);

        verify(fightService, times(1)).handlePlayerVsNpc(player, npc, player, game);
        verify(fightService, times(0)).handlePlayerVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handlePlayerVsCampbell(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handleCampbellVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handleNpcVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testResolveFightOutcomeWhenNpcVsPlayer() {
        npc.setUser(null); // NPC no tiene usuario
        player.setUser(userPlayer);
        Fight fight = new Fight();

        fightService.resolveFightOutcome(npc, player, npc, game, fight);

        verify(fightService, times(1)).handleNpcVsPlayer(npc, player, npc, game);
        verify(fightService, times(0)).handlePlayerVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handlePlayerVsCampbell(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any());
        verify(fightService, times(0)).handleCampbellVsPlayer(Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any());
        verify(fightService, times(0)).handlePlayerVsNpc(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any());
    }

    @Test
    void testFightWhenAttackerWins() {
        npc.setStrength(5);
        player.setStrength(6);
        Fight fight = new Fight();

        fight.setAttacker(npc);
        fight.setDefender(player);

        Mockito.doNothing().when(fightService).validateFightConditions(npc, player);
        Mockito.when(fightService.initializeFight(npc, player)).thenReturn(fight);
        Mockito.when(fightService.rollDice()).thenReturn(4, 2); // Attacker wins
        Mockito.when(fightService.calculateScore(4, npc)).thenReturn(9); // 5 (strength) + 4 (dice)
        Mockito.when(fightService.calculateScore(2, player)).thenReturn(8); // 6 (strength) + 2 (dice)
        Mockito.doNothing().when(fightService).resolveFightOutcome(npc, player, npc, game, fight);
        Mockito.doNothing().when(fightService).resetPieceStates(npc, player);
        Mockito.when(fightService.saveFight(fight)).thenReturn(fight);
        Mockito.doNothing().when(fightService).updateGameSession(fight, game);

        Fight result = fightService.fight(npc, player, game);

        assertEquals(npc, result.getWinner(), "El atacante debería ser el ganador");
        assertEquals(4, result.getAttackerDiceValue(), "El valor del dado del atacante debería ser 4");
        assertEquals(2, result.getDefenderDiceValue(), "El valor del dado del defensor debería ser 2");

        verify(fightService, times(1)).validateFightConditions(npc, player);
        verify(fightService, times(1)).initializeFight(npc, player);
        verify(fightService, times(2)).rollDice(); // Dos veces por atacante y defensor
        verify(fightService, times(1)).calculateScore(4, npc);
        verify(fightService, times(1)).calculateScore(2, player);
        verify(fightService, times(1)).resolveFightOutcome(npc, player, npc, game, fight);
        verify(fightService, times(1)).resetPieceStates(npc, player);
        verify(fightService, times(1)).saveFight(fight);
        verify(fightService, times(1)).updateGameSession(fight, game);
    }

    @Test
    void testFightWhenDefenderWins() {
        npc.setStrength(5); 
        player.setStrength(6);
        Fight fight = new Fight();

        fight.setAttacker(npc);
        fight.setDefender(player);

        Mockito.doNothing().when(fightService).validateFightConditions(npc, player);
        Mockito.when(fightService.initializeFight(npc, player)).thenReturn(fight);
        Mockito.when(fightService.rollDice()).thenReturn(2, 4); // Defensor gana
        Mockito.when(fightService.calculateScore(2, npc)).thenReturn(7); // 5 (fuerza) + 2 (dado)
        Mockito.when(fightService.calculateScore(4, player)).thenReturn(10); // 6 (fuerza) + 4 (dado)
        Mockito.doNothing().when(fightService).resolveFightOutcome(npc, player, player, game, fight);
        Mockito.doNothing().when(fightService).resetPieceStates(npc, player);
        Mockito.when(fightService.saveFight(fight)).thenReturn(fight);
        Mockito.doNothing().when(fightService).updateGameSession(fight, game);

        Fight result = fightService.fight(npc, player, game);

        assertEquals(player, result.getWinner(), "El defensor debería ser el ganador");
        assertEquals(2, result.getAttackerDiceValue(), "El valor del dado del atacante debería ser 2");
        assertEquals(4, result.getDefenderDiceValue(), "El valor del dado del defensor debería ser 4");

        verify(fightService, times(1)).validateFightConditions(npc, player);
        verify(fightService, times(1)).initializeFight(npc, player);
        verify(fightService, times(2)).rollDice(); // Dos veces por atacante y defensor
        verify(fightService, times(1)).calculateScore(2, npc);
        verify(fightService, times(1)).calculateScore(4, player);
        verify(fightService, times(1)).resolveFightOutcome(npc, player, player, game, fight);
        verify(fightService, times(1)).resetPieceStates(npc, player);
        verify(fightService, times(1)).saveFight(fight);
        verify(fightService, times(1)).updateGameSession(fight, game);
    }

    @Test
    public void testCountFightsWonByPiece() {
        Piece piece = mock(Piece.class);
        when(fightRepository.countByWinner(piece)).thenReturn(5);

        Integer result = fightService.countFightsWonByPiece(piece);

        assertEquals((Integer) 5, result);
        verify(fightRepository, times(1)).countByWinner(piece);
    }

    @Test
    public void testCountFightsLostByPiece() {
        Piece piece = mock(Piece.class);
        when(fightRepository.countByLoser(piece)).thenReturn(3);

        Integer result = fightService.countFightsLostByPiece(piece);

        assertEquals((Integer) 3, result);
        verify(fightRepository, times(1)).countByLoser(piece);
    }

    @Test
    public void testGetMostFoughtSquare() {
        Square square = mock(Square.class);
        when(fightRepository.mostFoughtSquare()).thenReturn(square);

        Square result = fightService.getMostFoughtSquare();

        assertEquals(square, result);
        verify(fightRepository, times(1)).mostFoughtSquare();
    }

}
