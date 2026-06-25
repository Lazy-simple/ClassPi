package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.dto.AiChatRequestDTO;

public interface AiChatService {
    
    Result chat(AiChatRequestDTO request);
    
    Result getChatHistory(Integer chatId);
    
    Result getUserChats(String userId);
    
    Result deleteChat(Integer chatId);
}