package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.entity.*;
import com.classpi.mapper.*;
import com.classpi.service.TeacherService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TeacherServiceImpl extends ServiceImpl<UserMapper, User> implements TeacherService {

    @Resource
    private CourseMapper courseMapper;

    @Resource
    private HomeworkMapper homeworkMapper;

    @Resource
    private StudentHomeworkMapper studentHomeworkMapper;

    @Override
    public Map<String, Object> getDashboardStats(Long teacherId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 1. 获取班级总人数（该教师课程下的学生数）
        int studentCount = getTeacherStudentCount(teacherId);
        stats.put("studentCount", studentCount);
        
        // 2. 获取待批阅作业数
        int pendingHomework = getPendingHomeworkCount(teacherId);
        stats.put("pendingHomework", pendingHomework);
        
        // 3. 获取平均分
        double avgScore = getAverageScore(teacherId);
        stats.put("avgScore", avgScore);
        
        return stats;
    }

    private int getTeacherStudentCount(Long teacherId) {
        // 查询该教师的所有课程
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getTeacherId, String.valueOf(teacherId));
        List<Course> courses = courseMapper.selectList(courseWrapper);
        
        if (courses == null || courses.isEmpty()) {
            return 0;
        }
        
        // 获取课程ID列表
        int totalStudents = 0;
        for (Course course : courses) {
            totalStudents += course.getEnrolledCount() != null ? course.getEnrolledCount() : 0;
        }
        
        return totalStudents;
    }

    private int getPendingHomeworkCount(Long teacherId) {
        // 查询该教师发布的作业ID列表
        LambdaQueryWrapper<Homework> homeworkWrapper = new LambdaQueryWrapper<>();
        homeworkWrapper.eq(Homework::getTeacherId, teacherId);
        List<Homework> homeworks = homeworkMapper.selectList(homeworkWrapper);
        
        if (homeworks == null || homeworks.isEmpty()) {
            return 0;
        }
        
        int pendingCount = 0;
        for (Homework homework : homeworks) {
            // 查询该作业下未批阅的学生提交
            LambdaQueryWrapper<StudentHomework> submitWrapper = new LambdaQueryWrapper<>();
            submitWrapper.eq(StudentHomework::getHomeworkId, homework.getId())
                       .eq(StudentHomework::getCorrected, 0);
            pendingCount += studentHomeworkMapper.selectCount(submitWrapper);
        }
        
        return pendingCount;
    }

    private double getAverageScore(Long teacherId) {
        // 查询该教师发布的作业
        LambdaQueryWrapper<Homework> homeworkWrapper = new LambdaQueryWrapper<>();
        homeworkWrapper.eq(Homework::getTeacherId, teacherId);
        List<Homework> homeworks = homeworkMapper.selectList(homeworkWrapper);
        
        if (homeworks == null || homeworks.isEmpty()) {
            return 0.0;
        }
        
        double totalScore = 0;
        int submitCount = 0;
        
        for (Homework homework : homeworks) {
            // 查询已批阅的学生提交
            LambdaQueryWrapper<StudentHomework> submitWrapper = new LambdaQueryWrapper<>();
            submitWrapper.eq(StudentHomework::getHomeworkId, homework.getId())
                       .eq(StudentHomework::getCorrected, 1);
            List<StudentHomework> submissions = studentHomeworkMapper.selectList(submitWrapper);
            
            for (StudentHomework submission : submissions) {
                // 如果有批阅分数，计算平均分
                if (submission.getCorrectionContent() != null && 
                    submission.getCorrectionContent().matches("\\d+(\\.\\d+)?")) {
                    try {
                        totalScore += Double.parseDouble(submission.getCorrectionContent().replaceAll("[^\\d.]", ""));
                        submitCount++;
                    } catch (NumberFormatException e) {
                        // 忽略无效分数
                    }
                }
            }
        }
        
        return submitCount > 0 ? Math.round(totalScore / submitCount * 100.0) / 100.0 : 0.0;
    }

    @Override
    public List<User> getTeacherStudents(Long teacherId) {
        // 获取该教师课程下的学生列表
        LambdaQueryWrapper<Course> courseWrapper = new LambdaQueryWrapper<>();
        courseWrapper.eq(Course::getTeacherId, String.valueOf(teacherId));
        List<Course> courses = courseMapper.selectList(courseWrapper);
        
        return null; // 简化实现
    }

    @Override
    public List<StudentHomework> getPendingHomeworks(Long teacherId) {
        // 获取待批阅作业列表
        return null; // 简化实现
    }

    @Override
    public Double getAvgScore(Long teacherId) {
        return getAverageScore(teacherId);
    }
}
