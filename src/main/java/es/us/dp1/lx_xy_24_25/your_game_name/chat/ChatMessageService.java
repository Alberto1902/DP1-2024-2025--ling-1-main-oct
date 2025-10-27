package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import es.us.dp1.lx_xy_24_25.your_game_name.user.User;
import es.us.dp1.lx_xy_24_25.your_game_name.user.UserRepository;

@Service
public class ChatMessageService{
    
    ChatMessageRepository chatMessageRepository;
    UserRepository userRepository;
    ChatRoomRepository chatRoomRepository;
    SimpMessagingTemplate template;

    @Autowired
    public ChatMessageService(ChatMessageRepository chatMessageRepository, UserRepository userRepository, ChatRoomRepository chatRoomRepository, SimpMessagingTemplate template) {
        this.chatMessageRepository = chatMessageRepository;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
        this.template = template;
    }

    @Transactional
    public ChatMessage createChatMessage(String message, Integer senderId, Integer chatRoomId) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setMessage(message);
        User sender = userRepository.findById(senderId).orElse(null);
        chatMessage.setSenderUsername(sender.getUsername());
        chatMessage.setChatRoom(chatRoomRepository.findById(chatRoomId).orElse(null));  
        chatMessage.setTimestamp(LocalDateTime.now());
        this.template.convertAndSend("/topic/chat", chatMessage);
        return chatMessageRepository.save(chatMessage);
    }

}
