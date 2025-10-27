package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;
import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class ChatMessage extends BaseEntity{
    
    @NotBlank
    private String senderUsername;

    @NotBlank
    private String message;

    private LocalDateTime timestamp;

    @NotNull
    @ManyToOne
    @JsonIgnore
    private ChatRoom chatRoom;

}
