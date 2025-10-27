package es.us.dp1.lx_xy_24_25.your_game_name.gamesession;

import java.time.LocalDateTime;
import java.util.ArrayList;
import es.us.dp1.lx_xy_24_25.your_game_name.chat.ChatRoom;
import es.us.dp1.lx_xy_24_25.your_game_name.fight.Fight;
import es.us.dp1.lx_xy_24_25.your_game_name.model.NamedEntity;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "gamesessions")
public class GameSession extends NamedEntity {

    @Min(3)
    @Max(6)
    private Integer maxPlayers;

    @Column(columnDefinition = "ENUM('IN_PROGRESS', 'FINISHED', 'WAITING')")
    private String status = "WAITING";

    @Min(0)
    @Max(6)
    private Integer currentPlayers = 1;

    @Min(0)
    @Max(6)
    private Integer turn = 0;

    @NotNull
    public Boolean isPrivate = false;

    private String pin;

    @Min(0)
    Integer gameDuration = 0;

    @ManyToOne
    @JoinColumn(name = "winner_id")
    private User winner;

    @Column(name = "startTime")
    public LocalDateTime start = null;

    @Column(name = "endTime")
    public LocalDateTime end = null;

    @Column(name = "casual_max_duration_minutes")
    private Integer casualMaxDurationMinutes;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private User creator;

    @ManyToMany
    @JoinTable(name = "game_session_users",
            joinColumns = @JoinColumn(name = "game_session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> players = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "game_session_spectators",
            joinColumns = @JoinColumn(name = "game_session_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<User> spectators = new ArrayList<>();

    @OneToOne
    private ChatRoom chatRoom;

    @OneToOne
    @JsonIgnore
    private Fight currenFight = null;

    @ElementCollection
    @CollectionTable(name = "addedWeapons", joinColumns = @JoinColumn(name = "addedWeapons_id"))
    private List<String> addedWeapons = new ArrayList<>();

    public List<User> getPlayers() { return players; }
    public void setPlayers(List<User> players) { this.players = players; }

    public LocalDateTime getStart() { return start; }
    public void setStart(LocalDateTime start) { this.start = start; }

    public LocalDateTime getEnd() { return end; }
    public void setEnd(LocalDateTime end) { this.end = end; }

    public Integer getCasualMaxDurationMinutes() { return casualMaxDurationMinutes; }
    public void setCasualMaxDurationMinutes(Integer casualMaxDurationMinutes) { this.casualMaxDurationMinutes = casualMaxDurationMinutes; }
}