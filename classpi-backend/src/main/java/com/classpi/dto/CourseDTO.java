package com.classpi.dto;

import lombok.Data;

@Data
public class CourseDTO {
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
}