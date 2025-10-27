package es.us.dp1.lx_xy_24_25.your_game_name.fight;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(FightRestController.class)
public class FightRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FightService fightService;

    @MockBean
    private PieceService pieceService;

    @MockBean
    private GameSessionService gameSessionService;

    private final String BASE_URL = "/api/v1/fights";

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void startFightSuccessTest() throws Exception {
        Piece attacker = new Piece();
        Piece defender = new Piece();
        GameSession gameSession = new GameSession();
        Fight fight = new Fight();

        when(pieceService.findPiece(1)).thenReturn(attacker);
        when(pieceService.findPiece(2)).thenReturn(defender);
        when(gameSessionService.findGameSession(100)).thenReturn(gameSession);
        when(fightService.fight(attacker, defender, gameSession)).thenReturn(fight);

        mvc.perform(post(BASE_URL + "/1-2")
                .param("gameId", "100")
                .with(csrf()))
                .andExpect(status().isOk());

        verify(fightService).fight(attacker, defender, gameSession);
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    public void startFightInvalidPositionTest() throws Exception {
        Piece attacker = new Piece();
        Piece defender = new Piece();
        GameSession gameSession = new GameSession();

        when(pieceService.findPiece(1)).thenReturn(attacker);
        when(pieceService.findPiece(2)).thenReturn(defender);
        when(gameSessionService.findGameSession(100)).thenReturn(gameSession);
        doThrow(new IllegalArgumentException("A fight takes place only in one square"))
                .when(fightService).fight(any(), any(), any());

        mvc.perform(post(BASE_URL + "/1-2")
                .param("gameId", "100")
                .with(csrf()))
                .andExpect(status().isBadRequest());

        verify(fightService).fight(attacker, defender, gameSession);
    }
}