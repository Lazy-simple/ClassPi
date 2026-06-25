package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_message")
public class AiMessage {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private Integer chatId;
    private String senderType;
    private String content;
    private Integer messageIndex;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}