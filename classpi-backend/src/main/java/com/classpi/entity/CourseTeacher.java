package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("course_teacher")
public class CourseTeacher {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer courseId;
    private String teacherId;
    private String teacherName;
    private Integer role;  // 1:创建者, 2:协作者
    private LocalDateTime joinTime;
    @TableLogic
    private Integer deleted;
}
