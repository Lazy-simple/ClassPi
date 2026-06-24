package com.classpi.dto.homework;

import lombok.Data;

@Data
public class HomeworkSubmitDTO {
    private Long homeworkId;
    private String submitContent;
    private String fileUrl;
    private String fileName;
    private String fileType;
}