package es.us.dp1.lx_xy_24_25.your_game_name.profile;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sagas")
public class SagaController {

    private final SagaService sagaService;

    @Autowired
    public SagaController(SagaService sagaService) {
        this.sagaService = sagaService;
    }

    @GetMapping
    public ResponseEntity<List<Saga>> getAllSagas() {
        List<Saga> sagas = sagaService.findAll();
        return ResponseEntity.ok(sagas);
    }
}
