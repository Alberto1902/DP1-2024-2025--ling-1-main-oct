package es.us.dp1.lx_xy_24_25.your_game_name.square;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@WebMvcTest(controllers = SquareRestController.class)
class SquareRestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SquareService squareService;



    private Square square;
    private SquareDTO squareDTO;

    @BeforeEach
    void setup() {
        square = new Square(null, 0, 0);
        square.setId(1);
        square.setName("Test Square");
        square.setBlackDice(3);
        square.setWhiteDice(5);

        squareDTO = new SquareDTO(square, squareService);
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnAllSquaresAsDTOs() throws Exception {
        when(squareService.findAll()).thenReturn(List.of(squareDTO));

        mockMvc.perform(get("/api/v1/squares/dto")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(squareDTO.getId()))
                .andExpect(jsonPath("$[0].name").value(squareDTO.getName()));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnSquareById() throws Exception {
        when(squareService.findSquare(1)).thenReturn(square);

        mockMvc.perform(get("/api/v1/squares/id/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(square.getId()))
                .andExpect(jsonPath("$.name").value(square.getName()));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnSquareByDices() throws Exception {
        when(squareService.findSquare(3, 5)).thenReturn(square);

        mockMvc.perform(get("/api/v1/squares/dices/3-5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(square.getId()))
                .andExpect(jsonPath("$.blackDice").value(square.getBlackDice()))
                .andExpect(jsonPath("$.whiteDice").value(square.getWhiteDice()));
    }

    @Test
    @WithMockUser("admin")
    void shouldReturnNotFoundWhenSquareDoesNotExist() throws Exception {
        when(squareService.findSquare(999)).thenThrow(new ResourceNotFoundException("Square", "id", 999));

        mockMvc.perform(get("/api/v1/squares/id/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
