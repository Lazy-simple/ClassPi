package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.AiChatRequestDTO;
import com.classpi.service.AiChatService;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin
public class AiChatController {

    @Autowired
    private AiChatService aiChatService;

    @PostMapping("/chat")
    public Result chat(@Valid @RequestBody AiChatRequestDTO request) {
        return aiChatService.chat(request);
    }

    @GetMapping("/chat/{chatId}")
    public Result getChatHistory(@PathVariable Integer chatId) {
        return aiChatService.getChatHistory(chatId);
    }

    @GetMapping("/chats/{userId}")
    public Result getUserChats(@PathVariable String userId) {
        return aiChatService.getUserChats(userId);
    }

    @DeleteMapping("/chat/{chatId}")
    public Result deleteChat(@PathVariable Integer chatId) {
        return aiChatService.deleteChat(chatId);
    }
}