package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkPublishDTO;
import com.classpi.dto.homework.HomeworkCorrectDTO;
import com.classpi.service.HomeworkService;
import com.classpi.service.StudentHomeworkService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

import com.classpi.entity.StudentHomework;

@RestController
@RequestMapping("/api/homework")
public class HomeworkController {

    @Resource
    private HomeworkService homeworkService;
    @Resource
    private StudentHomeworkService studentHomeworkService;

    /**
     * 发布作业
     * POST /api/homework/publish
     * 请求头携带登录用户token，后端解析teacherId
     */
    @PostMapping("/publish")
    public Result publish(@RequestBody HomeworkPublishDTO dto, @RequestHeader("userId") Long teacherId) {
        // 这里后期拦截器校验身份必须是teacher，这里仅做参数接收
        return homeworkService.publish(dto, teacherId);
    }

    /**
     * 催交作业
     * POST /api/homework/remind/{hwId}
     */
    @PostMapping("/remind/{hwId}")
    public Result remind(@PathVariable Long hwId) {
        return homeworkService.remind(hwId);
    }

    // ✅ 新增：针对单个学生的催交
    @PostMapping("/remind/single/{hwId}")
    public Result remindSingle(
            @PathVariable Long hwId,
            @RequestBody Map<String, Long> params) {
        Long studentId = params.get("studentId");
        return homeworkService.remindSingle(hwId, studentId);
    }
    /**
     * 查询该作业所有学生提交记录（批阅列表）
     * GET /api/homework/submit/list/{hwId}
     */
    @GetMapping("/submit/list/{hwId}")
    public Result<List<StudentHomework>> getSubmitList(@PathVariable Long hwId) {
        List<StudentHomework> list = studentHomeworkService.listByHomeworkId(hwId);
        return Result.success("查询提交记录成功", list);
    }

    /**
     * 批改作业
     * POST /api/homework/correct
     */
    @PostMapping("/correct")
    public Result correct(@RequestBody HomeworkCorrectDTO dto, @RequestHeader("userId") Long teacherId) {
        return studentHomeworkService.correct(dto, teacherId);
    }

    /**
     * 获取教师的所有作业（教师发布作业时查看）
     */
    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherHomework(@PathVariable Long teacherId) {
        return homeworkService.getTeacherHomework(teacherId);
    }

    /**
     * 获取某作业未提交的学生列表
     * GET /api/homework/unsubmitted/{hwId}
     */
    @GetMapping("/unsubmitted/{hwId}")
    public Result getUnsubmittedStudents(@PathVariable Long hwId) {
        return studentHomeworkService.getUnsubmittedStudents(hwId);
    }
}