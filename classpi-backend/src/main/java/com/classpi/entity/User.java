package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("user")
public class User {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String username; // 账号（邮箱/手机号/自定义账号）
    private String password;
    private Integer role; // 2=普通用户（保留）
    private Integer deleted;

    // 新增字段（对应数据库）
    private String phone; // 手机号
    private String name; // 姓名
    private String school; // 学校/机构
    private String studentId; // 学号（驼峰命名，MyBatis-Plus自动映射student_id）
    private String identity; // 身份：teacher/student

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}