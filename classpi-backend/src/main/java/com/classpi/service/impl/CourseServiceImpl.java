package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.Course;
import com.classpi.entity.CourseTeacher;
import com.classpi.entity.StudentCourse;
import com.classpi.mapper.CourseMapper;
import com.classpi.mapper.CourseTeacherMapper;
import com.classpi.mapper.StudentCourseMapper;
import com.classpi.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseTeacherMapper courseTeacherMapper;

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
        course.setDescription(courseDTO.getDescription() != null ? courseDTO.getDescription() : "");
        course.setTeacherId(courseDTO.getTeacherId());
        course.setTeacherName(courseDTO.getTeacherName() != null ? courseDTO.getTeacherName() : "教师");
        course.setDepartment(courseDTO.getDepartment() != null ? courseDTO.getDepartment() : "计算机学院");
        course.setClassroom(courseDTO.getClassroom());
        course.setSchedule(courseDTO.getSchedule());
        course.setCredit(courseDTO.getCredit() != null ? courseDTO.getCredit() : 3);
        course.setCapacity(courseDTO.getCapacity() != null ? courseDTO.getCapacity() : 50);
        course.setEnrolledCount(0);
        course.setStatus(0);

        courseMapper.insert(course);

        // ✅ 创建者自动加入 course_teacher 表
        CourseTeacher ct = new CourseTeacher();
        ct.setCourseId(course.getId());
        ct.setTeacherId(courseDTO.getTeacherId());
        ct.setTeacherName(courseDTO.getTeacherName() != null ? courseDTO.getTeacherName() : "教师");
        ct.setRole(1); // 创建者
        ct.setJoinTime(LocalDateTime.now());
        courseTeacherMapper.insert(ct);

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
        queryWrapper.eq("status", 1);  // 只查已选状态

        // 用 selectList 代替 selectOne
        List<StudentCourse> existSCList = studentCourseMapper.selectList(queryWrapper);

        // 检查是否有已选的记录
        for (StudentCourse sc : existSCList) {
            if (sc.getStatus() == 1) {
                return Result.error("您已选该课程");
            }
        }

        // 检查是否有审核中的记录
        QueryWrapper<StudentCourse> pendingWrapper = new QueryWrapper<>();
        pendingWrapper.eq("student_id", studentId);
        pendingWrapper.eq("course_id", courseId);
        pendingWrapper.eq("status", 0);
        List<StudentCourse> pendingList = studentCourseMapper.selectList(pendingWrapper);
        if (!pendingList.isEmpty()) {
            return Result.error("您的选课申请正在审核中");
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
        // 1. 先查选课记录
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("status", 1);
        List<StudentCourse> selectedCourses = studentCourseMapper.selectList(queryWrapper);

        if (selectedCourses.isEmpty()) {
            return Result.success("该学生暂无已选课程", selectedCourses);
        }

        // 2. 收集课程ID
        List<Integer> courseIds = selectedCourses.stream()
                .map(StudentCourse::getCourseId)
                .collect(Collectors.toList());

        // 3. 查询课程表获取完整信息
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.in("id", courseIds);
        List<Course> courseList = courseMapper.selectList(courseWrapper);

        // 4. 构建 courseId -> Course 的映射
        java.util.Map<Integer, Course> courseMap = courseList.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 5. 组装数据：把课程信息补充到选课记录中
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (StudentCourse sc : selectedCourses) {
            Course course = courseMap.get(sc.getCourseId());
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", sc.getId());
            item.put("studentId", sc.getStudentId());
            item.put("studentName", sc.getStudentName());
            item.put("courseId", sc.getCourseId());
            item.put("courseNo", sc.getCourseNo());
            item.put("courseName", sc.getCourseName());
            item.put("status", sc.getStatus());
            item.put("createTime", sc.getCreateTime());
            // ✅ 从课程表补充字段
            if (course != null) {
                item.put("credit", course.getCredit());
                item.put("teacherName", course.getTeacherName());
                item.put("teacherId", course.getTeacherId());
                item.put("capacity", course.getCapacity());
                item.put("enrolledCount", course.getEnrolledCount());
                item.put("schedule", course.getSchedule());
                item.put("classroom", course.getClassroom());
                item.put("department", course.getDepartment());
            } else {
                // 课程被删了，给默认值
                item.put("credit", 0);
                item.put("teacherName", "未知教师");
                item.put("capacity", 0);
                item.put("enrolledCount", 0);
            }
            result.add(item);
        }

        return Result.success("获取学生课程列表成功", result);
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

    @Override
    public Result getTeacherCourses(String teacherId, Boolean includeArchived) {
        // ✅ 从 course_teacher 表查询该教师关联的所有课程
        QueryWrapper<CourseTeacher> ctWrapper = new QueryWrapper<>();
        ctWrapper.eq("teacher_id", teacherId);
        List<CourseTeacher> ctList = courseTeacherMapper.selectList(ctWrapper);

        if (ctList.isEmpty()) {
            return Result.success("获取教师课程列表成功", new ArrayList<>());
        }

        List<Integer> courseIds = ctList.stream()
                .map(CourseTeacher::getCourseId)
                .collect(Collectors.toList());

        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.in("id", courseIds);
        if (!includeArchived) {
            courseWrapper.eq("status", 0);
        }

        List<Course> courses = courseMapper.selectList(courseWrapper);
        return Result.success("获取教师课程列表成功", courses);
    }

    @Override
    public Result getStudentCourses(String studentId, Boolean includeArchived) {
        // 1. 查选课记录
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .eq("status", 1);
        List<StudentCourse> selectedCourses = studentCourseMapper.selectList(queryWrapper);

        if (selectedCourses.isEmpty()) {
            return Result.success("该学生暂无已选课程", new ArrayList<>());
        }

        // 2. 收集课程ID
        List<Integer> courseIds = selectedCourses.stream()
                .map(StudentCourse::getCourseId)
                .collect(Collectors.toList());

        // 3. 查询课程表（根据 includeArchived 决定是否过滤）
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.in("id", courseIds);
        if (!includeArchived) {
            courseWrapper.eq("status", 0);
        }
        List<Course> courseList = courseMapper.selectList(courseWrapper);
        java.util.Map<Integer, Course> courseMap = courseList.stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 4. 组装数据
        List<java.util.Map<String, Object>> result = new ArrayList<>();
        for (StudentCourse sc : selectedCourses) {
            Course course = courseMap.get(sc.getCourseId());
            // 如果不包含归档且课程不存在（已归档），跳过
            if (!includeArchived && course == null) {
                continue;
            }
            java.util.Map<String, Object> item = new java.util.HashMap<>();
            item.put("id", sc.getId());
            item.put("studentId", sc.getStudentId());
            item.put("studentName", sc.getStudentName());
            item.put("courseId", sc.getCourseId());
            item.put("courseNo", sc.getCourseNo());
            item.put("courseName", sc.getCourseName());
            item.put("status", sc.getStatus());
            item.put("createTime", sc.getCreateTime());

            if (course != null) {
                item.put("credit", course.getCredit());
                item.put("teacherName", course.getTeacherName());
                item.put("teacherId", course.getTeacherId());
                item.put("capacity", course.getCapacity());
                item.put("enrolledCount", course.getEnrolledCount());
                item.put("schedule", course.getSchedule());
                item.put("classroom", course.getClassroom());
                item.put("department", course.getDepartment());
            } else {
                item.put("credit", 0);
                item.put("teacherName", "未知教师");
                item.put("capacity", 0);
                item.put("enrolledCount", 0);
            }
            result.add(item);
        }

        return Result.success("获取学生课程列表成功", result);
    }

    @Override
    @Transactional
    public Result archiveCourse(Integer id, Boolean archived) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        course.setStatus(archived ? 1 : 0);
        int result = courseMapper.updateById(course);

        if (result > 0) {
            String msg = archived ? "课程已归档" : "课程已恢复";
            return Result.success(msg, course);
        } else {
            return Result.error("操作失败");
        }
    }

    @Override
    @Transactional
    public Result teacherJoinCourse(String teacherId, String teacherName, String courseNo) {
        // 1. 查询课程是否存在
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.eq("course_no", courseNo);
        Course course = courseMapper.selectOne(courseWrapper);
        if (course == null) {
            return Result.error("课程不存在，请检查加课码");
        }

        // 2. 检查该教师是否已加入该课程
        QueryWrapper<CourseTeacher> checkWrapper = new QueryWrapper<>();
        checkWrapper.eq("course_id", course.getId())
                .eq("teacher_id", teacherId);
        if (courseTeacherMapper.selectCount(checkWrapper) > 0) {
            return Result.error("您已加入该课程，无需重复加入");
        }

        // 3. 插入关联记录
        CourseTeacher ct = new CourseTeacher();
        ct.setCourseId(course.getId());
        ct.setTeacherId(teacherId);
        ct.setTeacherName(teacherName);
        ct.setRole(2); // 协作者
        ct.setJoinTime(LocalDateTime.now());
        courseTeacherMapper.insert(ct);

        return Result.success("加入课程成功", course);
    }
}