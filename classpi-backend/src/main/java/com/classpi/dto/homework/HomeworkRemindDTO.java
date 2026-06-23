package com.classpi.dto.homework;

import lombok.Data;
import java.util.List;

@Data
public class HomeworkRemindDTO {
    private Long homeworkId;
    private List<Long> studentIds;
}
