package com.classpi.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class AiChatResponseDTO {
    
    private Integer chatId;
    private String chatTitle;
    private String response;
    private List<MessageItem> history;
    
    @Data
    public static class MessageItem {
        private Integer id;
        private String senderType;
        private String content;
        private LocalDateTime createTime;
    }
}