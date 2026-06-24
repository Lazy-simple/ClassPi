package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkCorrectDTO;
import com.classpi.dto.homework.HomeworkSubmitDTO;
import com.classpi.entity.StudentHomework;
import com.classpi.mapper.StudentHomeworkMapper;
import com.classpi.service.HomeworkService;
import com.classpi.service.StudentHomeworkService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class StudentHomeworkServiceImpl extends ServiceImpl<StudentHomeworkMapper, StudentHomework> implements StudentHomeworkService {

    @Override
    public Result submit(HomeworkSubmitDTO dto, Long studentId) {
        // 判断是否重复提交
        LambdaQueryWrapper<StudentHomework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentHomework::getHomeworkId, dto.getHomeworkId())
                .eq(StudentHomework::getStudentId, studentId);
        StudentHomework exist = this.getOne(wrapper);
        if (exist != null) {
            return Result.error("你已提交该作业，不可重复提交");
        }

        StudentHomework submit = new StudentHomework();
        submit.setHomeworkId(dto.getHomeworkId());
        submit.setStudentId(studentId);
        submit.setSubmitContent(dto.getSubmitContent());
        submit.setFileUrl(dto.getFileUrl());
        submit.setFileName(dto.getFileName());
        submit.setFileType(dto.getFileType());
        submit.setSubmitTime(LocalDateTime.now());
        submit.setCorrected(0);
        boolean save = this.save(submit);
        return save ? Result.success("提交成功") : Result.error("提交失败");
    }

    @Override
    public List<StudentHomework> listByHomeworkId(Long homeworkId) {
        LambdaQueryWrapper<StudentHomework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentHomework::getHomeworkId, homeworkId);
        return this.list(wrapper);
    }

    @Override
    public Result correct(HomeworkCorrectDTO dto, Long teacherId) {
        StudentHomework submit = this.getById(dto.getSubmitId());
        if (submit == null) {
            return Result.error("提交记录不存在");
        }
        submit.setCorrectionContent(dto.getCorrectionContent());
        submit.setCorrectionTime(LocalDateTime.now());
        submit.setCorrected(1);
        boolean update = this.updateById(submit);
        return update ? Result.success("批改完成") : Result.error("批改失败");
    }

    @Override
    public StudentHomework getByHwAndStu(Long homeworkId, Long studentId) {
        LambdaQueryWrapper<StudentHomework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentHomework::getHomeworkId, homeworkId)
                .eq(StudentHomework::getStudentId, studentId);
        return this.getOne(wrapper);
    }
}