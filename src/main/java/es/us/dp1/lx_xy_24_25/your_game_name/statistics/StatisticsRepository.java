package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.springframework.data.repository.CrudRepository;
import java.util.Optional;


public interface StatisticsRepository extends CrudRepository<Statistics, Integer> {

    Optional<Statistics> findByUserId(Integer userId);
    Optional<Statistics> findByUserIsNull();
    
}
