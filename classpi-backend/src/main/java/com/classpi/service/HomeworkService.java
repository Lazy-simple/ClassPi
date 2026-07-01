package com.classpi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkPublishDTO;
import com.classpi.entity.Homework;

public interface HomeworkService extends IService<Homework> {
    /**
     * 教师发布作业
     */
    Result publish(HomeworkPublishDTO dto, Long teacherId);

    /**
     * 催交作业，给未提交学生发通知
     */
    Result remind(Long homeworkId);

    /**
     * 获取某课程的所有作业
     */
    Result getHomeworkByCourse(Long courseId);

    /**
     * 获取教师的所有作业
     */
    Result getTeacherHomework(Long teacherId);  // ✅ 添加这行

    /**
     * 催交单个学生
     */
    Result remindSingle(Long homeworkId, Long studentId);
}
