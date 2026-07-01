package com.classpi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkCorrectDTO;
import com.classpi.dto.homework.HomeworkSubmitDTO;
import com.classpi.entity.StudentHomework;
import java.util.List;

public interface StudentHomeworkService extends IService<StudentHomework> {
    /**
     * 学生提交作业
     */
    Result submit(HomeworkSubmitDTO dto, Long studentId);

    /**
     * 根据作业id查询所有提交记录（教师批阅列表）
     */
    List<StudentHomework> listByHomeworkId(Long homeworkId);

    /**
     * 教师批改作业
     */
    Result correct(HomeworkCorrectDTO dto, Long teacherId);

    /**
     * 查询学生单份作业提交记录
     */
    StudentHomework getByHwAndStu(Long homeworkId, Long studentId);

    /**
     * 获取某作业未提交的学生列表
     */
    Result getUnsubmittedStudents(Long homeworkId);
}