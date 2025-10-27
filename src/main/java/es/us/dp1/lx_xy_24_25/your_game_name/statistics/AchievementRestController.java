package es.us.dp1.lx_xy_24_25.your_game_name.statistics;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.exceptions.BadRequestException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/achievements")
@Tag(name = "Achievements", description = "Achievements management API")
@SecurityRequirement(name = "bearerAuth")
public class AchievementRestController {
    private final AchievementService achievementService;

    @Autowired
    public AchievementRestController(AchievementService achievementService) {
        this.achievementService = achievementService;
    }

    @Operation(summary = "Get all achievements", description = "Returns a list of all achievements. It shall be available for users of all roles")
    @GetMapping
	public ResponseEntity<?> findAll(Pageable pageable) {
		return new ResponseEntity<>(achievementService.getAchievements(pageable), HttpStatus.OK);
	}

    @Operation(summary = "Get an achievement by its id", description = "Returns an achievement given its id. It shall be available for users of all roles")
    @Parameter(name = "id", description = "Achievement id")
    @GetMapping("/{id}")
    public ResponseEntity<Achievement> findAchievement(@PathVariable("id") int id) {
        Achievement achievement = achievementService.getById(id);
        if (achievement == null) {
            throw new ResourceNotFoundException("Achievement with id " + id + " not found");
        }
        return new ResponseEntity<Achievement>(achievement, HttpStatus.OK);
    }

    @Operation(summary = "Create an achievement", description = "Creates a new achievement. It is only available for users with the role of ADMIN")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Achievement object to be published", required = true)
    @PostMapping
    public ResponseEntity<Achievement> createAchievement(@RequestBody @Valid Achievement achievement, BindingResult br) {
        Achievement result = null;
        if(!br.hasErrors()) {
            result = achievementService.saveAchievement(achievement);
        }
        else {
            throw new BadRequestException(br.getAllErrors());
        }
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modify an achievement", description = "Modifies an achievement given its id. It is only available for users with the role of ADMIN")
    @Parameter(name = "id", description = "Achievement id")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Achievement object to be modified", required = true)
    public ResponseEntity<Void> modifyAchievement(@RequestBody @Valid Achievement achievement, BindingResult br, @PathVariable("id") int id) {
        Achievement achievementToUpdate = this.findAchievement(id).getBody();
        if(br.hasErrors()) {
            throw new BadRequestException(br.getAllErrors());
        }
        else if(achievement.getId() == null || !achievement.getId().equals(id)) {
            throw new BadRequestException("Achievement id does not exist)");
        }
        else{
            BeanUtils.copyProperties(achievement, achievementToUpdate, "id");
            achievementService.saveAchievement(achievementToUpdate);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Delete an achievement", description = "Deletes an achievement given its id. It is only available for users with the role of ADMIN")
    @Parameter(name = "id", description = "Achievement id to be deleted")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAchievement (@PathVariable("id") int id){
        findAchievement(id);
        achievementService.deleteAchievementById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
