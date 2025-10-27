package es.us.dp1.lx_xy_24_25.your_game_name.invitation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import es.us.dp1.lx_xy_24_25.your_game_name.friendship.FriendshipService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Service
public class InvitationService {

    private final InvitationRepository invitationRepository;
    private final FriendshipService friendshipService;

    @Autowired
    public InvitationService(InvitationRepository invitationRepository, FriendshipService friendshipService) {
        this.invitationRepository = invitationRepository;
        this.friendshipService = friendshipService;
    }

    @Transactional(rollbackFor = Exception.class)
    public Invitation createInvitation(Invitation invitation) {
        Invitation newInvitation = new Invitation();
        Invitation existingInvitation = invitationRepository.findByReceiverIdAndGameId(invitation.getReceiver().getId(), invitation.getGame().getId()).orElse(null);
        if(existingInvitation != null) {
            throw new IllegalArgumentException("Invitation already exists");
        }
        if(invitation.isSpectator()){
            for(User player : invitation.getGame().getPlayers()) {
                if(!friendshipService.getFriendshipsByUser(player).contains(invitation.getReceiver())) {
                    throw new IllegalArgumentException("The spectator must be a friend of all the players in the game");
                }
            }
        }
        if(!invitation.getGame().getPlayers().contains(invitation.getSender())) {
            throw new IllegalArgumentException("Sender must be a player of the game");
        }
        newInvitation.setSender(invitation.getSender());
        if(!friendshipService.getFriendshipsByUser(invitation.getSender()).contains(invitation.getReceiver())) {
            throw new IllegalArgumentException("Sender and receiver must be friends");
        }
        if(invitation.getGame().getPlayers().contains(invitation.getReceiver()) || invitation.getGame().getSpectators().contains(invitation.getReceiver())) {
            throw new IllegalArgumentException("Receiver is already in the game");
        }
        newInvitation.setReceiver(invitation.getReceiver());
        if(!invitation.isSpectator() && invitation.getGame().getCurrentPlayers() == invitation.getGame().getMaxPlayers()) {
            throw new IllegalArgumentException("Game is full");
        }
        if(!invitation.isSpectator() && !invitation.getGame().getStatus().equals("WAITING")) {
            throw new IllegalArgumentException("Game must be waiting");
        }
        newInvitation.setGame(invitation.getGame());
        newInvitation.setSpectator(invitation.isSpectator());
        return invitationRepository.save(newInvitation);
    }

    @Transactional
    public void acceptInvitation(Invitation invitation) {
        if(!invitation.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Invitation must be pending");
        }
        invitation.setStatus("ACCEPTED");
        invitationRepository.save(invitation);
    }

    @Transactional
    public void rejectInvitation(Invitation invitation) {
        if(!invitation.getStatus().equals("PENDING")) {
            throw new IllegalArgumentException("Invitation must be pending");
        }
        invitation.setStatus("REJECTED");
        invitationRepository.save(invitation);
    }

    @Transactional(readOnly = true)
    public Invitation findInvitation(Integer id) {
        return invitationRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Invitation> getPendingInvitations(User user) {
        return invitationRepository.findByReceiverIdAndStatus(user.getId(), "PENDING");
    }
    
}
