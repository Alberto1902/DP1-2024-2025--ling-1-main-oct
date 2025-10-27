package es.us.dp1.lx_xy_24_25.your_game_name.friendship;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/friendship")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Friendship", description = "Friendship management API")
public class FriendshipController {
    
    FriendshipService friendshipService;
    UserService userService;

    @Autowired
    public FriendshipController(FriendshipService friendshipService, UserService userService) {
        this.friendshipService = friendshipService;
        this.userService = userService;
    }

    @Operation(summary = "Get all friends of a user given its username", description= "Returns a list of all friends of a user given its username, it is only available for users with the role of PLAYER")
    @Parameter(name = "username", description = "Username of the user")
    @GetMapping(value = "/friends")
    public ResponseEntity<List<User>> getFriends(@RequestParam(value="username") String username) {
        User user = userService.findUser(username);
        return new ResponseEntity<>(friendshipService.getFriendshipsByUser(user), HttpStatus.OK);
    }

    @Operation(summary = "Accept a friendship request", description= "Accept a friendship request between two users given their usernames, it is only available for users with the role of PLAYER")
    @Parameter(name = "username1", description = "Username of the first user")
    @Parameter(name = "username2", description = "Username of the second user")
    @PutMapping(value = "/accept")
    public ResponseEntity<Friendship> acceptFriendship(@RequestParam(value="username1") String username1, @RequestParam(value="username2") String username2) {
        User user1 = userService.findUser(username1);
        User user2 = userService.findUser(username2);
        Friendship friendship = friendshipService.getFriendshipByUser1AndUser2(user1, user2);
        Friendship updatedFriendship = friendshipService.acceptFriendship(friendship);
        return new ResponseEntity<>(updatedFriendship, HttpStatus.OK);
    }

    @Operation(summary = "Reject a friendship request", description= "Reject a friendship request between two users given their usernames, it is only available for users with the role of PLAYER")
    @Parameter(name = "username1", description = "Username of the first user")
    @Parameter(name = "username2", description = "Username of the second user")
    @PutMapping(value = "/reject")
    public ResponseEntity<Friendship> rejectFriendship(@RequestParam(value="username1") String username1, @RequestParam(value="username2") String username2) {
        User user1 = userService.findUser(username1);
        User user2 = userService.findUser(username2);
        Friendship friendship = friendshipService.getFriendshipByUser1AndUser2(user1, user2);
        Friendship updatedFriendship = friendshipService.rejectFriendship(friendship);
        return new ResponseEntity<>(updatedFriendship, HttpStatus.OK);
    }

    @Operation(summary = "Send a friendship request", description= "Send a friendship request between two users given their usernames, it is only available for users with the role of PLAYER")
    @Parameter(name = "username1", description = "Username of the first user")
    @Parameter(name = "username2", description = "Username of the second user")
    @PostMapping(value = "/sendRequest")
    public ResponseEntity<Friendship> sendFriendshipRequest(@RequestParam(value="username1") String username1, @RequestParam(value="username2") String username2) {
        User user1 = userService.findUser(username1);
        User user2 = userService.findUser(username2);
        Friendship newFriendship = friendshipService.createFriendship(user1, user2);
        return new ResponseEntity<>(newFriendship, HttpStatus.CREATED);
    }

    @Operation(summary = "Get all friendship requests of a user given its username that are in a given status", description= "Returns a list of all friendship requests of a user, in the given status, given its username, it is only available for users with the role of PLAYER")
    @Parameter(name = "username", description = "Username of the user")
    @Parameter(name = "status", description = "Status of the friendship request")
    @GetMapping(value = "/requests")
    public ResponseEntity<List<Friendship>> getFriendshipRequests(@RequestParam(value="username") String username, @RequestParam(value="status") String status) {
        User user = userService.findUser(username);
        return new ResponseEntity<List<Friendship>>(friendshipService.getFriendshipRequests(user, status), HttpStatus.OK);
    }


}
