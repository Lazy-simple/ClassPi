package com.classpi.dto;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username; // 账号（邮箱/手机号/自定义）
    private String password; // 密码
    private String phone; // 手机号（可选，短信登录用）
    private String name; // 姓名
    private String school; // 学校/机构
    private String studentId; // 学号（学生专属）
    private String identity; // 身份：teacher/student
}
