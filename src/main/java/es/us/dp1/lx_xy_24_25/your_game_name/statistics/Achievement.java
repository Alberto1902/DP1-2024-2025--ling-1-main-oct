package es.us.dp1.lx_xy_24_25.your_game_name.statistics;

import es.us.dp1.lx_xy_24_25.your_game_name.model.NamedEntity;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
public class Achievement extends NamedEntity {
    
    @NotBlank
    private String description;

    private String badgeImage;

    @Min(0)
    private int threshold;

    @Enumerated(EnumType.STRING)
    @NotNull
    Metric metric;

    private String profilePictureUri;
    
    public String getActualDescription(){
        return description.replace("<THRESHOLD>",String.valueOf(threshold));
    }
}
