package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("ai_chat")
public class AiChat {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String userId;
    private String userRole;
    private String chatTitle;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}