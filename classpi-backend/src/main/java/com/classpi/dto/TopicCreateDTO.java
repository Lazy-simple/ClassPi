package com.classpi.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class TopicCreateDTO {
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    @NotBlank(message = "课程号不能为空")
    private String courseNo;
    
    @NotBlank(message = "话题标题不能为空")
    @Size(max = 255, message = "话题标题不能超过255个字符")
    private String title;
    
    @NotBlank(message = "话题内容不能为空")
    private String content;
    
    @NotBlank(message = "作者ID不能为空")
    private String authorId;
    
    @NotBlank(message = "作者姓名不能为空")
    private String authorName;
    
    @NotNull(message = "作者类型不能为空")
    private Integer authorType;
    
    private Integer isAnonymous;
}
