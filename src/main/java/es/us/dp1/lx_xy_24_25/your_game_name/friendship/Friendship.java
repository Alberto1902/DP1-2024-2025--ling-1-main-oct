package es.us.dp1.lx_xy_24_25.your_game_name.friendship;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Friendship extends BaseEntity{

    @ManyToOne
    private User user1;

    @ManyToOne
    private User user2;

    @Enumerated(EnumType.STRING)
    private FriendshipStatus status;
    
}
