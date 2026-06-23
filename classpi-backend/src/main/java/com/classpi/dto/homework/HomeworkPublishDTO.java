package com.classpi.dto.homework;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class HomeworkPublishDTO {
    private Long courseId;
    private String title;
    private String content;
    private String fileUrl;
    private String fileName;
    private String fileType;
    private LocalDateTime deadline;
}