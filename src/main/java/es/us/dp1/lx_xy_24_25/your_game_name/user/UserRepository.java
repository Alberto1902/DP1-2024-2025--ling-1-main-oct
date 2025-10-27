package es.us.dp1.lx_xy_24_25.your_game_name.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends  CrudRepository<User, Integer>{			

	Optional<User> findByUsername(String username);

	@Query("SELECT CASE WHEN COUNT(u) > 0 THEN TRUE ELSE FALSE END FROM User u WHERE u.username = :username")
	Boolean existsByUsername(String username);

	@Query("SELECT u from User u")
	Page<User> findAllPages(Pageable pageable);

	Optional<User> findById(Integer id);
	
	@Query("SELECT u FROM User u WHERE u.authority.authority = :auth")
	Iterable<User> findAllByAuthority(String auth);

	@Query("SELECT u, (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) as victories, (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) FROM User u WHERE u.authority.authority = 'PLAYER' ORDER BY victories DESC")
	Page<Object[]> findAllByWins(Pageable pageable);

	@Query("SELECT u, (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) as victories, (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) as gamesPlayed FROM User u WHERE u.authority.authority = 'PLAYER' ORDER BY gamesPlayed DESC")
	Page<Object[]> findAllByGamesPlayed(Pageable pageable);

	@Query("SELECT u, " +
       "(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) as victories, " +
       "(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) as gamesPlayed, " +
       "CASE WHEN (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) = 0 THEN 0.0 " +
       "ELSE (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id)* 1.0 / " +
       "(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) END as winRatio " +
       "FROM User u WHERE u.authority.authority = 'PLAYER' ORDER BY winRatio DESC")
	Page<Object[]> findAllByWinRatio(Pageable pageable);
	
	@Query("SELECT u, " +
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) as victories, "+
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) as gamesPlayed " +
	"FROM User u " +
	"WHERE u.authority.authority = 'PLAYER' AND " +
	"(u.id IN (SELECT f.user1.id FROM Friendship f WHERE f.status = 'ACCEPTED' AND f.user2.id = :id) OR " +
	"u.id IN (SELECT f.user2.id FROM Friendship f WHERE f.status = 'ACCEPTED' AND f.user1.id = :id)) OR " +
	"u.id = :id " +
	"ORDER BY gamesPlayed DESC")
	Page<Object[]> findAllByGamesPlayedFriends(@Param("id") Integer id, Pageable pageable);

	@Query("SELECT u, " +
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) as victories, "+
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) as gamesPlayed " +
	"FROM User u " +
	"WHERE u.authority.authority = 'PLAYER' AND " +
	"(u.id IN (SELECT f.user1.id FROM Friendship f WHERE f.status = 'ACCEPTED' AND f.user2.id = :id) OR " +
	"u.id IN (SELECT f.user2.id FROM Friendship f WHERE f.status = 'ACCEPTED' AND f.user1.id = :id)) OR " +
	"u.id = :id " +
	"ORDER BY victories DESC")
	Page<Object[]> findAllByWinsFriends(@Param("id") Integer id, Pageable pageable);

	@Query("SELECT u, " +
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) as victories, " +
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) as gamesPlayed, " +
	"CASE WHEN (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) = 0 THEN 0.0 " +
	"ELSE (SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p) AND g.winner.id = u.id) * 1.0 / " +
	"(SELECT count(g) FROM GameSession g WHERE g.status = 'FINISHED' AND u.id IN (SELECT p.id FROM g.players p)) END as winRatio " +
	"FROM User u " +
	"WHERE u.authority.authority = 'PLAYER' AND " +
	"(u.id IN (SELECT f.user1.id FROM Friendship f WHERE f.status = 'ACCEPTED' AND f.user2.id = :id) OR " +
	"u.id IN (SELECT f.user2.id FROM Friendship f WHERE f.status = 'ACCEPTED' AND f.user1.id = :id)) OR " +
	"u.id = :id " +
	"ORDER BY winRatio DESC")
	Page<Object[]> findAllByWinRatioFriends(@Param("id") Integer id, Pageable pageable);

	@Query("SELECT count(u) FROM User u WHERE u.authority.authority = 'PLAYER'")
	Integer countPlayers();
	
	@Query("SELECT count(u) FROM User u WHERE u.authority.authority = 'PLAYER' AND u.online = TRUE")
	Integer countOnlinePlayers();
}
