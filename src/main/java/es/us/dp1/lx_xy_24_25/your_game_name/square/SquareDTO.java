package es.us.dp1.lx_xy_24_25.your_game_name.square;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SquareDTO {

    private Integer id;
    private Integer whiteDice;
    private Integer blackDice;
    private Integer whiteDiceAlt;
    private String name;
    private Integer xPosition;
    private Integer yPosition;
    private String adyacentSquaresId;

    public SquareDTO(Square square, SquareService squareService) {
        this.id = square.getId();
        this.whiteDice = square.getWhiteDice();
        this.blackDice = square.getBlackDice();
        this.whiteDiceAlt = square.getWhiteDiceAlt();
        this.name = square.getName();
        this.xPosition = square.getXPosition();
        this.yPosition = square.getYPosition();
        this.adyacentSquaresId = squareService.findNeighborsIdsAsString(square.getId());
    }
}
