package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.ArrayList;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatRoom extends BaseEntity {

    @OneToMany(mappedBy = "chatRoom") 
    private List<ChatMessage> messages;

    public ChatRoom() {
        this.messages = new ArrayList<>();
    }
}
