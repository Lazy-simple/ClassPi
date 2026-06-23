package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.Course;
import com.classpi.entity.StudentCourse;
import com.classpi.mapper.CourseMapper;
import com.classpi.mapper.StudentCourseMapper;
import com.classpi.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Override
    @Transactional
    public Result createCourse(CourseDTO courseDTO) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_no", courseDTO.getCourseNo());
        Course existCourse = courseMapper.selectOne(queryWrapper);
        if (existCourse != null) {
            return Result.error("课程号已存在");
        }

        Course course = new Course();
        course.setCourseNo(courseDTO.getCourseNo());
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setTeacherId(courseDTO.getTeacherId());
        course.setTeacherName(courseDTO.getTeacherName());
        course.setDepartment(courseDTO.getDepartment());
        course.setClassroom(courseDTO.getClassroom());
        course.setSchedule(courseDTO.getSchedule());
        course.setCredit(courseDTO.getCredit());
        course.setCapacity(courseDTO.getCapacity());
        course.setEnrolledCount(0);

        courseMapper.insert(course);
        return Result.success("课程创建成功", course);
    }

    @Override
    @Transactional
    public Result updateCourse(Integer id, CourseDTO courseDTO) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_no", courseDTO.getCourseNo());
        queryWrapper.ne("id", id);
        Course existCourse = courseMapper.selectOne(queryWrapper);
        if (existCourse != null) {
            return Result.error("课程号已存在");
        }

        course.setCourseNo(courseDTO.getCourseNo());
        course.setName(courseDTO.getName());
        course.setDescription(courseDTO.getDescription());
        course.setTeacherId(courseDTO.getTeacherId());
        course.setTeacherName(courseDTO.getTeacherName());
        course.setDepartment(courseDTO.getDepartment());
        course.setClassroom(courseDTO.getClassroom());
        course.setSchedule(courseDTO.getSchedule());
        course.setCredit(courseDTO.getCredit());
        course.setCapacity(courseDTO.getCapacity());

        courseMapper.updateById(course);
        return Result.success("课程修改成功", course);
    }

    @Override
    @Transactional
    public Result deleteCourse(Integer id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        QueryWrapper<StudentCourse> scQueryWrapper = new QueryWrapper<>();
        scQueryWrapper.eq("course_id", id);
        List<StudentCourse> studentCourses = studentCourseMapper.selectList(scQueryWrapper);
        if (!studentCourses.isEmpty()) {
            return Result.error("该课程已有学生选课，无法删除");
        }

        courseMapper.deleteById(id);
        return Result.success("课程删除成功");
    }

    @Override
    public Result getTeacherCourses(String teacherId) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("teacher_id", teacherId);
        List<Course> courses = courseMapper.selectList(queryWrapper);
        return Result.success("获取教师课程列表成功", courses);
    }

    @Override
    public Result getAllCourses() {
        List<Course> courses = courseMapper.selectList(null);
        return Result.success("获取所有课程列表成功", courses);
    }

    @Override
    public Result getCourseByNo(String courseNo) {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_no", courseNo);
        Course course = courseMapper.selectOne(queryWrapper);
        if (course == null) {
            return Result.error("课程不存在");
        }
        return Result.success("获取课程详情成功", course);
    }

    @Override
    public Result getCourseById(Integer id) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }
        return Result.success("获取课程详情成功", course);
    }

    @Override
    @Transactional
    public Result selectCourse(String studentId, String studentName, Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        if (course.getEnrolledCount() >= course.getCapacity()) {
            return Result.error("课程已满员");
        }

        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("course_id", courseId);
        StudentCourse existSC = studentCourseMapper.selectOne(queryWrapper);
        if (existSC != null) {
            if (existSC.getStatus() == 1) {
                return Result.error("您已选该课程");
            } else if (existSC.getStatus() == 0) {
                return Result.error("您的选课申请正在审核中");
            }
        }

        StudentCourse studentCourse = new StudentCourse();
        studentCourse.setStudentId(studentId);
        studentCourse.setStudentName(studentName);
        studentCourse.setCourseId(courseId);
        studentCourse.setCourseNo(course.getCourseNo());
        studentCourse.setCourseName(course.getName());
        studentCourse.setStatus(1);

        studentCourseMapper.insert(studentCourse);

        course.setEnrolledCount(course.getEnrolledCount() + 1);
        courseMapper.updateById(course);

        return Result.success("选课成功", studentCourse);
    }

    @Override
    public Result getStudentCourses(String studentId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("status", 1);
        List<StudentCourse> courses = studentCourseMapper.selectList(queryWrapper);
        return Result.success("获取学生课程列表成功", courses);
    }

    @Override
    @Transactional
    public Result dropCourse(String studentId, Integer courseId) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("course_id", courseId);
        StudentCourse studentCourse = studentCourseMapper.selectOne(queryWrapper);

        if (studentCourse == null) {
            return Result.error("您未选该课程");
        }

        if (studentCourse.getStatus() != 1) {
            return Result.error("无法退选，当前状态不允许");
        }

        studentCourse.setStatus(2);
        studentCourseMapper.updateById(studentCourse);

        Course course = courseMapper.selectById(courseId);
        if (course != null && course.getEnrolledCount() > 0) {
            course.setEnrolledCount(course.getEnrolledCount() - 1);
            courseMapper.updateById(course);
        }

        return Result.success("退选成功");
    }
}