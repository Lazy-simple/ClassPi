package com.classpi.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResourceCreateDTO {
    @NotNull(message = "课程ID不能为空")
    private Integer courseId;
    
    @NotBlank(message = "课程号不能为空")
    private String courseNo;
    
    @NotBlank(message = "资源名称不能为空")
    @Size(max = 255, message = "资源名称不能超过255个字符")
    private String name;
    
    @NotBlank(message = "资源类型不能为空")
    private String type;
    
    private String url;
    
    private String parentId;
    
    private String folderName;
    
    private Boolean isFolder;
    
    @NotBlank(message = "上传者ID不能为空")
    private String uploaderId;
    
    @NotBlank(message = "上传者姓名不能为空")
    private String uploaderName;
}
