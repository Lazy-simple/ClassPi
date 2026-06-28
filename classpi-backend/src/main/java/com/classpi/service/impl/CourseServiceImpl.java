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

        // 优化：无数据时返回成功，前端根据空列表处理，而非错误
        if (courses.isEmpty()) {
            return Result.success("该学生暂无已选课程", courses); // 成功+空列表
        }
        return Result.success("获取学生课程列表成功", courses);
    }

    @Override
    @Transactional
    public Result dropCourse(String studentId, Integer courseId) {
        System.out.println("========== dropCourse ==========");
        System.out.println("studentId: " + studentId);
        System.out.println("courseId: " + courseId);

        try {
            QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id", studentId);
            queryWrapper.eq("course_id", courseId);
            queryWrapper.eq("status", 1);  // 只查已选状态的

            // 用 selectList 代替 selectOne
            List<StudentCourse> studentCourses = studentCourseMapper.selectList(queryWrapper);

            if (studentCourses == null || studentCourses.isEmpty()) {
                return Result.error("您未选该课程");
            }

            // 取第一条，或者全部更新
            StudentCourse studentCourse = studentCourses.get(0);

            // 如果有多个，全部更新
            for (StudentCourse sc : studentCourses) {
                sc.setStatus(2);
                studentCourseMapper.updateById(sc);
            }

            // 更新课程人数
            Course course = courseMapper.selectById(courseId);
            if (course != null && course.getEnrolledCount() > 0) {
                course.setEnrolledCount(course.getEnrolledCount() - studentCourses.size());
                courseMapper.updateById(course);
            }

            return Result.success("退选成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("退选失败：" + e.getMessage());
        }
    }

    @Override
    public Result<List<StudentCourse>> getCourseAllStudent(Integer courseId) {
        Course course = courseMapper.selectById(courseId);
        if(course == null){
            return Result.error("课程不存在");
        }
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("status", 1);
        List<StudentCourse> studentList = studentCourseMapper.selectList(wrapper);
        // 你之前修复的必须带msg参数
        return Result.success("查询选课学生列表成功", studentList);
    }
}