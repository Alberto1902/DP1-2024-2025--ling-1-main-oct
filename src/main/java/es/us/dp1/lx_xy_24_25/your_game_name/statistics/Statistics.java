package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import io.micrometer.common.lang.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Statistics extends BaseEntity{

    private Integer gamesPlayed;
    private Integer victories;
    private Integer defeats;

    private Integer shortestGame;
    private Integer longestGame;
    private Double averageGameDuration;

    private Integer smallestGameRoom;
    private Integer biggestGameRoomSize;
    private Double averageGameRoomSize;

    @Nullable
    @OneToOne
    private User user;

    private Double winRatio; // In %
    private Double lossRatio; // In %
    
}
