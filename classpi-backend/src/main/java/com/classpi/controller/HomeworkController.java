package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkPublishDTO;
import com.classpi.dto.homework.HomeworkCorrectDTO;
import com.classpi.service.HomeworkService;
import com.classpi.service.StudentHomeworkService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
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
}