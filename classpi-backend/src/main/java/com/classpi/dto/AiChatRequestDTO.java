package com.classpi.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.Data;

@Data
public class AiChatRequestDTO {

    @NotBlank(message = "用户ID不能为空")
    private String userId;

    @NotBlank(message = "用户角色不能为空")
    private String userRole;

    @NotBlank(message = "消息内容不能为空")
    @Size(max = 2000, message = "消息内容不能超过2000字")
    private String content;

    private Integer chatId;
}