package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.StudentCourse;
import com.classpi.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/course")
@CrossOrigin
public class CourseController {

    @Autowired
    private CourseService courseService;

    @PostMapping("/create")
    public Result createCourse(@RequestBody CourseDTO courseDTO) {
        return courseService.createCourse(courseDTO);
    }

    @PutMapping("/{id}")
    public Result updateCourse(@PathVariable Integer id, @RequestBody CourseDTO courseDTO) {
        return courseService.updateCourse(id, courseDTO);
    }

    @DeleteMapping("/{id}")
    public Result deleteCourse(@PathVariable Integer id, @RequestParam String teacherId) {
        return courseService.deleteCourse(id, teacherId);
    }

    @GetMapping("/list")
    public Result getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/no/{courseNo}")
    public Result getCourseByNo(@PathVariable String courseNo) {
        return courseService.getCourseByNo(courseNo);
    }

    @GetMapping("/{id}")
    public Result getCourseById(@PathVariable Integer id) {
        return courseService.getCourseById(id);
    }

    @PostMapping("/select")
    public Result selectCourse(@RequestParam String studentId,
                               @RequestParam String studentName,
                               @RequestParam Integer courseId) {
        return courseService.selectCourse(studentId, studentName, courseId);
    }

    @PostMapping("/drop")
    public Result dropCourse(@RequestParam String studentId,
                             @RequestParam Integer courseId) {
        return courseService.dropCourse(studentId, courseId);
    }

    @GetMapping("/{courseId}/allStudent")
    public Result<List<StudentCourse>> getCourseAllStudent(@PathVariable Integer courseId) {
        return courseService.getCourseAllStudent(courseId);
    }

    @GetMapping("/teacher/{teacherId}")
    public Result getTeacherCourses(@PathVariable String teacherId,
                                    @RequestParam(defaultValue = "false") Boolean includeArchived) {
        return courseService.getTeacherCourses(teacherId, includeArchived);
    }

    @GetMapping("/teacher/{teacherId}/list")
    public Result<List<Map<String, Object>>> getTeacherCourseList(@PathVariable String teacherId) {
        return courseService.getTeacherCourseList(teacherId);
    }

    @GetMapping("/teacher/{teacherId}/archived")
    public Result<List<Map<String, Object>>> getTeacherArchivedCourses(@PathVariable String teacherId) {
        return courseService.getTeacherArchivedCourses(teacherId);
    }

    @GetMapping("/student/{studentId}")
    public Result getStudentCourses(@PathVariable String studentId,
                                    @RequestParam(defaultValue = "false") Boolean includeArchived) {
        return courseService.getStudentCourses(studentId, includeArchived);
    }

    @PutMapping("/{id}/archive")
    public Result archiveCourse(@PathVariable Integer id, @RequestParam Boolean archived) {
        return courseService.archiveCourse(id, archived);
    }

    @PostMapping("/join")
    public Result joinCourse(@RequestParam String teacherId,
                             @RequestParam String teacherName,
                             @RequestParam String courseNo) {
        return courseService.joinCourse(teacherId, teacherName, courseNo);
    }

    @PutMapping("/{id}/archive-self")
    public Result archiveSelf(@PathVariable Integer id,
                              @RequestParam String teacherId,
                              @RequestParam Boolean archived) {
        return courseService.archiveSelf(id, teacherId, archived);
    }

    @PutMapping("/{id}/archive-all")
    public Result archiveAll(@PathVariable Integer id,
                             @RequestParam String teacherId,
                             @RequestParam Boolean archived) {
        return courseService.archiveAll(id, teacherId, archived);
    }

    @PutMapping("/teacher/{teacherId}/sort")
    public Result updateCourseSort(@PathVariable String teacherId,
                                   @RequestBody List<Integer> courseIds) {
        return courseService.updateCourseSort(teacherId, courseIds);
    }

    @PutMapping("/{id}/restore")
    public Result restoreCourse(@PathVariable Integer id, @RequestParam String teacherId) {
        return courseService.restoreCourse(id, teacherId);
    }

    @DeleteMapping("/{id}/archived")
    public Result deleteArchivedCourse(@PathVariable Integer id, @RequestParam String teacherId) {
        return courseService.deleteArchivedCourse(id, teacherId);
    }
}
