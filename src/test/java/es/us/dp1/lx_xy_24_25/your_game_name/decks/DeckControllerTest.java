package es.us.dp1.lx_xy_24_25.your_game_name.decks;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.piece.PieceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(value = {
        DeckController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class))
public class DeckControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private DeckService deckService;

    @MockBean
    private GameSessionService gameSessionService;

    @MockBean
    private PieceService pieceService;

    private final String BASE_URL = "/api/v1/decks";

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void reshuffleShouldReturnBadRequestWhenGameSessionIsInvalid() throws Exception {
        int gameSessionId = 999;

        when(gameSessionService.findGameSession(gameSessionId)).thenReturn(null);

        mvc.perform(put("/api/v1/decks/reshuffle?gameSessionId=" + gameSessionId).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void takeOneCardShouldReturnBadRequestWhenGameSessionIsInvalid() throws Exception {
        when(gameSessionService.findGameSession(999)).thenReturn(null);

        mvc.perform(get(BASE_URL+"/takeOneCard?isDiscard=true&gameSessionId=999").with(csrf()))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void shuffleDeckShouldReturnBadRequestWhenGameSessionIsInvalid() throws Exception {
        when(gameSessionService.findGameSession(999)).thenReturn(null);

        mvc.perform(put("/api/v1/decks/shuffle?isDiscard=true&gameSessionId=999").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void discardCardsShouldReturnBadRequestWhenPieceIsInvalid() throws Exception {
        when(pieceService.findPiece(999)).thenReturn(null);

        mvc.perform(put("/api/v1/decks/discard?id=999").with(csrf())
                .contentType("application/json")
                .content("[]"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void initialCardsShouldReturnBadRequestWhenGameSessionIsInvalid() throws Exception {
        when(gameSessionService.findGameSession(999)).thenReturn(null);

        mvc.perform(put("/api/v1/decks/initialCards?gameSessionId=999").with(csrf()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void findDeckShouldReturnOkWhenIdIsProvided() throws Exception {
        int deckId = 1;
        Deck deck = new Deck();
        deck.setId(deckId);

        when(deckService.findDeck(deckId)).thenReturn(deck);

        mvc.perform(get("/api/v1/decks/deck?id=" + deckId + "&isDiscard=true&gameSessionId=1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(deckId));
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void findDeckShouldReturnOkWhenIdIsNotProvided() throws Exception {
        int gameSessionId = 1;
        boolean isDiscard = true;
        GameSession gameSession = new GameSession();
        gameSession.setId(gameSessionId);
        Deck deck = new Deck();

        when(gameSessionService.findGameSession(gameSessionId)).thenReturn(gameSession);
        when(deckService.findDecksByIsDiscardAndGameSession(isDiscard, gameSession)).thenReturn(deck);

        mvc.perform(get("/api/v1/decks/deck?isDiscard=" + isDiscard + "&gameSessionId=" + gameSessionId).with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void findDeckShouldReturnBadRequestWhenGameSessionIsInvalid() throws Exception {
        int gameSessionId = 999;
        boolean isDiscard = true;

        when(gameSessionService.findGameSession(gameSessionId)).thenReturn(null);

        mvc.perform(get("/api/v1/decks/deck?isDiscard=" + isDiscard + "&gameSessionId=" + gameSessionId).with(csrf()))
                .andExpect(status().isBadRequest());
    }

    
}
