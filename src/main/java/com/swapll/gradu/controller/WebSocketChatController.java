package com.swapll.gradu.controller;

import com.swapll.gradu.model.Chat;
import com.swapll.gradu.dto.ChatMessageDTO;
import com.swapll.gradu.dto.MessageDTO;
import com.swapll.gradu.service.ChatService;
import com.swapll.gradu.dto.ChatSummaryDTO;
import com.swapll.gradu.repository.UserRepository;
import com.swapll.gradu.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WebSocketChatController {

    @Autowired private ChatService chatService;
    @Autowired private SimpMessagingTemplate messagingTemplate;
    @Autowired private UserRepository userRepository;


    @GetMapping("/with/{receiverId}")
    public ChatSummaryDTO getOrCreateChatWithUser(@PathVariable int receiverId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = auth.getName();

        User sender = userRepository.findByUserNameOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .orElseThrow();

        Chat chat = chatService.getOrCreateChat(sender.getId(), receiverId);
        return chatService.getChatSummary(chat, sender.getId());
    }

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload ChatMessageDTO chatMessageDTO) {
        MessageDTO savedMessage = chatService.saveMessage(chatMessageDTO);

        // Get current authenticated user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = auth.getName();
        User sender = userRepository.findByUserNameOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .orElseThrow();

        // Get the chat after message is saved
        Chat chat = chatService.getOrCreateChat(sender.getId(), chatMessageDTO.getReceiverId());

        // Send the message to both users' chat topics
        messagingTemplate.convertAndSend("/topic/chat." + chat.getId(), savedMessage);
    }

    @MessageMapping("/chat.getMessages.{chatId}")
    @SendTo("/topic/chat.{chatId}")
    public List<MessageDTO> getMessages(@DestinationVariable Integer chatId) {
        return chatService.getChatMessages(chatId);
    }

    @MessageMapping("/chat.getInbox")
    @SendTo("/topic/inbox")
    public List<ChatSummaryDTO> getInbox() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usernameOrEmail = auth.getName();

        User user = userRepository.findByUserNameOrEmailIgnoreCase(usernameOrEmail, usernameOrEmail)
                .orElseThrow();

        return chatService.getUserChats(user.getId());
    }
}
//ddd
