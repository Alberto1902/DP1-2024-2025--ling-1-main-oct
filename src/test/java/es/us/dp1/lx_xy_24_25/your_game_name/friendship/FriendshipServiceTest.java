package es.us.dp1.lx_xy_24_25.your_game_name.friendship;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;

@ExtendWith(MockitoExtension.class)
public class FriendshipServiceTest {

    @Mock
    private FriendshipRepository friendshipRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FriendshipService friendshipService;

    private User user1;
    private User user2;
    private User user3;
    private Friendship friendship;

    @BeforeEach
    void setup() {
        user1 = new User();
        user1.setId(1);

        user2 = new User();
        user2.setId(2);

        user3 = new User();
        user3.setId(3);

        friendship = new Friendship();
        friendship.setId(1);
        friendship.setUser1(user1);
        friendship.setUser2(user2);
        friendship.setStatus(FriendshipStatus.PENDING);
    }

    @Test
    void shouldCreateFriendship() {
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship createdFriendship = friendshipService.createFriendship(user1, user3);

        assertNotNull(createdFriendship);
        assertEquals(FriendshipStatus.PENDING, createdFriendship.getStatus());
    }

    @Test
    void shouldGetFriendshipsByUser() {
        when(friendshipRepository.findByUser1AndStatus(user1, FriendshipStatus.ACCEPTED))
            .thenReturn(List.of(friendship));
        when(friendshipRepository.findByUser2AndStatus(user1, FriendshipStatus.ACCEPTED))
            .thenReturn(new ArrayList<>());

        List<User> friends = friendshipService.getFriendshipsByUser(user1);
        assertNotNull(friends);
        assertEquals(1, friends.size());
        assertEquals(user2, friends.get(0));
    }

    @Test
    void shouldAcceptFriendship() {
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship acceptedFriendship = friendshipService.acceptFriendship(friendship);

        assertEquals(FriendshipStatus.ACCEPTED, acceptedFriendship.getStatus());
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void shouldRejectFriendship() {
        friendship.setStatus(FriendshipStatus.PENDING);
        when(friendshipRepository.save(any(Friendship.class))).thenReturn(friendship);

        Friendship rejectedFriendship = friendshipService.rejectFriendship(friendship);

        assertEquals(FriendshipStatus.REJECTED, rejectedFriendship.getStatus());
        verify(friendshipRepository, times(1)).save(friendship);
    }

    @Test
    void shouldGetFriendshipRequestsForUser() {
        when(friendshipRepository.findByUser2AndStatus(user2, FriendshipStatus.PENDING))
            .thenReturn(List.of(friendship));

        List<Friendship> requests = friendshipService.getFriendshipRequests(user2, FriendshipStatus.PENDING.name());

        assertNotNull(requests);
        assertEquals(1, requests.size());
        assertEquals(friendship, requests.get(0));
        verify(friendshipRepository, times(1)).findByUser2AndStatus(user2, FriendshipStatus.PENDING);
    }
}
