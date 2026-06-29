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
    private Integer fullScore;
    private Integer enableCheck;
    private Integer checkThreshold;
    private Integer autoReject;
    private Integer rejectThreshold;
}