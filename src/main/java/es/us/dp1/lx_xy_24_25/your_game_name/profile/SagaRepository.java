package es.us.dp1.lx_xy_24_25.your_game_name.profile;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SagaRepository extends JpaRepository<Saga, Integer> {
}
