package com.classpi.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class PreparationCreateDTO {
    @NotBlank(message = "教师ID不能为空")
    private String teacherId;
    
    @NotBlank(message = "教师姓名不能为空")
    private String teacherName;
    
    @NotBlank(message = "标题不能为空")
    @Size(max = 255, message = "标题不能超过255个字符")
    private String title;
    
    @NotBlank(message = "类型不能为空")
    private String type;
    
    @NotBlank(message = "内容不能为空")
    private String content;
    
    private Integer courseId;
    
    private String courseNo;
}
