package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkPublishDTO;
import com.classpi.entity.*;
import com.classpi.mapper.HomeworkMapper;
import com.classpi.service.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

    @Resource
    private CourseService courseService;

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private HomeworkNoticeService homeworkNoticeService;
    @Resource
    private UserService userService;

    @Override
    public Result publish(HomeworkPublishDTO dto, Long teacherId) {
        // 1.组装作业
        Homework homework = new Homework();
        homework.setTeacherId(teacherId);
        homework.setCourseId(dto.getCourseId());
        homework.setTitle(dto.getTitle());
        homework.setContent(dto.getContent());
        homework.setFileUrl(dto.getFileUrl());
        homework.setFileName(dto.getFileName());
        homework.setFileType(dto.getFileType());
        homework.setPublishTime(LocalDateTime.now());
        homework.setDeadline(dto.getDeadline());
        homework.setFullScore(dto.getFullScore() != null ? dto.getFullScore() : 100);
        homework.setEnableCheck(dto.getEnableCheck() != null ? dto.getEnableCheck() : 0);
        homework.setCheckThreshold(dto.getCheckThreshold() != null ? dto.getCheckThreshold() : 50);
        homework.setAutoReject(dto.getAutoReject() != null ? dto.getAutoReject() : 0);
        homework.setRejectThreshold(dto.getRejectThreshold() != null ? dto.getRejectThreshold() : 50);
        boolean save = this.save(homework);
        if (!save) {
            return Result.error("作业发布失败");
        }

        // 2.查询全部学生id
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIdentity, "student");
        List<User> studentList = userService.list(wrapper);
        List<Long> studentIds = studentList.stream()
                .filter(sh -> sh.getStudentId() != null && !sh.getStudentId().trim().isEmpty())
                .map(sh -> Long.valueOf(sh.getStudentId()))
                .collect(Collectors.toList());

        // 3.批量生成发布通知
        homeworkNoticeService.batchCreateNotice(homework.getId(), studentIds, "publish");
        return Result.success("发布成功");
    }

    @Override
    public Result remind(Long homeworkId) {
        Homework homework = this.getById(homeworkId);
        if (homework == null) {
            return Result.error("作业不存在");
        }

        // 用 CourseService 获取选课学生
        Result<List<StudentCourse>> courseResult = courseService.getCourseAllStudent(homework.getCourseId().intValue());
        if (courseResult.getCode() != 200) {
            return Result.error("获取选课学生失败");
        }
        List<StudentCourse> enrolledStudents = courseResult.getData();
        List<Long> enrolledIds = enrolledStudents.stream()
                .map(sc -> Long.valueOf(sc.getStudentId()))
                .collect(Collectors.toList());

        if (enrolledIds.isEmpty()) {
            return Result.success("该课程暂无学生选课");
        }

        // 查出已提交的学生
        LambdaQueryWrapper<StudentHomework> submitWrapper = new LambdaQueryWrapper<>();
        submitWrapper.eq(StudentHomework::getHomeworkId, homeworkId);
        List<StudentHomework> submitList = studentHomeworkService.list(submitWrapper);
        List<Long> submitStuIds = submitList.stream()
                .map(sh -> Long.valueOf(sh.getStudentId()))
                .collect(Collectors.toList());

        // 过滤未提交学生
        List<Long> remindIds = enrolledIds.stream()
                .filter(id -> !submitStuIds.contains(id))
                .collect(Collectors.toList());

        if (remindIds.isEmpty()) {
            return Result.success("所有学生已提交，无需催交");
        }

        homeworkNoticeService.batchCreateNotice(homeworkId, remindIds, "remind");
        return Result.success("催交通知已发送");
    }

    @Resource
    private StudentHomeworkService studentHomeworkService;

    @Override
    public Result getHomeworkByCourse(Long courseId) {
        try {
            LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Homework::getCourseId, courseId)
                    .eq(Homework::getDeleted, 0)
                    .orderByDesc(Homework::getCreateTime);
            List<Homework> list = homeworkMapper.selectList(wrapper);
            return Result.success("查询课程作业成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询课程作业失败：" + e.getMessage());
        }
    }

    @Override
    public Result getTeacherHomework(Long teacherId) {
        try {
            LambdaQueryWrapper<Homework> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Homework::getTeacherId, teacherId)
                    .eq(Homework::getDeleted, 0)
                    .orderByDesc(Homework::getCreateTime);
            List<Homework> list = homeworkMapper.selectList(wrapper);
            return Result.success("查询教师作业成功", list);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询教师作业失败：" + e.getMessage());
        }
    }

    @Override
    public Result remindSingle(Long homeworkId, Long studentId) {
        LambdaQueryWrapper<StudentHomework> submitWrapper = new LambdaQueryWrapper<>();
        submitWrapper.eq(StudentHomework::getHomeworkId, homeworkId)
                .eq(StudentHomework::getStudentId, String.valueOf(studentId));
        long count = studentHomeworkService.count(submitWrapper);

        if (count > 0) {
            return Result.error("该学生已提交作业，无需催交");
        }

        List<Long> studentIds = Collections.singletonList(studentId);
        homeworkNoticeService.batchCreateNotice(homeworkId, studentIds, "remind");
        return Result.success("催交通知已发送");
    }
}