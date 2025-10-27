package es.us.dp1.lx_xy_24_25.your_game_name.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PlatformService {

    private final PlatformRepository platformRepository;

    @Autowired
    public PlatformService(PlatformRepository platformRepository) {
        this.platformRepository = platformRepository;
    }

    @Transactional(readOnly = true)
    public List<Platform> findAll() {
        return platformRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Platform findById(Integer id) {
        return platformRepository.findById(id).orElse(null);
    }

    @Transactional
    public Platform save(Platform platform) {
        return platformRepository.save(platform);
    }

    @Transactional
    public void deleteById(Integer id) {
        platformRepository.deleteById(id);
    }
}