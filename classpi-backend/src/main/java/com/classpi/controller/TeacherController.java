package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.service.TeacherService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/teacher")
@CrossOrigin
public class TeacherController {

    @Resource
    private TeacherService teacherService;

    /**
     * 获取教师仪表盘统计数据
     * GET /api/teacher/stats
     */
    @GetMapping("/stats")
    public Result getDashboardStats(@RequestHeader(value = "userId", required = false) Long teacherId) {
        try {
            // 如果没有传userId，默认设为1（演示用）
            if (teacherId == null) {
                teacherId = 1L;
            }
            
            Map<String, Object> stats = teacherService.getDashboardStats(teacherId);
            return Result.success("获取统计数据成功", stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败：" + e.getMessage());
        }
    }
}
