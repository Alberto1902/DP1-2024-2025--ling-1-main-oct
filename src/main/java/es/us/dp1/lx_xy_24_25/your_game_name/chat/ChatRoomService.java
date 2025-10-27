package es.us.dp1.lx_xy_24_25.your_game_name.chat;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChatRoomService {

    ChatRoomRepository chatRoomRepository;

    @Autowired
    public ChatRoomService(ChatRoomRepository chatRoomRepository) {
        this.chatRoomRepository = chatRoomRepository;
    }

    @Transactional
    public ChatRoom createChatRoom() {
        ChatRoom chatRoom = new ChatRoom();
        return chatRoomRepository.save(chatRoom);
    }

    @Transactional
    public ChatRoom addMessageToChatRoomById(Integer id, ChatMessage message) {
        ChatRoom chatRoom = chatRoomRepository.findById(id).orElse(null);
        if (chatRoom != null) {
            List<ChatMessage> messages = chatRoom.getMessages();
            messages.add(message);
            chatRoom.setMessages(messages);
            return chatRoomRepository.save(chatRoom);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getMessagesByChatRoomId(Integer id) {
        return chatRoomRepository.getMessagesById(id);
    }
    
}
