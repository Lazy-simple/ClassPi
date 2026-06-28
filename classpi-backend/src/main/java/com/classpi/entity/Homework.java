package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("homework")
public class Homework {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long teacherId;
    @TableField(exist = false)
    private Long courseId;
    private String title;
    private String content;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDateTime publishTime;
    private LocalDateTime deadline;
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
