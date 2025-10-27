package es.us.dp1.lx_xy_24_25.your_game_name.square;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/squares")
@Tag(name = "Square", description = "Square management API")
class SquareRestController {

    private final SquareService squareService;

    @Autowired
    public SquareRestController(SquareService squareService) {
        this.squareService = squareService;
    }

    @Operation(summary = "Get all squares", description = "Returns a list of all squares in the map. It is only available for users with the role of PLAYER")
    @GetMapping(value = "/dto")
    public ResponseEntity<List<SquareDTO>> findAllSquares() {
        List<SquareDTO> squares = squareService.findAll();
        return new ResponseEntity<>(squares, HttpStatus.OK);
    }

    @Operation(summary = "Get a square by its id", description = "Returns a square given its id. It is only available for users with the role of PLAYER")
    @Parameter(name = "id", description = "Square id")
    @GetMapping(value = "/id/{id}")
    public ResponseEntity<Square> findSquare(@PathVariable Integer id) {
        Square square = squareService.findSquare(id);
        return new ResponseEntity<>(square, HttpStatus.OK);
    }

    @Operation(summary = "Get a square by its coordinates", description = "Returns a square given its coordinates. It is only available for users with the role of PLAYER")
    @Parameter(name = "blackDice", description = "Square black dice value") 
    @Parameter(name = "whiteDice", description = "Square white dice value")
    @GetMapping("/dices/{blackDice}-{whiteDice}")
    public ResponseEntity<Square> findSquare(@PathVariable Integer blackDice, @PathVariable Integer whiteDice) {
        Square square = squareService.findSquare(blackDice, whiteDice);
        return new ResponseEntity<>(square, HttpStatus.OK);
    }
}
