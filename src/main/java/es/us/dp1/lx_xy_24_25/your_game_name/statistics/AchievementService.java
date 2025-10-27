package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class AchievementService {
    AchievementRepository achievementRepository;

    @Autowired
    public AchievementService(AchievementRepository achievementRepository) {
        this.achievementRepository = achievementRepository;
    }

    @Transactional(readOnly = true)
    public Page<Achievement> getAchievements(Pageable pageable) {
        return achievementRepository.findAllAchievements(pageable);
    }

    @Transactional(readOnly = true)
    public Achievement getById (int id) {
        Optional<Achievement> result = achievementRepository.findById(id);
        return result.isPresent() ? result.get() : null;
    }

    @Transactional
    public Achievement saveAchievement(@Valid Achievement achievement) {
        return achievementRepository.save(achievement);
    }
        
    @Transactional
    public void deleteAchievementById(int id){
        achievementRepository.deleteById(id);
    }
}
