package es.us.dp1.lx_xy_24_25.your_game_name.cards;

import es.us.dp1.lx_xy_24_25.your_game_name.model.BaseEntity;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Card extends BaseEntity{

    @NotBlank
    @Size(min = 1, max = 1)
    private String letter;

    @NotBlank
    private String title;

    private String image;

    private String letterImage;

}
