package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("student_course")
public class StudentCourse {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String studentId; // 学生ID
    private String studentName; // 学生姓名
    private Integer courseId; // 课程ID
    private String courseNo; // 课程号
    private String courseName; // 课程名称
    private Integer status; // 选课状态：0-待审核，1-已通过，2-已退选
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}