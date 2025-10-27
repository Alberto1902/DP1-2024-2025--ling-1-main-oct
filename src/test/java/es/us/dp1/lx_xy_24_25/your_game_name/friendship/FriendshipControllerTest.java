package es.us.dp1.lx_xy_24_25.your_game_name.friendship;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import java.util.List;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserService;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(value = {
        FriendshipController.class }, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class))
public class FriendshipControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private FriendshipService friendshipService;

    @MockBean
    private UserService userService;

    private final String BASE_URL = "/api/v1/friendship/sendRequest";

    @Test
    @WithMockUser(username = "user1", authorities = "PLAYER")
    public void sendFriendshipRequestShouldReturnCreatedWhenValidRequest() throws Exception {
        User user1 = new User();
        user1.setUsername("user1");
        User user2 = new User();
        user2.setUsername("user2");

        Friendship newFriendship = new Friendship();
        newFriendship.setUser1(user1);
        newFriendship.setUser2(user2);

        when(userService.findUser("user1")).thenReturn(user1);
        when(userService.findUser("user2")).thenReturn(user2);
        when(friendshipService.getFriendshipsByUser(user2)).thenReturn(List.of());
        when(friendshipService.getFriendshipRequests(user2, "PENDING")).thenReturn(List.of());
        when(friendshipService.createFriendship(user1, user2)).thenReturn(newFriendship);

        mvc.perform(post(BASE_URL)
                .with(csrf())
                .param("username1", "user1")
                .param("username2", "user2"))
                .andExpect(status().isCreated());

        verify(friendshipService).createFriendship(user1, user2);
    }
}
