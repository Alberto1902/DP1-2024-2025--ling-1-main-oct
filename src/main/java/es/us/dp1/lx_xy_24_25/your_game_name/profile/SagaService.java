package es.us.dp1.lx_xy_24_25.your_game_name.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SagaService {

    private final SagaRepository sagaRepository;

    @Autowired
    public SagaService(SagaRepository sagaRepository) {
        this.sagaRepository = sagaRepository;
    }

    @Transactional(readOnly = true)
    public List<Saga> findAll() {
        return sagaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Saga findById(Integer id) {
        return sagaRepository.findById(id).orElse(null);
    }

    @Transactional
    public Saga save(Saga saga) {
        return sagaRepository.save(saga);
    }

    @Transactional
    public void deleteById(Integer id) {
        sagaRepository.deleteById(id);
    }
}
