package com.classpi.dto;

import lombok.Data;

@Data
public class ImportPreparationDTO {
    private Long preparationId;
    private Integer courseId;
    private String courseNo;
    private String uploaderId;
    private String uploaderName;
    private String parentId;
}
