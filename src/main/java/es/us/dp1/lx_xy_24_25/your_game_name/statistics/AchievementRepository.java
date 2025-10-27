package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import java.util.List;

@Repository
public interface AchievementRepository extends CrudRepository<Achievement, Integer> {

    @Query("SELECT a FROM Achievement a")
    Page<Achievement> findAllAchievements(Pageable pageable);
}