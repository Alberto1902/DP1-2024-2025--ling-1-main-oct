package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/statistics")
@Tag(name = "Statistics", description = "Statistics management API")
@SecurityRequirement(name = "bearerAuth")
public class StatisticsRestController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsRestController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    // --- Endpoints de Estadísticas Globales (Puedes adaptarlos a un DTO si la entidad Statistics da problemas aquí también) ---
    @Operation(summary = "Post global statistics", description = "Create the global statistics if they do not exist")
    @PostMapping("/global")
    public ResponseEntity<Statistics> createGlobalStatistics() {
        System.out.println("REST: Recibida petición POST /global");
        return new ResponseEntity<>(statisticsService.updateGlobalStatistics(), HttpStatus.CREATED);
    }

    @Operation(summary = "Update global statistics", description = "Update the global statistics")
    @PutMapping("/global")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Statistics> updateGlobalStatistics() {
        System.out.println("REST: Recibida petición PUT /global");
        return new ResponseEntity<>(statisticsService.updateGlobalStatistics(), HttpStatus.OK);
    }

    @Operation(summary = "Get global statistics", description = "Returns the global statistics")
    @GetMapping("/global")
    public ResponseEntity<Statistics> findGlobalStatistics() {
        System.out.println("REST: Recibida petición GET /global");
        return new ResponseEntity<>(statisticsService.getGlobalStatistics(), HttpStatus.OK);
    }

    // --- Endpoints de Estadísticas de Usuario ---

    @Operation(summary = "Get user statistics", description = "Returns the statistics of a user, it is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "Id of the user whose statistics we want to retrieve")
    @GetMapping("/user")
    // ¡¡¡CAMBIO CRÍTICO AQUÍ: Devolvemos UserStatisticsDTO!!!
    public ResponseEntity<UserStatisticsDTO> findUserStatistics(@RequestParam("userId") Integer userId) {
        System.out.println("REST: Recibida petición GET /user para userId: " + userId);
        Statistics statistics = statisticsService.getUserStatistics(userId); // Intenta obtener la entidad existente

        if (statistics == null) {
            System.out.println("REST: No se encontraron estadísticas existentes para userId: " + userId + ". Devolviendo DTO con ceros.");
            // Si no se encuentran estadísticas, devolvemos un DTO con valores por defecto (todos a cero)
            return new ResponseEntity<>(new UserStatisticsDTO(), HttpStatus.OK);
        } else {
            System.out.println("REST: Estadísticas encontradas para userId: " + userId + ". Mapeando a DTO.");
            // Mapeamos la entidad Statistics a tu DTO UserStatisticsDTO
            UserStatisticsDTO dto = new UserStatisticsDTO(
                statistics.getGamesPlayed() != null ? statistics.getGamesPlayed() : 0,
                statistics.getVictories() != null ? statistics.getVictories() : 0,
                statistics.getDefeats() != null ? statistics.getDefeats() : 0,
                statistics.getWinRatio() != null ? statistics.getWinRatio() : 0.0,
                statistics.getLossRatio() != null ? statistics.getLossRatio() : 0.0,
                statistics.getAverageGameDuration() != null ? statistics.getAverageGameDuration() : 0.0
            );
            System.out.println("REST: DTO a enviar: " + dto.getGamesPlayed() + " partidas, " + dto.getVictories() + " victorias.");
            return new ResponseEntity<>(dto, HttpStatus.OK);
        }
    }

    @Operation(summary = "Update user statistics", description = "Update the statistics of a user, it is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "Id of the user whose statistics we want to update")
    @PutMapping("/user")
    // ¡¡¡CAMBIO CRÍTICO AQUÍ: Devolvemos UserStatisticsDTO!!!
    public ResponseEntity<UserStatisticsDTO> updateUserStatistics(@RequestParam("userId") Integer userId) {
        System.out.println("REST: Recibida petición PUT /user para userId: " + userId);
        try {
            Statistics updatedStatistics = statisticsService.updateUserStatistics(userId); // Llama al servicio para actualizar/crear

            // Mapeamos la entidad actualizada a tu DTO
            UserStatisticsDTO dto = new UserStatisticsDTO(
                updatedStatistics.getGamesPlayed() != null ? updatedStatistics.getGamesPlayed() : 0,
                updatedStatistics.getVictories() != null ? updatedStatistics.getVictories() : 0,
                updatedStatistics.getDefeats() != null ? updatedStatistics.getDefeats() : 0,
                updatedStatistics.getWinRatio() != null ? updatedStatistics.getWinRatio() : 0.0,
                updatedStatistics.getLossRatio() != null ? updatedStatistics.getLossRatio() : 0.0,
                updatedStatistics.getAverageGameDuration() != null ? updatedStatistics.getAverageGameDuration() : 0.0
            );
            System.out.println("REST: Estadísticas de usuario actualizadas y enviadas: " + dto.getGamesPlayed() + " partidas.");
            return new ResponseEntity<>(dto, HttpStatus.OK);
        } catch (RuntimeException e) {
            System.err.println("ERROR: Fallo al actualizar estadísticas para userId " + userId + ": " + e.getMessage());
            e.printStackTrace(); // Imprime el stack trace para depuración detallada
            return new ResponseEntity<>(new UserStatisticsDTO(), HttpStatus.INTERNAL_SERVER_ERROR); // Devuelve un DTO vacío en caso de error
        }
    }

    @Operation(summary = "Create user statistics", description = "Create the statistics of a user, it is only available for users with the role of PLAYER")
    @Parameter(name = "userId", description = "Id of the user whose statistics we want to create")
    @PostMapping("/user")
    // ¡¡¡CAMBIO CRÍTICO AQUÍ: Devolvemos UserStatisticsDTO!!!
    public ResponseEntity<UserStatisticsDTO> createUserStatistics(@RequestParam("userId") Integer userId) {
        System.out.println("REST: Recibida petición POST /user para userId: " + userId);
        try {
            // Reutilizamos updateUserStatistics ya que tu lógica maneja creación si no existe
            Statistics createdStatistics = statisticsService.updateUserStatistics(userId); 
            
            // Mapeamos la entidad creada a tu DTO
            UserStatisticsDTO dto = new UserStatisticsDTO(
                createdStatistics.getGamesPlayed() != null ? createdStatistics.getGamesPlayed() : 0,
                createdStatistics.getVictories() != null ? createdStatistics.getVictories() : 0,
                createdStatistics.getDefeats() != null ? createdStatistics.getDefeats() : 0,
                createdStatistics.getWinRatio() != null ? createdStatistics.getWinRatio() : 0.0,
                createdStatistics.getLossRatio() != null ? createdStatistics.getLossRatio() : 0.0,
                createdStatistics.getAverageGameDuration() != null ? createdStatistics.getAverageGameDuration() : 0.0
            );
            System.out.println("REST: Estadísticas de usuario creadas y enviadas: " + dto.getGamesPlayed() + " partidas.");
            return new ResponseEntity<>(dto, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            System.err.println("ERROR: Fallo al crear estadísticas para userId " + userId + ": " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(new UserStatisticsDTO(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}