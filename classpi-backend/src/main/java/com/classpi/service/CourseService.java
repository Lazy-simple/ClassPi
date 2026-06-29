package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.StudentCourse;

import java.util.List;

public interface CourseService {
    Result createCourse(CourseDTO courseDTO);

    Result updateCourse(Integer id, CourseDTO courseDTO);

    Result deleteCourse(Integer id, String teacherId);

    Result getTeacherCourses(String teacherId);

    Result getAllCourses();

    Result getCourseByNo(String courseNo);

    Result getCourseById(Integer id);

    Result selectCourse(String studentId, String studentName, Integer courseId);

    Result getStudentCourses(String studentId);

    Result dropCourse(String studentId, Integer courseId);

    Result<List<StudentCourse>> getCourseAllStudent(Integer courseId);

    Result archiveCourse(Integer id, Boolean archived);

    Result getTeacherCourses(String teacherId, Boolean includeArchived);

    Result getStudentCourses(String studentId, Boolean includeArchived);

    Result joinCourse(String teacherId, String teacherName, String courseNo);

    Result getTeacherCourseList(String teacherId);

    Result getTeacherArchivedCourses(String teacherId);

    Result archiveSelf(Integer courseId, String teacherId, Boolean archived);

    Result archiveAll(Integer courseId, String teacherId, Boolean archived);

    Result updateCourseSort(String teacherId, List<Integer> courseIds);

    Result restoreCourse(Integer courseId, String teacherId);

    Result deleteArchivedCourse(Integer courseId, String teacherId);
}
