package com.classpi.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classpi.common.PageResult;
import com.classpi.common.Result;
import com.classpi.entity.Homework;
import com.classpi.entity.StudentHomework;
import com.classpi.entity.User;
import com.classpi.mapper.HomeworkMapper;
import com.classpi.mapper.StudentHomeworkMapper;
import com.classpi.mapper.UserMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/score")
@CrossOrigin
public class ScoreController {

    @Resource
    private StudentHomeworkMapper studentHomeworkMapper;

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private UserMapper userMapper;

    /**
     * 获取成绩列表（教师查看已批阅的作业成绩）
     * GET /api/score/list?teacherId=xxx&page=1&pageSize=10
     */
    @GetMapping("/list")
    public Result getScoreList(
            @RequestParam(required = false) Long teacherId,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize) {
        try {
            // 查询该教师发布的作业
            LambdaQueryWrapper<Homework> homeworkWrapper = new LambdaQueryWrapper<>();
            homeworkWrapper.eq(Homework::getTeacherId, teacherId);
            List<Homework> homeworks = homeworkMapper.selectList(homeworkWrapper);

            if (homeworks == null || homeworks.isEmpty()) {
                return Result.success("暂无成绩数据", PageResult.of(0L, page, pageSize, new ArrayList<>()));
            }

            // 获取作业ID列表
            List<Long> homeworkIds = new ArrayList<>();
            for (Homework hw : homeworks) {
                homeworkIds.add(hw.getId());
            }

            // 查询已批阅的学生作业提交
            LambdaQueryWrapper<StudentHomework> submitWrapper = new LambdaQueryWrapper<>();
            submitWrapper.in(StudentHomework::getHomeworkId, homeworkIds)
                    .eq(StudentHomework::getCorrected, 1);

            // 获取总数
            Long total = studentHomeworkMapper.selectCount(submitWrapper);

            // 分页查询
            Page<StudentHomework> pageResult = new Page<>(page, pageSize);
            Page<StudentHomework> result = studentHomeworkMapper.selectPage(pageResult, submitWrapper);

            // 构建成绩列表
            List<Map<String, Object>> scoreList = new ArrayList<>();
            for (StudentHomework sh : result.getRecords()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", sh.getId());
                item.put("homeworkId", sh.getHomeworkId());
                item.put("studentId", sh.getStudentId());

                // 获取学生信息
                User student = userMapper.selectById(sh.getStudentId());
                if (student != null) {
                    item.put("studentName", student.getName());
                    item.put("studentUsername", student.getUsername());
                }

                // 获取作业信息
                Homework homework = homeworkMapper.selectById(sh.getHomeworkId());
                if (homework != null) {
                    item.put("homeworkTitle", homework.getTitle());
                }

                item.put("score", sh.getCorrectionContent());
                item.put("submitTime", sh.getSubmitTime());
                item.put("correctionTime", sh.getCorrectionTime());

                scoreList.add(item);
            }

            return Result.success("获取成绩列表成功", PageResult.of(total, page, pageSize, scoreList));
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取成绩列表失败：" + e.getMessage());
        }
    }
}
