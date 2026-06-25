package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.classpi.common.Result;
import com.classpi.dto.AiChatRequestDTO;
import com.classpi.dto.AiChatResponseDTO;
import com.classpi.entity.AiChat;
import com.classpi.entity.AiMessage;
import com.classpi.mapper.AiChatMapper;
import com.classpi.mapper.AiMessageMapper;
import com.classpi.service.AiChatService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiChatServiceImpl implements AiChatService {

    @Autowired
    private AiChatMapper aiChatMapper;

    @Autowired
    private AiMessageMapper aiMessageMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${deepseek.api.url:https://api.deepseek.com/v1/chat/completions}")
    private String deepseekApiUrl;

    @Value("${deepseek.api.key:EMPTY}")
    private String deepseekApiKey;

    private static final int CONTEXT_WINDOW = 10;

    @Override
    @Transactional
    public Result chat(AiChatRequestDTO request) {
        try {
            AiChat chat = getOrCreateChat(request.getUserId(), request.getUserRole(), request.getChatId());

            List<AiMessage> historyMessages = aiMessageMapper.findByChatId(chat.getId());
            List<AiChatResponseDTO.MessageItem> contextMessages = buildContext(historyMessages);

            String aiResponse = callDeepSeek(request.getContent(), contextMessages);

            AiMessage userMessage = new AiMessage();
            userMessage.setChatId(chat.getId());
            userMessage.setSenderType("user");
            userMessage.setContent(request.getContent());
            userMessage.setMessageIndex(historyMessages.size());
            aiMessageMapper.insert(userMessage);

            AiMessage aiMessage = new AiMessage();
            aiMessage.setChatId(chat.getId());
            aiMessage.setSenderType("assistant");
            aiMessage.setContent(aiResponse);
            aiMessage.setMessageIndex(historyMessages.size() + 1);
            aiMessageMapper.insert(aiMessage);

            if (chat.getChatTitle() == null || chat.getChatTitle().isEmpty()) {
                chat.setChatTitle(truncateContent(request.getContent(), 20));
                aiChatMapper.updateById(chat);
            }

            AiChatResponseDTO response = new AiChatResponseDTO();
            response.setChatId(chat.getId());
            response.setChatTitle(chat.getChatTitle());
            response.setResponse(aiResponse);
            response.setHistory(buildHistoryResponse(historyMessages, userMessage, aiMessage));

            return Result.success("success", response);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("对话失败: " + e.getMessage());
        }
    }

    @Override
    public Result getChatHistory(Integer chatId) {
        try {
            AiChat chat = aiChatMapper.selectById(chatId);
            if (chat == null) {
                return Result.error("对话不存在");
            }

            List<AiMessage> messages = aiMessageMapper.findByChatId(chatId);

            AiChatResponseDTO response = new AiChatResponseDTO();
            response.setChatId(chat.getId());
            response.setChatTitle(chat.getChatTitle());
            response.setHistory(buildHistoryResponse(messages));

            return Result.success("success", response);
        } catch (Exception e) {
            return Result.error("获取历史失败: " + e.getMessage());
        }
    }

    @Override
    public Result getUserChats(String userId) {
        try {
            List<AiChat> chats = aiChatMapper.findByUserId(userId);
            return Result.success("success", chats);
        } catch (Exception e) {
            return Result.error("获取对话列表失败: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result deleteChat(Integer chatId) {
        try {
            aiMessageMapper.delete(new LambdaQueryWrapper<AiMessage>().eq(AiMessage::getChatId, chatId));
            aiChatMapper.deleteById(chatId);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    private AiChat getOrCreateChat(String userId, String userRole, Integer chatId) {
        if (chatId != null) {
            AiChat existing = aiChatMapper.selectById(chatId);
            if (existing != null && existing.getUserId().equals(userId)) {
                return existing;
            }
        }

        AiChat newChat = new AiChat();
        newChat.setUserId(userId);
        newChat.setUserRole(userRole);
        newChat.setChatTitle("新对话");
        aiChatMapper.insert(newChat);
        return newChat;
    }

    private List<AiChatResponseDTO.MessageItem> buildContext(List<AiMessage> messages) {
        List<AiChatResponseDTO.MessageItem> context = new ArrayList<>();
        int startIndex = Math.max(0, messages.size() - CONTEXT_WINDOW);

        for (int i = startIndex; i < messages.size(); i++) {
            AiMessage msg = messages.get(i);
            AiChatResponseDTO.MessageItem item = new AiChatResponseDTO.MessageItem();
            item.setSenderType(msg.getSenderType());
            item.setContent(msg.getContent());
            context.add(item);
        }

        return context;
    }

    private String callDeepSeek(String userContent, List<AiChatResponseDTO.MessageItem> context) throws Exception {
        if ("EMPTY".equals(deepseekApiKey)) {
            return generateMockResponse(userContent);
        }

        String systemPrompt = "你是一个智能助教，擅长解答学生和教师的问题，包括课程知识、作业辅导等。请用简洁明了的语言回答问题。";

        List<Object> messages = new ArrayList<>();
        messages.add(new java.util.HashMap<String, Object>() {
            {
                put("role", "system");
                put("content", systemPrompt);
            }
        });

        for (AiChatResponseDTO.MessageItem item : context) {
            messages.add(new java.util.HashMap<String, Object>() {
                {
                    put("role", "user".equals(item.getSenderType()) ? "user" : "assistant");
                    put("content", item.getContent());
                }
            });
        }

        messages.add(new java.util.HashMap<String, Object>() {
            {
                put("role", "user");
                put("content", userContent);
            }
        });

        java.util.HashMap<String, Object> requestBody = new java.util.HashMap<>();
        requestBody.put("model", "deepseek-chat");
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);
        requestBody.put("max_tokens", 1024);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + deepseekApiKey);

        HttpEntity<Object> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                deepseekApiUrl,
                HttpMethod.POST,
                entity,
                String.class);

        JsonNode root = objectMapper.readTree(response.getBody());
        return root.get("choices").get(0).get("message").get("content").asText();
    }

    private String generateMockResponse(String userContent) {
        String lowerContent = userContent.toLowerCase();

        if (lowerContent.contains("作业") || lowerContent.contains("homework")) {
            return "好的，关于作业问题，我可以帮你解答。请告诉我具体是哪一门课程的作业，以及你遇到的具体问题。我会尽力为你提供帮助！";
        } else if (lowerContent.contains("课程") || lowerContent.contains("course")) {
            return "课程相关的问题我很乐意解答！你可以问我关于课程内容、学习方法或者课程安排等方面的问题。";
        } else if (lowerContent.contains("老师") || lowerContent.contains("teacher")) {
            return "有什么关于教师的问题吗？我可以帮你了解教师的教学特点、办公时间或者联系方式等信息。";
        } else if (lowerContent.contains("考试") || lowerContent.contains("exam")) {
            return "考试复习是很重要的！建议你提前制定复习计划，重点复习课堂笔记和作业，多做练习题巩固知识。祝你考试顺利！";
        } else if (lowerContent.contains("你好") || lowerContent.contains("hello") || lowerContent.contains("hi")) {
            return "你好！我是你的智能助教，很高兴为你服务。有什么我可以帮助你的吗？";
        } else {
            return "感谢你的提问！这是一个很好的问题。我来帮你分析一下：" + userContent
                    + "\n\n根据我的分析，我认为可以从以下几个方面来考虑...（此处为模拟AI回复，配置DeepSeek API密钥后可获得真实AI回答）";
        }
    }

    private List<AiChatResponseDTO.MessageItem> buildHistoryResponse(List<AiMessage> messages) {
        return buildHistoryResponse(messages, null, null);
    }

    private List<AiChatResponseDTO.MessageItem> buildHistoryResponse(List<AiMessage> messages,
            AiMessage userMsg,
            AiMessage aiMsg) {
        List<AiChatResponseDTO.MessageItem> history = new ArrayList<>();

        for (AiMessage msg : messages) {
            AiChatResponseDTO.MessageItem item = new AiChatResponseDTO.MessageItem();
            item.setId(msg.getId());
            item.setSenderType(msg.getSenderType());
            item.setContent(msg.getContent());
            item.setCreateTime(msg.getCreateTime());
            history.add(item);
        }

        if (userMsg != null) {
            AiChatResponseDTO.MessageItem item = new AiChatResponseDTO.MessageItem();
            item.setId(userMsg.getId());
            item.setSenderType(userMsg.getSenderType());
            item.setContent(userMsg.getContent());
            item.setCreateTime(userMsg.getCreateTime());
            history.add(item);
        }

        if (aiMsg != null) {
            AiChatResponseDTO.MessageItem item = new AiChatResponseDTO.MessageItem();
            item.setId(aiMsg.getId());
            item.setSenderType(aiMsg.getSenderType());
            item.setContent(aiMsg.getContent());
            item.setCreateTime(aiMsg.getCreateTime());
            history.add(item);
        }

        return history;
    }

    private String truncateContent(String content, int maxLength) {
        if (content == null)
            return "";
        return content.length() > maxLength ? content.substring(0, maxLength) + "..." : content;
    }
}