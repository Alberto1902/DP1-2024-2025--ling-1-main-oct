package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService; 
import java.util.Optional;

@Service
public class StatisticsService {

    private final GameSessionService gameSessionService;
    private final UserService userService;
    private final StatisticsRepository statisticsRepository;

    @Autowired
    public StatisticsService(GameSessionService gameSessionService, UserService userService, StatisticsRepository statisticsRepository) {
        this.statisticsRepository = statisticsRepository;
        this.gameSessionService = gameSessionService;
        this.userService = userService;
    }

    @Transactional
    public Statistics updateUserStatistics(Integer userId) {
        System.out.println("DEBUG: Iniciando updateUserStatistics para userId: " + userId);
        Statistics statistics = null;
        Optional<Statistics> statisticsToCheck = statisticsRepository.findByUserId(userId);

        if(statisticsToCheck.isPresent()){
            statistics = statisticsToCheck.get();
            System.out.println("DEBUG: Estadísticas existentes encontradas para userId: " + userId);
        } else {
            statistics = new Statistics();
            System.out.println("DEBUG: Creando nueva entrada de estadísticas para userId: " + userId);
            User user = userService.findUser(userId);
            if (user == null) {
                System.err.println("ERROR: Usuario con ID " + userId + " no encontrado. No se pueden actualizar/crear estadísticas.");
                throw new RuntimeException("Usuario no encontrado para actualización de estadísticas: " + userId);
            }
            statistics.setUser(user);
        }

        Integer totalGames = gameSessionService.findTotalGamesByUser(userId);
        System.out.println("DEBUG: Total de partidas jugadas: " + totalGames);
        Integer totalWins = gameSessionService.findTotalWinsByUser(userId);
        System.out.println("DEBUG: Total de victorias: " + totalWins);


        int gamesPlayed = (totalGames != null) ? totalGames : 0;
        int victories = (totalWins != null) ? totalWins : 0;
        int defeats = gamesPlayed - victories; 
        System.out.println("DEBUG: Derrotas calculadas: " + defeats);

        statistics.setGamesPlayed(gamesPlayed);
        statistics.setVictories(victories);
        statistics.setDefeats(defeats);

        statistics.setShortestGame(gameSessionService.findShortestGameByUser(userId));
        System.out.println("DEBUG: Partida más corta: " + statistics.getShortestGame());
        statistics.setLongestGame(gameSessionService.findLongestGameByUser(userId));
        System.out.println("DEBUG: Partida más larga: " + statistics.getLongestGame());
        statistics.setAverageGameDuration(gameSessionService.findAverageGameByUser(userId));
        System.out.println("DEBUG: Duración media de partida: " + statistics.getAverageGameDuration());
        statistics.setSmallestGameRoom(gameSessionService.findSmallestGameByUser(userId));
        System.out.println("DEBUG: Sala más pequeña: " + statistics.getSmallestGameRoom());
        statistics.setBiggestGameRoomSize(gameSessionService.findBiggestGameByUser(userId));
        System.out.println("DEBUG: Sala más grande: " + statistics.getBiggestGameRoomSize());
        statistics.setAverageGameRoomSize(gameSessionService.findAveragePlayersByUser(userId));
        System.out.println("DEBUG: Tamaño medio de sala: " + statistics.getAverageGameRoomSize());

        Double winRatio = (gamesPlayed > 0) ? (victories * 100.0) / gamesPlayed : 0.0;
        Double lossRatio = (gamesPlayed > 0) ? (defeats * 100.0) / gamesPlayed : 0.0;
        statistics.setWinRatio(winRatio);
        statistics.setLossRatio(lossRatio);
        System.out.println("DEBUG: Ratio de victorias: " + winRatio + ", Ratio de derrotas: " + lossRatio);

        statisticsRepository.save(statistics);
        System.out.println("DEBUG: Estadísticas guardadas/actualizadas para userId: " + userId);
        return statistics;
    }

    @Transactional
    public Statistics updateGlobalStatistics() {
        System.out.println("DEBUG: Iniciando updateGlobalStatistics.");
        Statistics statistics = null;
        Optional<Statistics> statisticsToCheck = statisticsRepository.findByUserIsNull();
        if(statisticsToCheck.isPresent()){
            statistics = statisticsToCheck.get();
            System.out.println("DEBUG: Estadísticas globales existentes encontradas.");
        } else {
            statistics = new Statistics();
            System.out.println("DEBUG: Creando nueva entrada de estadísticas globales.");
        }
        
        Integer totalGames = gameSessionService.findTotalFinishedGames();
        statistics.setGamesPlayed(totalGames != null ? totalGames : 0);
        
        statistics.setVictories(null);
        statistics.setDefeats(null);
        statistics.setWinRatio(null);
        statistics.setLossRatio(null);

        statistics.setShortestGame(gameSessionService.findShortestGame());
        statistics.setLongestGame(gameSessionService.findLongestGame());
        statistics.setAverageGameDuration(gameSessionService.findAverageGame());
        statistics.setSmallestGameRoom(null); 
        statistics.setBiggestGameRoomSize(null);
        statistics.setAverageGameRoomSize(gameSessionService.findAverageGameSize());
        
        System.out.println("DEBUG: Estadísticas globales calculadas: Total Juegos=" + statistics.getGamesPlayed() + ", Duración Media=" + statistics.getAverageGameDuration());
        statisticsRepository.save(statistics);
        System.out.println("DEBUG: Estadísticas globales guardadas.");
        return statistics;
    }

    @Transactional(readOnly = true)
    public Statistics getGlobalStatistics() {
        System.out.println("DEBUG: Recuperando estadísticas globales.");
        return statisticsRepository.findByUserIsNull().orElse(null);
    }

    @Transactional(readOnly = true) 
    public Statistics getUserStatistics(Integer userId) {
        System.out.println("DEBUG: Recuperando estadísticas para userId: " + userId);
        return statisticsRepository.findByUserId(userId).orElse(null);
    }
}