package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("student_homework")
public class StudentHomework {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long homeworkId;
    private Long studentId;
    private Integer score;
    private String submitContent;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDateTime submitTime;
    private String correctionContent;
    private LocalDateTime correctionTime;
    private Integer corrected;
    @TableLogic
    private Integer deleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
