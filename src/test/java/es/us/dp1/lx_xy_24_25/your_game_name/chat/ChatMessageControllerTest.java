package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

@WebMvcTest(value = {ChatMessageController.class}, excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = WebSecurityConfiguration.class))
public class ChatMessageControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    ChatMessageService chatMessageService;

    @MockBean
    ChatRoomService chatRoomService;

    private final String BASE_URL = "/api/v1/messages";

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void createChatMessageWithValidDataTest() throws Exception {
        String message = "Hello, world!";
        Integer senderId = 1;
        Integer chatRoomId = 2;
        ChatMessage chatMessageMock = new ChatMessage();
        chatMessageMock.setMessage(message);
        chatMessageMock.setSenderUsername("player");
        when(chatMessageService.createChatMessage(message, senderId, chatRoomId)).thenReturn(chatMessageMock);
        mvc.perform(post(BASE_URL)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content(message) 
                .param("senderId", senderId.toString())
                .param("chatRoomId", chatRoomId.toString()))
            .andExpect(status().isOk());

        verify(chatMessageService).createChatMessage(message, senderId, chatRoomId);
        verify(chatRoomService).addMessageToChatRoomById(chatRoomId, chatMessageMock);
    }

    @Test
    @WithMockUser(username = "player", authorities = "PLAYER")
    public void createChatMessageWithoutCsrfTest() throws Exception {
        String message = "Hello, world!";
        Integer senderId = 1;
        Integer chatRoomId = 2;
        mvc.perform(post(BASE_URL)
                .contentType("application/json")
                .content("\"" + message + "\"")
                .param("senderId", senderId.toString())
                .param("chatRoomId", chatRoomId.toString()))
            .andExpect(status().isForbidden());

        verify(chatMessageService, never()).createChatMessage(any(), any(), any());
        verify(chatRoomService, never()).addMessageToChatRoomById(any(), any());
    }

    @Test
    public void createChatMessageUnauthorizedTest() throws Exception {
        String message = "Hello, world!";
        Integer senderId = 1;
        Integer chatRoomId = 2;
        mvc.perform(post(BASE_URL)
                .with(SecurityMockMvcRequestPostProcessors.csrf())
                .contentType("application/json")
                .content("\"" + message + "\"")
                .param("senderId", senderId.toString())
                .param("chatRoomId", chatRoomId.toString()))
            .andExpect(status().isUnauthorized());
        verify(chatMessageService, never()).createChatMessage(any(), any(), any());
        verify(chatRoomService, never()).addMessageToChatRoomById(any(), any());
    }
}
