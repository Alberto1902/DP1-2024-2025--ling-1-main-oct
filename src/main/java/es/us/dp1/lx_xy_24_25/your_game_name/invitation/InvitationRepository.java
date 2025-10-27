package es.us.dp1.lx_xy_24_25.your_game_name.invitation;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface InvitationRepository extends CrudRepository<Invitation, Integer> {
    Optional<Invitation> findById(Integer id);
    List<Invitation> findByReceiverIdAndStatus(Integer receiverId, String status);
    Optional<Invitation> findByReceiverIdAndGameId(Integer receiverId, Integer gameId);
}
