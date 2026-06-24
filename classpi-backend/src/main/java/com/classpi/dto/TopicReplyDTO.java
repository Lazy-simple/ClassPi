package com.classpi.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TopicReplyDTO {
    @NotNull(message = "话题ID不能为空")
    private Integer topicId;
    
    @NotBlank(message = "回复内容不能为空")
    private String content;
    
    @NotBlank(message = "作者ID不能为空")
    private String authorId;
    
    @NotBlank(message = "作者姓名不能为空")
    private String authorName;
    
    @NotNull(message = "作者类型不能为空")
    private Integer authorType;
    
    private Integer isAnonymous;
}
