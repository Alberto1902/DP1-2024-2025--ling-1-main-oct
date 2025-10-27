package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatRoomRepository extends CrudRepository<ChatRoom, Integer> {
    List<ChatRoom> findAll();
    Optional<ChatRoom> findById(Integer id);
    @Query("SELECT c.messages FROM ChatRoom c WHERE c.id = ?1")
    List<ChatMessage> getMessagesById(Integer id);
}
