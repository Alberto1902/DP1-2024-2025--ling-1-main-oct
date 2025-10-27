package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

@Repository
public interface GameSessionRepository extends CrudRepository<GameSession, Integer> {
    Optional<GameSession> findById(Integer id);
    List<GameSession> findAll();
    List<GameSession> findByStatus(String status);

    @Query("SELECT g FROM GameSession g JOIN g.players p WHERE p.id = :userId AND g.status = :status")
    List<GameSession> findActiveSessionsByUser(@Param("userId") Integer userId, @Param("status") String status);

    @Query("SELECT min(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Integer> findShortestGameByUser (@Param("userId") Integer userId);

    @Query("SELECT max(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Integer> findLongestGameByUser (@Param("userId") Integer userId);

    @Query("SELECT avg(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Double> findAverageGameByUser (@Param("userId") Integer userId);

    @Query("SELECT min(g.currentPlayers) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Integer> findSmallestGameByUser (@Param("userId") Integer userId);

    @Query("SELECT max(g.currentPlayers) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Integer> findBiggestGameByUser (@Param("userId") Integer userId);

    @Query("SELECT avg(g.currentPlayers) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Double> findAveragePlayersByUser (@Param("userId") Integer userId);

    @Query("SELECT count(g) FROM GameSession g JOIN g.players p WHERE p.id = :userId AND g.status = 'FINISHED'")
    Optional<Integer> findTotalGamesByUser (@Param("userId") Integer userId);

    @Query("SELECT count(g) FROM GameSession g JOIN g.players p WHERE p.id = :userId AND g.status = 'FINISHED' AND g.winner.id = :userId")
    Optional<Integer> findTotalWinsByUser (@Param("userId") Integer userId);

    @Query("SELECT min(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED'")
    Optional<Integer> findShortestGame ();

    @Query("SELECT max(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED'")
    Optional<Integer> findLongestGame ();

    @Query("SELECT avg(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED'")
    Optional<Double> findAverageGame ();

    @Query("SELECT avg(g.currentPlayers) FROM GameSession g")
    Optional<Double> findAverageGameSize ();

    @Query("SELECT sum(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED'")
    Optional<Integer> findMinutesPlayed ();

    @Query("SELECT sum(g.gameDuration) FROM GameSession g WHERE g.status = 'FINISHED' AND :userId IN (SELECT u.id FROM g.players u)")
    Optional<Integer> findMinutesPlayedByUser (@Param ("userId") Integer userId);

    @Query("SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED'")
    Optional<Integer> findTotalFinishedGames ();

    @Query("SELECT count(g) FROM GameSession g WHERE g.status = 'IN_PROGRESS'")
    Optional<Integer> findTotalActiveGames ();
}
