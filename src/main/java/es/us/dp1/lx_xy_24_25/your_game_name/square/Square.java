package es.us.dp1.lx_xy_24_25.your_game_name.square;

import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.EqualsAndHashCode;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
public class Square extends BaseEntity {

    private Integer whiteDice;

    private Integer blackDice;

    private Integer whiteDiceAlt;

    private String name;
    
    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "square_adyacentes", joinColumns = @JoinColumn(name = "square_id"), inverseJoinColumns = @JoinColumn(name = "adyacente_id"))
    private Set<Square> adyacentSquares = new HashSet<>();
    
    @NotNull
    private Integer xPosition;
    
    @NotNull
    private Integer yPosition;    

    private String escapeWord;

    /* ONLY FOR TESTING */
    public Square(String name, int x, int y) {
        this.name = name;
        this.xPosition = x;
        this.yPosition = y;
    }
}
