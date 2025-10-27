package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/messages")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ChatMessage", description = "Chat message management API")
public class ChatMessageController {
    
    ChatMessageService chatMessageService;
    ChatRoomService chatRoomService;

    @Autowired
    public ChatMessageController(ChatMessageService chatMessageService, ChatRoomService chatRoomService) {
        this.chatMessageService = chatMessageService;
        this.chatRoomService = chatRoomService;
    }

    @Operation(summary = "Create a new message")
    @ApiResponse(responseCode = "201", description = "Message created")
    @PostMapping
    public ChatMessage createChatMessage(@Valid @RequestBody String message, @RequestParam(value = "senderId") Integer senderId, @RequestParam(value = "chatRoomId") Integer chatRoomId) {
        ChatMessage newMessage = chatMessageService.createChatMessage(message, senderId, chatRoomId);
        chatRoomService.addMessageToChatRoomById(chatRoomId, newMessage);
        return newMessage;
    }
}
