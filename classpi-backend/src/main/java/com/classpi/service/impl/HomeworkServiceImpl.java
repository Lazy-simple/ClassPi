package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkPublishDTO;
import com.classpi.entity.Homework;
import com.classpi.entity.StudentHomework;
import com.classpi.entity.User;
import com.classpi.mapper.HomeworkMapper;
import com.classpi.service.HomeworkNoticeService;
import com.classpi.service.HomeworkService;
import com.classpi.service.StudentHomeworkService;
import com.classpi.service.UserService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class HomeworkServiceImpl extends ServiceImpl<HomeworkMapper, Homework> implements HomeworkService {

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
        boolean save = this.save(homework);
        if (!save) {
            return Result.error("作业发布失败");
        }

        // 2.查询全部学生id
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIdentity, "student");
        List<User> studentList = userService.list(wrapper);
        List<Long> studentIds = studentList.stream()
                .map(sh -> Long.valueOf(sh.getStudentId()))
                .collect(Collectors.toList());

        // 3.批量生成发布通知
        homeworkNoticeService.batchCreateNotice(homework.getId(), studentIds, "publish");
        return Result.success("发布成功");
    }

    @Override
    public Result remind(Long homeworkId) {
        // 1.获取全部学生
        LambdaQueryWrapper<User> stuWrapper = new LambdaQueryWrapper<>();
        stuWrapper.eq(User::getIdentity, "student");
        List<User> allStudent = userService.list(stuWrapper);
        List<Long> allStuIds = allStudent.stream().map(user -> Long.valueOf(user.getId())).collect(Collectors.toList());

        // 2.查出已提交的学生
        LambdaQueryWrapper<StudentHomework> submitWrapper = new LambdaQueryWrapper<>();
        submitWrapper.eq(StudentHomework::getHomeworkId, homeworkId);
        List<StudentHomework> submitList = studentHomeworkService.list(submitWrapper);
        List<Long> submitStuIds = submitList.stream().map(sh -> Long.valueOf(sh.getStudentId())).collect(Collectors.toList());;

        // 3.过滤未提交学生
        List<Long> remindIds = allStuIds.stream()
                .filter(id -> !submitStuIds.contains(id))
                .collect(Collectors.toList());
        if (remindIds.isEmpty()) {
            return Result.success("所有学生已提交，无需催交");
        }
        // 4.批量生成催交通知
        homeworkNoticeService.batchCreateNotice(homeworkId, remindIds, "remind");
        return Result.success("催交通知已发送");
    }

    @Resource
    private StudentHomeworkService studentHomeworkService;
}