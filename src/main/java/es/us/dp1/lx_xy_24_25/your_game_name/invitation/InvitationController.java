package es.us.dp1.lx_xy_24_25.your_game_name.invitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;

@RestController
@RequestMapping("/api/v1/invitation")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Invitation", description = "Invitation management API")
public class InvitationController {
    
    private final InvitationService invitationService;
    private final GameSessionService gameSessionService;
    private final UserService userService;

    @Autowired
    public InvitationController(InvitationService invitationService, GameSessionService gameSessionService, UserService userService) {
        this.invitationService = invitationService;
        this.gameSessionService = gameSessionService;
        this.userService = userService;
    }

    @Operation(summary = "Create a new invitation", description = "Creates a new invitation between two users for a game session in the specified modality. It is only available for users with the role of PLAYER that are already in the gameSession that is being invited to.")
    @Parameter(name = "sender_id", description = "Id of the user that sends the invitation")
    @Parameter(name = "receiver_id", description = "Id of the user that receives the invitation")
    @Parameter(name = "game_id", description = "Id of the game session that the invitation is for")
    @Parameter(name = "is_spectator", description = "Boolean that indicates if the user is a spectator or a player")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Invitation> createInvitation(@RequestParam("sender_id") Integer senderId, @RequestParam("receiver_id") Integer receiverId, @RequestParam("game_id") Integer gameId, @RequestParam("is_spectator") boolean isSpectator) {
        Invitation invitation = new Invitation();
        invitation.setSender(userService.findUser(senderId));
        invitation.setReceiver(userService.findUser(receiverId));
        invitation.setGame(gameSessionService.findGameSession(gameId));
        invitation.setSpectator(isSpectator);
        Invitation newInvitation = invitationService.createInvitation(invitation);
        return new ResponseEntity<>(newInvitation, HttpStatus.CREATED);
    }

    @Operation(summary = "Accept an invitation", description = "Accepts an invitation to a game session. It is only available for users with the role of PLAYER.")
    @Parameter(name = "id", description = "Id of the invitation")
    @PutMapping(value = "/accept")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Invitation> acceptInvitation(@RequestParam("id") Integer id) {
        Invitation invitation = invitationService.findInvitation(id);
        invitationService.acceptInvitation(invitation);
        GameSession game = invitation.getGame();
        if(!invitation.isSpectator()) {
            game.setCurrentPlayers(game.getCurrentPlayers() + 1);
            game.getPlayers().add(invitation.getReceiver());
            gameSessionService.updateGameSession(game, game.getId());
        } else {
            game.getSpectators().add(invitation.getReceiver());
            gameSessionService.updateGameSession(game, game.getId());
        }
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }
    
    @Operation(summary = "Reject an invitation", description = "Rejects an invitation to a game session. It is only available for users with the role of PLAYER.")
    @Parameter(name = "id", description = "Id of the invitation")
    @PutMapping(value = "/reject")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Invitation> rejectInvitation(@RequestParam("id") Integer id) {
        Invitation invitation = invitationService.findInvitation(id);
        invitationService.rejectInvitation(invitation);
        return new ResponseEntity<>(invitation, HttpStatus.OK);
    }

    @Operation(summary = "Get all invitations of a user that are pending", description = "Returns a list of all invitations of a user that are pending. It is only available for users with the role of PLAYER.")
    @Parameter(name = "receiver_id", description = "Id of the user")
    @GetMapping(value = "/pending")
    @ResponseStatus(HttpStatus.OK)
    public List<Invitation> getInvitations(@RequestParam("receiver_id") Integer id) {
        User receiver = userService.findUser(id);
        return invitationService.getPendingInvitations(receiver);
    }
}
