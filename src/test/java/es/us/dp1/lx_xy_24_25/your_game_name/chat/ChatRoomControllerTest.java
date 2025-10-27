package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(value = {ChatRoomController.class}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class))
public class ChatRoomControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ChatRoomService chatRoomService;

    private final String BASE_URL = "/api/v1/chat";

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void createChatRoomWithoutCsrfTest() throws Exception {
        mvc.perform(post(BASE_URL))
            .andExpect(status().isForbidden());
        verify(chatRoomService, never()).createChatRoom();
    }

    @Test
    public void createChatRoomUnauthorizedTest() throws Exception {
        mvc.perform(post(BASE_URL)
                .with(csrf()))
            .andExpect(status().isUnauthorized());
        verify(chatRoomService, never()).createChatRoom();
    }
}
