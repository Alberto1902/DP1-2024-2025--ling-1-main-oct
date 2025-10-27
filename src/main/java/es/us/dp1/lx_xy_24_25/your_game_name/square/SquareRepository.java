package es.us.dp1.lx_xy_24_25.your_game_name.square;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SquareRepository extends CrudRepository<Square, Integer> {

    Optional<Square> findById(Integer id);

    @Query("SELECT s FROM Square s WHERE s.blackDice = :blackDice AND s.whiteDice = :whiteDice")
    Optional<Square> findByDice(Integer blackDice, Integer whiteDice);

    @Query("SELECT s FROM Square s WHERE s.blackDice = :blackDice AND s.whiteDiceAlt = :whiteDiceAlt")
    Optional<Square> findByAltDice(Integer blackDice, Integer whiteDiceAlt);

    @Query("SELECT s FROM Square s JOIN s.adyacentSquares a WHERE a.id = :squareId")
    List<Square> findNeighbors(@Param("squareId") Integer squareId);

    @Query("SELECT s FROM Square s WHERE s.name LIKE %:name%")
    List<Square> findByNameContains(@Param("name") String name);

    List<Square> findAll();
    
}
