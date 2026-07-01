package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.StudentCourse;

import java.util.List;

public interface CourseService {
    // 创建课程
    Result createCourse(CourseDTO courseDTO);
    
    // 修改课程
    Result updateCourse(Integer id, CourseDTO courseDTO);
    
    // 删除课程
    Result deleteCourse(Integer id);
    
    // 获取所有课程（学生选课用）
    Result getAllCourses();
    
    // 根据课程号获取课程详情
    Result getCourseByNo(String courseNo);
    
    // 根据ID获取课程详情
    Result getCourseById(Integer id);
    
    // 学生选修课程
    Result selectCourse(String studentId, String studentName, Integer courseId);
    
    // 获取学生已选课程列表
    Result getStudentCourses(String studentId);
    
    // 学生退选课程
    Result dropCourse(String studentId, Integer courseId);

    // 根据课程id查询所有已选课学生
    Result<List<StudentCourse>> getCourseAllStudent(Integer courseId);

    // 归档/取消归档课程
    Result archiveCourse(Integer id, Boolean archived);

    // 获取教师的课程列表（可包含归档）
    Result getTeacherCourses(String teacherId, Boolean includeArchived);

    // 获取学生已选课程列表（可包含归档）
    Result getStudentCourses(String studentId, Boolean includeArchived);

    /**
     * 教师通过加课码加入课程
     */
    Result teacherJoinCourse(String teacherId, String teacherName, String courseNo);
}