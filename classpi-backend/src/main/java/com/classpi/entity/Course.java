package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course")
public class Course {
    @TableId(type = IdType.AUTO)
    private Integer id;
    
    private String courseNo; // 课程号
    private String name; // 课程名称
    private String description; // 课程描述
    private String teacherId; // 授课教师ID
    private String teacherName; // 授课教师姓名
    private String department; // 开课院系
    private String classroom; // 上课地点
    private String schedule; // 上课时间
    private Integer credit; // 学分
    private Integer capacity; // 选课容量
    private Integer enrolledCount; // 已选人数
    private Integer status;
    
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}