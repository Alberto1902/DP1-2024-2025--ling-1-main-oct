package es.us.dp1.lx_xy_24_25.your_game_name.friendship;

import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@Repository
public interface FriendshipRepository extends CrudRepository<Friendship, Integer> {
    Friendship findByUser1AndUser2(User user1, User user2);
    Friendship findByUser1AndUser2AndStatus(User user1, User user2, FriendshipStatus status);
    List<Friendship> findByUser1AndStatus(User user1, FriendshipStatus status);
    List<Friendship> findByUser2AndStatus(User user2, FriendshipStatus status);
}
