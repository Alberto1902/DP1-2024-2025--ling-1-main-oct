package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.us.dp1.lx_xy_24_25.your_game_name.cards.Card;
import es.us.dp1.lx_xy_24_25.your_game_name.fight.Fight;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;
import es.us.dp1.lx_xy_24_25.your_game_name.square.SquareService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;

@WebMvcTest(PieceRestController.class)
class PieceRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PieceService pieceService;

    @MockBean
    private GameSessionService gameSessionService;

    @MockBean
    private SquareService squareService;

    @MockBean
    private UserService userService;

    private Piece testPiece;
    private Square testSquare;
    private GameSession testGame;

    @Autowired
    private ObjectMapper objectMapper;
    private Card testCard;
    private List<Card> testCards;
    private Fight testFight;

    @BeforeEach
    void setup() {
        testPiece = new Piece();
        testPiece.setId(1);
        testPiece.setStrength(3);
        testPiece.setIsCampbell(false);

        testSquare = new Square();
        testSquare.setId(1);

        testGame = new GameSession();
        testGame.setId(1);

        testPiece.setGame(testGame);

        when(pieceService.receiveCard(any(Card.class), eq(testPiece))).thenReturn(testPiece);
        when(gameSessionService.findGameSession(testGame.getId())).thenReturn(testGame);
        when(pieceService.createAllPieces(testGame)).thenReturn(Collections.singletonList(testPiece));
        when(pieceService.findPiece(eq(1))).thenReturn(testPiece);
        when(pieceService.updatePiece(eq(testPiece), eq(1))).thenReturn(testPiece);

        testPiece = new Piece();
        testPiece.setId(1);

        testCard = new Card();
        testCard.setLetter("A");
        testCard.setTitle("Test Card");

        testCards = Collections.singletonList(testCard);
    }

    @Test
    @WithMockUser(username = "admin", roles = { "ADMIN" })
    void testCreateAllPieces() throws Exception {
        int gameId = 1;

        mockMvc.perform(post("/api/v1/pieces")
                .param("gameId", String.valueOf(gameId)).with(csrf())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].game.id").value(gameId));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testMovePiece_Success() throws Exception {
        when(pieceService.findPiece(eq(1))).thenReturn(testPiece);
        when(squareService.findSquare(eq(1))).thenReturn(testSquare);
        when(userService.findCurrentUser()).thenReturn(new User());
        when(pieceService.movePiece(eq(testSquare), eq(testPiece), any(User.class))).thenReturn(testPiece);

        mockMvc.perform(put("/api/v1/pieces/move").with(csrf())
                .param("position", "1")
                .param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testMovePiece_PieceNotFound() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(null);

        mockMvc.perform(put("/api/v1/pieces/move").with(csrf())
                .param("position", "1")
                .param("id", "1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testMovePiece_SquareNotFound() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(testPiece);
        when(squareService.findSquare(1)).thenReturn(null);

        mockMvc.perform(put("/api/v1/pieces/move").with(csrf())
                .param("position", "1")
                .param("id", "1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "testUser")
    void testGetPiecesByGameId_Success() throws Exception {
        when(pieceService.findPiecesByGameId(1)).thenReturn(Collections.singletonList(testPiece));

        mockMvc.perform(get("/api/v1/pieces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testCreateAllPieces_Success() throws Exception {
        when(gameSessionService.findGameSession(1)).thenReturn(testGame);
        when(pieceService.createAllPieces(testGame)).thenReturn(Arrays.asList(testPiece));

        mockMvc.perform(post("/api/v1/pieces").with(csrf())
                .param("gameId", "1"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testInitialPositionNonPlayer_Success() throws Exception {
        when(pieceService.setAllNonPlayerInitialPositions(eq(1))).thenReturn(Collections.singletonList(testPiece));

        mockMvc.perform(put("/api/v1/pieces/initialPositionNonPlayer/1")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser")
    void testReceiveCard_Success() throws Exception {
        Card testCard = new Card();
        testCard.setLetter("A");
        testCard.setTitle("Test Card");

        when(pieceService.receiveCard(eq(testCard), eq(testPiece))).thenReturn(testPiece);

        mockMvc.perform(put("/api/v1/pieces/receiveCard")
                .with(csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCard)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "PLAYER")
    void testReceiveInitialCards_Success() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(testPiece);
        when(pieceService.receiveInitialCards(testCards, testPiece)).thenReturn(testPiece);

        mockMvc.perform(put("/api/v1/pieces/receiveInitialCards")
                .with(csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCards)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "PLAYER")
    void testReceiveInitialCards_PieceNotFound() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(null);

        mockMvc.perform(put("/api/v1/pieces/receiveInitialCards")
                .with(csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCards)))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "testUser", roles = "PLAYER")
    void testReceiveCardAfterFight_Success() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(testPiece);
        when(pieceService.receiveCardAfterFight(testCard, testPiece)).thenReturn(testPiece);

        mockMvc.perform(put("/api/v1/pieces/receiveCardAfterFight")
                .with(csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCard)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "PLAYER")
    void testPutCardsInBag_Success() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(testPiece);
        when(pieceService.putCardsInBag(testCards, testPiece)).thenReturn(testPiece);

        mockMvc.perform(put("/api/v1/pieces/putCardsInBag")
                .with(csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testCards)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(username = "testUser", roles = "PLAYER")
    void testDiscardCards_PieceNotFound() throws Exception {
        when(pieceService.findPiece(1)).thenReturn(null);

        mockMvc.perform(put("/api/v1/pieces/discardCards")
                .with(csrf())
                .param("id", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("\"word\""))
                .andExpect(status().isNotFound());
    }

    private final String BASE_URL = "/api/v1/pieces";    

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testEscape() throws Exception {
        Piece piece = new Piece();
        when(pieceService.findPiece(1)).thenReturn(piece);
        when(pieceService.escape(piece)).thenReturn(6);

        mockMvc.perform(put(BASE_URL + "/escape")
                .param("id", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).findPiece(1);
        verify(pieceService).escape(piece);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testCatapulted() throws Exception {
        Piece piece = new Piece();
        when(pieceService.findPiece(1)).thenReturn(piece);
        when(pieceService.catapulted(piece)).thenReturn(piece);

        mockMvc.perform(put(BASE_URL + "/catapulted")
                .param("id", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).findPiece(1);
        verify(pieceService).catapulted(piece);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testFindPiecesInSquare() throws Exception {
        Square square = new Square();
        GameSession gameSession = new GameSession();
        List<Piece> pieces = List.of(new Piece());

        when(squareService.findSquare(1)).thenReturn(square);
        when(gameSessionService.findGameSession(1)).thenReturn(gameSession);
        when(pieceService.findPiecesInSquare(gameSession, square)).thenReturn(pieces);

        mockMvc.perform(get(BASE_URL + "/piecesInSquare")
                .param("gameSessionId", "1")
                .param("squareId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(squareService).findSquare(1);
        verify(gameSessionService).findGameSession(1);
        verify(pieceService).findPiecesInSquare(gameSession, square);
    }

    @Test
    void shouldSetWinnerCorrectly() {
        Piece attacker = new Piece();
        attacker.setId(1);

        Piece defender = new Piece();
        defender.setId(2);

        Fight fight = new Fight();
        fight.setAttacker(attacker);
        fight.setDefender(defender);

        fight.setWinner(attacker);
        assertEquals(attacker, fight.getWinner());

        fight.setWinner(defender);
        assertEquals(defender, fight.getWinner());

        fight.setWinner(null);
        assertNull(fight.getWinner());
    }

    @Test
    void shouldThrowExceptionWhenWinnerIsInvalid() {
        Piece attacker = new Piece();
        attacker.setId(1);

        Piece defender = new Piece();
        defender.setId(2);

        Piece invalidPiece = new Piece();
        invalidPiece.setId(3);

        Fight fight = new Fight();
        fight.setAttacker(attacker);
        fight.setDefender(defender);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fight.setWinner(invalidPiece);
        });
        assertEquals("Winner must be either attacker, defender, or null", exception.getMessage());
    }

    @Test
    void shouldReturnCorrectLoser() {
        Piece attacker = new Piece();
        attacker.setId(1);

        Piece defender = new Piece();
        defender.setId(2);

        Fight fight = new Fight();
        fight.setAttacker(attacker);
        fight.setDefender(defender);

        fight.setWinner(attacker);
        assertEquals(defender, fight.getLoser());

        fight.setWinner(defender);
        assertEquals(attacker, fight.getLoser());

        fight.setWinner(null);
        assertNull(fight.getLoser());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testFighting() throws Exception {
        Piece piece = new Piece();
        when(pieceService.findPiece(1)).thenReturn(piece);
        when(pieceService.isFighting(piece)).thenReturn(piece);

        mockMvc.perform(put("/api/v1/pieces/fighting")
                .param("pieceId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).findPiece(1);
        verify(pieceService).isFighting(piece);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testAttacking() throws Exception {
        Piece piece = new Piece();
        when(pieceService.findPiece(1)).thenReturn(piece);
        when(pieceService.isAttacking(piece)).thenReturn(piece);

        mockMvc.perform(put("/api/v1/pieces/attacking")
                .param("pieceId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).findPiece(1);
        verify(pieceService).isAttacking(piece);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testDefending() throws Exception {
        Piece piece = new Piece();
        when(pieceService.findPiece(1)).thenReturn(piece);
        when(pieceService.isDefending(piece)).thenReturn(piece);

        mockMvc.perform(put("/api/v1/pieces/defending")
                .param("pieceId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).findPiece(1);
        verify(pieceService).isDefending(piece);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testGetAttacker() throws Exception {
        Piece attacker = new Piece();
        when(pieceService.getAttacker(1)).thenReturn(attacker);

        mockMvc.perform(get("/api/v1/pieces/attacker")
                .param("gameId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).getAttacker(1);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testGetDefender() throws Exception {
        Piece defender = new Piece();
        when(pieceService.getDefender(1)).thenReturn(defender);

        mockMvc.perform(get("/api/v1/pieces/defender")
                .param("gameId", "1")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(pieceService).getDefender(1);
    }
    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testStealFromPlayerHand() throws Exception {
        GameSession game = new GameSession();
        Fight fight = new Fight();
        Piece winner = new Piece();
        Piece defender = new Piece();
    
        fight.setAttacker(winner);
        fight.setDefender(defender);
        fight.setWinner(winner); 
    
        game.setCurrenFight(fight);
    
        when(gameSessionService.findGameSession(1)).thenReturn(game);
        when(pieceService.stealFromOtherPlayerHand(fight.getWinner(), fight.getLoser())).thenReturn(winner);
    
        mockMvc.perform(put("/api/v1/pieces/stealFromPlayerHand")
                .param("gameId", "1")
                .with(csrf()))
                .andExpect(status().isOk());
    
        verify(gameSessionService).findGameSession(1);
        verify(pieceService).stealFromOtherPlayerHand(fight.getWinner(), fight.getLoser());
    }
    
    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testStealFromPlayerBag() throws Exception {
        GameSession game = new GameSession();
        Fight fight = new Fight();
        Piece winner = new Piece();
        Piece attacker = new Piece();
    
        fight.setAttacker(attacker);
        fight.setDefender(winner);
        fight.setWinner(winner); 
    
        game.setCurrenFight(fight);
    
        when(gameSessionService.findGameSession(1)).thenReturn(game);
        when(pieceService.stealFromOtherPlayerBag(fight.getWinner(), fight.getLoser(), 1)).thenReturn(winner);
    
        mockMvc.perform(put("/api/v1/pieces/stealFromPlayerBag")
                .param("gameId", "1")
                .param("cardId", "1")
                .with(csrf()))
                .andExpect(status().isOk());
    
        verify(gameSessionService).findGameSession(1);
        verify(pieceService).stealFromOtherPlayerBag(fight.getWinner(), fight.getLoser(), 1);
    }
    
    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    void testDiscardAfterLosing() throws Exception {
        GameSession game = new GameSession();
        Fight fight = new Fight();
        Piece attacker = new Piece();
        Piece defender = new Piece();
        Card card = new Card();
    
        fight.setAttacker(attacker);
        fight.setDefender(defender);
        fight.setWinner(attacker); 
    
        game.setCurrenFight(fight);
    
        when(gameSessionService.findGameSession(1)).thenReturn(game);
        when(pieceService.discardAfterLosing(fight.getLoser(), 1, true)).thenReturn(card);
    
        mockMvc.perform(put("/api/v1/pieces/discardAfterLosing")
                .param("gameId", "1")
                .param("cardId", "1")
                .param("fromHand", "true")
                .with(csrf()))
                .andExpect(status().isOk());
    
        verify(gameSessionService).findGameSession(1);
        verify(pieceService).discardAfterLosing(fight.getLoser(), 1, true);
    }
    
}
