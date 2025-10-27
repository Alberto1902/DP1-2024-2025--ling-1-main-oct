package es.us.dp1.lx_xy_24_25.your_game_name.invitation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;

import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSession;
import es.us.dp1.lx_xy_24_25.your_game_name.gamesession.GameSessionService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureTestDatabase
public class InvitationServiceTest {

    @Autowired
    protected InvitationService invitationService;

    @Autowired
    protected UserService userService;

    @Autowired
    protected GameSessionService gameSessionService;
    

    @Test
    @Transactional
    void shouldCreateInvitation() {
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        User user2 = userService.findUser(5);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        Invitation newInvitation = invitationService.createInvitation(invitation);
        assertNotNull(newInvitation.getId());
    }

    @Test
    @Transactional
    void shouldFailCreateDuplicateInvitation(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        game = gameSessionService.createGameSession(game);
        User user2 = userService.findUser(5);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        invitationService.createInvitation(invitation);
        Invitation newInvitation = new Invitation();
        newInvitation.setSpectator(false);
        newInvitation.setSender(user);
        newInvitation.setReceiver(user2);
        newInvitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(newInvitation));

    }

    @Test
    @Transactional
    void shouldFailCreateInvitationWithNonFriendSpectator(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(true);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        User user3 = userService.findUser(7);
        game.setCurrentPlayers(2);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        players.add(user2);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user3);
        invitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldFailCreateInvitationWithSenderNotInGame(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(true);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        User user3 = userService.findUser(7);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user2);
        game.setPlayers(players);
        invitation.setSender(user3);
        invitation.setReceiver(user);
        invitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldFailCreateInvitationIfNotFriends(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(4);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldFailCreateInvitationWithReceiverInGame(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(2);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        players.add(user2);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldFailCreateInvitationWithFullGame(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(4);
        User user3 = userService.findUser(6);
        User user4 = userService.findUser(5);
        game.setCurrentPlayers(3);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        players.add(user2);
        players.add(user3);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user4);
        invitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldFailCreateInvitationInNotWaiting(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("IN_PROGRESS");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        assertThrows(IllegalArgumentException.class, () -> invitationService.createInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldAcceptInvitation(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        invitationService.acceptInvitation(invitation);
        assertEquals(invitation.getStatus(), "ACCEPTED");
    }

    @Test
    @Transactional
    void shouldFailAcceptInvitationNotPending(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        invitation.setStatus("ACCEPTED");
        assertThrows(IllegalArgumentException.class, () -> invitationService.acceptInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldRejectInvitation(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        invitationService.rejectInvitation(invitation);
        assertEquals(invitation.getStatus(), "REJECTED");
    }

    @Test
    @Transactional
    void shouldFailRejectInvitationNotPending(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        invitation.setStatus("ACCEPTED");
        assertThrows(IllegalArgumentException.class, () -> invitationService.rejectInvitation(invitation));
    }

    @Test
    @Transactional
    void shouldFindInvitation(){
        Invitation invitation = new Invitation();
        invitation.setSpectator(false);
        GameSession game = new GameSession();
        User user = userService.findUser(3);
        User user2 = userService.findUser(5);
        game.setCurrentPlayers(1);
        game.setMaxPlayers(3);
        game.setStatus("WAITING");
        game.setCreator(user);
        List<User> players = new ArrayList<>();
        players.add(user);
        game.setPlayers(players);
        invitation.setSender(user);
        invitation.setReceiver(user2);
        invitation.setGame(game);
        invitation = invitationService.createInvitation(invitation);
        Invitation foundInvitation = invitationService.findInvitation(invitation.getId());
        assertEquals(invitation, foundInvitation);
    }

    @Test
    @Transactional
    void shouldReturnNullWhenInvitationNotFound(){
        Invitation invitation = invitationService.findInvitation(999);
        assertNull(invitation);
    }

    @Test
    @Transactional
    void shouldGetPendingInvitations(){
        User user = userService.findUser(3);
        List<Invitation> invitations = invitationService.getPendingInvitations(user);
        assertEquals(0, invitations.size());
    }
    
}
