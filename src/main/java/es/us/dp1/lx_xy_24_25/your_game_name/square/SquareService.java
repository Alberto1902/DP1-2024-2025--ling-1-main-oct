package es.us.dp1.lx_xy_24_25.your_game_name.square;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;

@Service
public class SquareService {

    private SquareRepository squareRepository;

    @Autowired
    public SquareService(SquareRepository squareRepository) {
        this.squareRepository = squareRepository;
    }

    @Transactional(readOnly = true)
    public Square findSquare(Integer id) {
        return squareRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Square", "id", id));
    }

    @Transactional(readOnly = true)
    public Square findSquare(Integer blackDice, Integer whiteDice) {
        return squareRepository.findByDice(blackDice, whiteDice)
                .or(() -> squareRepository.findByAltDice(blackDice, whiteDice))
                .orElseThrow(() -> new ResourceNotFoundException("Square", "blackDice and whiteDice",
                        blackDice + " and " + whiteDice));
    }

    @Transactional(readOnly = true)
    public List<SquareDTO> findAll() {
        List<Square> squares = squareRepository.findAll();
        return convertToDTO(squares);
    }

    private List<SquareDTO> convertToDTO(List<Square> squares) {
        return squares.stream()
                .map(square -> new SquareDTO(square, this))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public String findNeighborsIdsAsString(Integer squareId) {
        List<Square> neighbors = squareRepository.findNeighbors(squareId);
        return neighbors.stream()
                .map(neighbor -> neighbor.getId().toString())
                .collect(Collectors.joining(", "));
    }

    @Transactional(readOnly = true)
    public List<Square> findByNameContains(String name) {
        return squareRepository.findByNameContains(name);
    }
}
