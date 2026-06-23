package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkSubmitDTO;
import com.classpi.entity.StudentHomework;
import com.classpi.service.StudentHomeworkService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/student/homework")
public class StudentHomeworkController {

    @Resource
    private StudentHomeworkService studentHomeworkService;

    /**
     * 学生提交作业
     * POST /api/student/homework/submit
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody HomeworkSubmitDTO dto, @RequestHeader("userId") Long studentId) {
        return studentHomeworkService.submit(dto, studentId);
    }

    /**
     * 查询自己这份作业提交状态
     * GET /api/student/homework/status/{hwId}
     */
    @GetMapping("/status/{hwId}")
    public Result<StudentHomework> getStatus(@PathVariable Long hwId, @RequestHeader("userId") Long studentId) {
        StudentHomework record = studentHomeworkService.getByHwAndStu(hwId, studentId);
        return Result.success("查询成功", record);
    }
}
