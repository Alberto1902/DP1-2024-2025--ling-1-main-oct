package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatMessageRepository extends CrudRepository<ChatMessage, Integer> {
    List<ChatMessage> findAll();
    Optional<ChatMessage> findById(Integer id);
}
