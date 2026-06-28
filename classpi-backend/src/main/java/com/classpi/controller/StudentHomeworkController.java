package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.homework.HomeworkSubmitDTO;
import com.classpi.entity.StudentHomework;
import com.classpi.service.HomeworkService;
import com.classpi.service.StudentHomeworkService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

@RestController
@RequestMapping("/api/student/homework")
public class StudentHomeworkController {

    @Resource
    private StudentHomeworkService studentHomeworkService;

    @Resource
    private HomeworkService homeworkService;  // ⚠️ 添加这行，注入 HomeworkService

    /**
     * 学生提交作业
     * POST /api/student/homework/submit
     */
    @PostMapping("/submit")
    public Result submit(@RequestBody HomeworkSubmitDTO dto, @RequestHeader(value = "userId", required = false) Long studentId) {
        System.out.println("========== StudentHomeworkController.submit 被调用 ==========");
        System.out.println("dto: " + dto);
        System.out.println("studentId (从Header获取): " + studentId);
        System.out.println("=====================================");

        if (studentId == null) {
            System.out.println("⚠️ studentId 为 null！请检查请求头是否包含 userId");
            return Result.error("用户ID不能为空，请重新登录");
        }

        return studentHomeworkService.submit(dto, studentId);
    }

    /**
     * 查询自己这份作业提交状态
     * GET /api/student/homework/status/{hwId}
     */
    @GetMapping("/status/{hwId}")
    public Result<StudentHomework> getStatus(@PathVariable Long hwId, @RequestHeader(value = "userId", required = false) Long studentId) {
        System.out.println("========== getStatus 被调用 ==========");
        System.out.println("hwId: " + hwId);
        System.out.println("studentId: " + studentId);

        if (studentId == null) {
            return Result.error("用户ID不能为空");
        }
        StudentHomework record = studentHomeworkService.getByHwAndStu(hwId, studentId);
        return Result.success("查询成功", record);
    }

    /**
     * 获取某课程的所有作业（学生提交作业时选择）
     * GET /api/student/homework/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public Result getHomeworkByCourse(@PathVariable Long courseId) {
        return homeworkService.getHomeworkByCourse(courseId);
    }
}