package com.classpi.dto.homework;

import lombok.Data;

@Data
public class HomeworkCorrectDTO {
    private Long submitId;
    private String correctionContent;
    private Integer score;  // 新增分数
}