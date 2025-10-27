package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {
    Optional<Card> findById(Integer id);
    List<Card> findAll();
}
