package es.us.dp1.lx_xy_24_25.your_game_name.decks;

import org.springframework.stereotype.Repository;

import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;

import org.springframework.data.repository.CrudRepository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeckRepository extends CrudRepository<Deck, Integer> {
    Optional<Deck> findById(Integer id);
    List<Deck> findAll();
    List<Deck> findByIsDiscardAndGameSession(Boolean isDiscard, GameSession gameSession);
    List<Deck> findByGameSessionId(Integer gameSessionId);
}
