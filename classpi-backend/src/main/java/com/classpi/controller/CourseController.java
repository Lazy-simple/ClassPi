package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.StudentCourse;
import com.classpi.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/course")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    // 创建课程（教师）
    @PostMapping("/create")
    public Result createCourse(@RequestBody CourseDTO courseDTO) {
        return courseService.createCourse(courseDTO);
    }

    // 修改课程（教师）
    @PutMapping("/{id}")
    public Result updateCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        return courseService.updateCourse(id, courseDTO);
    }

    // 删除课程（教师）
    @DeleteMapping("/{id}")
    public Result deleteCourse(@PathVariable Integer id) {
        return courseService.deleteCourse(id);
    }

    // 获取所有课程（学生选课用）
    @GetMapping("/list")
    public Result getAllCourses() {
        return courseService.getAllCourses();
    }

    // 根据课程号获取课程详情
    @GetMapping("/no/{courseNo}")
    public Result getCourseByNo(@PathVariable String courseNo) {
        return courseService.getCourseByNo(courseNo);
    }

    // 根据ID获取课程详情
    @GetMapping("/{id}")
    public Result getCourseById(@PathVariable Integer id) {
        return courseService.getCourseById(id);
    }

    // 学生选修课程
    @PostMapping("/select")
    public Result selectCourse(@RequestParam String studentId, 
                               @RequestParam String studentName, 
                               @RequestParam Integer courseId) {
        return courseService.selectCourse(studentId, studentName, courseId);
    }

    // 学生退选课程
    @PostMapping("/drop")
    public Result dropCourse(@RequestParam String studentId, 
                             @RequestParam Integer courseId) {
        return courseService.dropCourse(studentId, courseId);
    }

    /**
     * 根据课程ID 获取该课程全部已选课学生（学生/老师都能调用）
     */
    @GetMapping("/{courseId}/allStudent")
    public Result<List<StudentCourse>> getCourseAllStudent(@PathVariable Integer courseId){
        return courseService.getCourseAllStudent(courseId);
    }

    // 获取教师的课程列表
    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherCourses(@PathVariable String teacherId,
                                    @RequestParam(defaultValue = "false") Boolean includeArchived) {
        return courseService.getTeacherCourses(teacherId, includeArchived);
    }

    // 获取学生已选课程列表
    @GetMapping("/student/{studentId}")
    public Result getStudentCourses(@PathVariable String studentId,
                                    @RequestParam(defaultValue = "false") Boolean includeArchived) {
        return courseService.getStudentCourses(studentId, includeArchived);
    }

    // 归档/取消归档课程
    @PutMapping("/{id}/archive")
    public Result archiveCourse(@PathVariable Integer id, @RequestParam Boolean archived) {
        return courseService.archiveCourse(id, archived);
    }

    /**
     * 教师加入课程（通过加课码）
     * POST /course/teacher/join
     */
    @PostMapping("/teacher/join")
    public Result teacherJoinCourse(@RequestParam String teacherId,
                                    @RequestParam String teacherName,
                                    @RequestParam String courseNo) {
        return courseService.teacherJoinCourse(teacherId, teacherName, courseNo);
    }
}