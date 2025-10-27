package es.us.dp1.lx_xy_24_25.your_game_name.square;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@SpringBootTest
@AutoConfigureTestDatabase
public class SquareServiceTests {

    @Autowired
    private SquareService squareService;

    private SquareDTO squareDTO;

    private final Integer squareId = 10;

    @BeforeEach
    void setUp() {
        squareDTO = new SquareDTO();
        squareDTO.setId(squareId);
        squareDTO.setName("TestSquare");
        squareDTO.setWhiteDice(2);
        squareDTO.setBlackDice(3);
        squareDTO.setWhiteDiceAlt(4);
        squareDTO.setXPosition(5);
        squareDTO.setYPosition(6);
        squareDTO.setAdyacentSquaresId("2, 3, 4");
    }

    @Test
    void shouldFindSquareById() {
        Integer id = 1;
        Square square = squareService.findSquare(id);
        assertNotNull(square);
        assertEquals(id, square.getId());
    }

    @Test
    void shouldThrowExceptionWhenSquareIdNotFound() {
        Integer nonExistentId = 999;
        assertThrows(ResourceNotFoundException.class, () -> squareService.findSquare(nonExistentId));
    }

    @Test
    void shouldFindSquareByDiceValues() {
        Integer blackDice = 2;
        Integer whiteDice = 5;
        Square square = squareService.findSquare(blackDice, whiteDice);
        assertNotNull(square);
        assertEquals(blackDice, square.getBlackDice());
        assertEquals(whiteDice, square.getWhiteDice());
    }

    @Test
    void shouldThrowExceptionWhenSquareWithDiceNotFound() {
        Integer nonExistentBlackDice = 10;
        Integer nonExistentWhiteDice = 20;
        assertThrows(ResourceNotFoundException.class, () -> squareService.findSquare(nonExistentBlackDice, nonExistentWhiteDice));
    }

    @Test
    void shouldFindAllSquares() {
        List<SquareDTO> squares = squareService.findAll();
        assertNotNull(squares);
    }

    @Test
    void shouldReturnSquareDTOFieldsCorrectly() {
        assertEquals(squareId, squareDTO.getId());
        assertEquals("TestSquare", squareDTO.getName());
        assertEquals(2, squareDTO.getWhiteDice());
        assertEquals(3, squareDTO.getBlackDice());
        assertEquals(4, squareDTO.getWhiteDiceAlt());
        assertEquals(5, squareDTO.getXPosition());
        assertEquals(6, squareDTO.getYPosition());
        assertEquals("2, 3, 4", squareDTO.getAdyacentSquaresId());
    }
}
