package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("homework_notice")
public class HomeworkNotice {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long homeworkId;
    private Long studentId;
    private String noticeType;
    private Integer isRead;
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
}
