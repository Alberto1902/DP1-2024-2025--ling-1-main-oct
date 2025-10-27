package es.us.dp1.lx_xy_24_25.your_game_name.fight;

import es.us.dp1.lx_xy_24_25.your_game_name.piece.Piece;
import es.us.dp1.lx_xy_24_25.your_game_name.square.Square;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FightRepository extends CrudRepository<Fight, Integer> {

    @Query("SELECT COUNT(f) FROM Fight f WHERE f.winner = :piece")
    Integer countByWinner(@Param("piece") Piece piece);

    @Query("SELECT COUNT(f) FROM Fight f WHERE f.defender = :piece AND f.winner != :piece")
    Integer countByLoser(@Param("piece") Piece piece);

    @Query("SELECT f.defender.position FROM Fight f GROUP BY f.defender.position ORDER BY COUNT(f) DESC LIMIT 1")
    Square mostFoughtSquare();
    
}