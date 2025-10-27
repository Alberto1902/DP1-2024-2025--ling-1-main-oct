package es.us.dp1.lx_xy_24_25.your_game_name.friendship;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;

@Service
public class FriendshipService {

    FriendshipRepository friendshipRepository;
    UserRepository userRepository;

    @Autowired
    public FriendshipService(FriendshipRepository friendshipRepository, UserRepository userRepository) {
        this.friendshipRepository = friendshipRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<User> getFriendshipsByUser(User user) {
        List<Friendship> friends = new ArrayList<>();
        friends.addAll(friendshipRepository.findByUser1AndStatus(user, FriendshipStatus.ACCEPTED));
        friends.addAll(friendshipRepository.findByUser2AndStatus(user, FriendshipStatus.ACCEPTED));
        return friends.stream().map(friendship -> {
            if (friendship.getUser1().equals(user)) {
                return friendship.getUser2();
            } else {
                return friendship.getUser1();
            }
        }).toList();
    }

    @Transactional(readOnly = true)
    public Friendship getFriendshipByUser1AndUser2(User user1, User user2) {
        return friendshipRepository.findByUser1AndUser2AndStatus(user1, user2, FriendshipStatus.PENDING);
    }

    @Transactional
    public Friendship acceptFriendship(Friendship friendship) {
        friendship.setStatus(FriendshipStatus.ACCEPTED);
        return friendshipRepository.save(friendship);
    }

    @Transactional
    public Friendship rejectFriendship(Friendship friendship) {
        friendship.setStatus(FriendshipStatus.REJECTED);
        return friendshipRepository.save(friendship);
    }

    @Transactional(rollbackFor = Exception.class)
    public Friendship createFriendship(User user1, User user2) {
        List<User> alreadyFriends = getFriendshipsByUser(user2);
        List<User> pendingRequests = getFriendshipRequests(user2, "PENDING")
                .stream().map(friendship -> friendship.getUser1()).toList();
        if (alreadyFriends.contains(user1)) {
            throw new IllegalArgumentException("Already friends");
        }
        if (pendingRequests.contains(user1)) {
            throw new IllegalArgumentException("Already requested");
        }
        Friendship friendship = new Friendship();
        friendship.setUser1(user1);
        friendship.setUser2(user2);
        friendship.setStatus(FriendshipStatus.PENDING);
        return friendshipRepository.save(friendship);
    }

    @Transactional(readOnly = true)
    public List<Friendship> getFriendshipRequests(User user2, String status) {
        List<Friendship> requests = new ArrayList<>();
        requests.addAll(friendshipRepository.findByUser2AndStatus(user2, FriendshipStatus.PENDING));
        return requests;
    }

}
