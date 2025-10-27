package es.us.dp1.lx_xy_24_25.your_game_name.piece;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PieceRepository extends CrudRepository<Piece, Integer> {
    Optional<Piece> findById(Integer id);
    Optional<Piece> findByUserIdAndGameId(Integer userId, Integer gameId);
    List<Piece> findByGameId(Integer gameId);
    Optional<Piece> findByGameIdAndPlayerOrder(Integer gameId, Integer playerOrder);
    List<Piece> findByGameIdAndPositionId(Integer gameSessionId, Integer positionId);
    
}
