package com.classpi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkPublishDTO;
import com.classpi.entity.Homework;

public interface HomeworkService extends IService<Homework> {
    /**
     * 教师发布作业
     * @param dto 发布参数
     * @param teacherId 当前教师id
     */
    Result publish(HomeworkPublishDTO dto, Long teacherId);

    /**
     * 催交作业，给未提交学生发通知
     * @param homeworkId 作业id
     */
    Result remind(Long homeworkId);
}
