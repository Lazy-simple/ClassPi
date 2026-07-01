package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkCorrectDTO;
import com.classpi.dto.homework.HomeworkSubmitDTO;
import com.classpi.entity.Homework;
import com.classpi.entity.StudentCourse;
import com.classpi.entity.StudentHomework;
import com.classpi.entity.User;
import com.classpi.mapper.HomeworkMapper;
import com.classpi.mapper.StudentHomeworkMapper;
import com.classpi.service.CourseService;
import com.classpi.service.StudentHomeworkService;
import com.classpi.service.UserService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentHomeworkServiceImpl extends ServiceImpl<StudentHomeworkMapper, StudentHomework> implements StudentHomeworkService {

    @Resource
    private CourseService courseService;

    @Resource
    private UserService userService;

    @Resource
    private HomeworkMapper homeworkMapper;

    @Override
    public Result getUnsubmittedStudents(Long homeworkId) {
        // 1. 获取该作业
        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) {
            return Result.error("作业不存在");
        }

        // 2. 获取选了这门课的学生（用 CourseService）
        Result<List<StudentCourse>> courseResult = courseService.getCourseAllStudent(homework.getCourseId().intValue());
        if (courseResult.getCode() != 200) {
            return Result.error("获取选课学生失败");
        }
        List<StudentCourse> enrolledStudents = courseResult.getData();

        // 如果返回的是 List<StudentCourse>，取 studentId
        List<Long> enrolledStudentIds = enrolledStudents.stream()
                .map(sc -> Long.valueOf(sc.getStudentId()))
                .collect(Collectors.toList());

        if (enrolledStudentIds.isEmpty()) {
            return Result.success("该课程暂无学生选课", Collections.emptyList());
        }

        // 3. 获取已提交的学生
        LambdaQueryWrapper<StudentHomework> shWrapper = new LambdaQueryWrapper<>();
        shWrapper.eq(StudentHomework::getHomeworkId, homeworkId);
        List<StudentHomework> submittedList = this.list(shWrapper);
        List<Long> submittedIds = submittedList.stream()
                .map(sh -> Long.valueOf(sh.getStudentId()))
                .collect(Collectors.toList());

        // 4. 过滤出未提交的学生
        List<Long> unsubmittedIds = enrolledStudentIds.stream()
                .filter(id -> !submittedIds.contains(id))
                .collect(Collectors.toList());

        if (unsubmittedIds.isEmpty()) {
            return Result.success("所有学生已提交", Collections.emptyList());
        }

        // 5. 查询学生信息
        List<User> unsubmittedStudents = userService.listByIds(unsubmittedIds);
        return Result.success("查询成功", unsubmittedStudents);
    }

    @Override
    public Result submit(HomeworkSubmitDTO dto, Long studentId) {
        // 查询是否已有提交记录
        LambdaQueryWrapper<StudentHomework> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(StudentHomework::getHomeworkId, dto.getHomeworkId())
                .eq(StudentHomework::getStudentId, studentId);
        StudentHomework exist = this.getOne(wrapper);

        if (exist != null) {
            // ========== 已有记录：更新（覆盖提交） ==========
            exist.setSubmitContent(dto.getSubmitContent());
            exist.setFileUrl(dto.getFileUrl());
            exist.setFileName(dto.getFileName());
            exist.setFileType(dto.getFileType());
            exist.setSubmitTime(LocalDateTime.now());
            exist.setCorrected(0);  // 重新提交后需要重新批阅
            exist.setCorrectionContent(null);  // 清空之前的评语
            exist.setCorrectionTime(null);
            boolean update = this.updateById(exist);
            return update ? Result.success("重新提交成功") : Result.error("重新提交失败");
        } else {
            // ========== 没有记录：新增 ==========
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

        // ✅ 只保存评语，不加"分数："前缀
        submit.setCorrectionContent(dto.getCorrectionContent());
        submit.setCorrectionTime(LocalDateTime.now());
        submit.setCorrected(1);
        submit.setScore(dto.getScore());  // 分数单独存

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