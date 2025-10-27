package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/chat")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "ChatRoom", description = "Chat room management API")
public class ChatRoomController {
    
    ChatRoomService chatRoomService;

    @Autowired
    public ChatRoomController(ChatRoomService chatRoomService) {
        this.chatRoomService = chatRoomService;
    }

    @Operation(summary = "Get all messages from a chat room")
    @ApiResponse(responseCode = "200", description = "Messages retrieved", content = {@Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ChatMessage.class)))})
    @GetMapping(value = "/messages")
    public ResponseEntity<List<ChatMessage>> getMessagesByChatRoomId(@RequestParam(value = "id") Integer id) {
        List<ChatMessage> messages = chatRoomService.getMessagesByChatRoomId(id);
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }
    
}
