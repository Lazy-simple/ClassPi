package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classpi.common.Result;
import com.classpi.dto.CourseDTO;
import com.classpi.entity.Course;
import com.classpi.entity.StudentCourse;
import com.classpi.entity.TeacherCourse;
import com.classpi.mapper.CourseMapper;
import com.classpi.mapper.StudentCourseMapper;
import com.classpi.mapper.TeacherCourseMapper;
import com.classpi.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;

    @Autowired
    private StudentCourseMapper studentCourseMapper;

    @Autowired
    private TeacherCourseMapper teacherCourseMapper;

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

        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTeacherId(courseDTO.getTeacherId());
        teacherCourse.setTeacherName(courseDTO.getTeacherName() != null ? courseDTO.getTeacherName() : "教师");
        teacherCourse.setCourseId(course.getId());
        teacherCourse.setCourseNo(course.getCourseNo());
        teacherCourse.setCourseName(course.getName());
        teacherCourse.setRole(1);
        teacherCourse.setSortOrder(0);
        teacherCourse.setArchived(0);
        teacherCourseMapper.insert(teacherCourse);

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

        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("course_id", id);
        List<TeacherCourse> tcList = teacherCourseMapper.selectList(tcWrapper);
        for (TeacherCourse tc : tcList) {
            tc.setCourseNo(course.getCourseNo());
            tc.setCourseName(course.getName());
            teacherCourseMapper.updateById(tc);
        }

        return Result.success("课程修改成功", course);
    }

    @Override
    @Transactional
    public Result deleteCourse(Integer id, String teacherId) {
        Course course = courseMapper.selectById(id);
        if (course == null) {
            return Result.error("课程不存在");
        }

        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("course_id", id).eq("teacher_id", teacherId);
        TeacherCourse teacherCourse = teacherCourseMapper.selectOne(tcWrapper);

        if (teacherCourse == null) {
            return Result.error("您无权操作该课程");
        }

        if (teacherCourse.getRole() == 1) {
            QueryWrapper<StudentCourse> scQueryWrapper = new QueryWrapper<>();
            scQueryWrapper.eq("course_id", id).eq("status", 1);
            List<StudentCourse> studentCourses = studentCourseMapper.selectList(scQueryWrapper);
            if (!studentCourses.isEmpty()) {
                return Result.error("该课程已有学生选课，无法删除");
            }

            QueryWrapper<TeacherCourse> allTcWrapper = new QueryWrapper<>();
            allTcWrapper.eq("course_id", id);
            teacherCourseMapper.delete(allTcWrapper);

            courseMapper.deleteById(id);
            return Result.success("课程删除成功");
        } else {
            teacherCourseMapper.deleteById(teacherCourse.getId());
            return Result.success("已退出该课程");
        }
    }

    @Override
    public Result getTeacherCourses(String teacherId) {
        return getTeacherCourseList(teacherId);
    }

    @Override
    public Result getAllCourses() {
        QueryWrapper<Course> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 0);
        List<Course> courses = courseMapper.selectList(queryWrapper);
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

        if (course.getStatus() == 1) {
            return Result.error("课程已归档，无法选课");
        }

        if (course.getEnrolledCount() >= course.getCapacity()) {
            return Result.error("课程已满员");
        }

        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("status", 1);

        List<StudentCourse> existSCList = studentCourseMapper.selectList(queryWrapper);

        for (StudentCourse sc : existSCList) {
            if (sc.getStatus() == 1) {
                return Result.error("您已选该课程");
            }
        }

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
        return getStudentCourses(studentId, false);
    }

    @Override
    @Transactional
    public Result dropCourse(String studentId, Integer courseId) {
        try {
            QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("student_id", studentId);
            queryWrapper.eq("course_id", courseId);
            queryWrapper.eq("status", 1);

            List<StudentCourse> studentCourses = studentCourseMapper.selectList(queryWrapper);

            if (studentCourses == null || studentCourses.isEmpty()) {
                return Result.error("您未选该课程");
            }

            for (StudentCourse sc : studentCourses) {
                sc.setStatus(2);
                studentCourseMapper.updateById(sc);
            }

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
        if (course == null) {
            return Result.error("课程不存在");
        }
        QueryWrapper<StudentCourse> wrapper = new QueryWrapper<>();
        wrapper.eq("course_id", courseId);
        wrapper.eq("status", 1);
        List<StudentCourse> studentList = studentCourseMapper.selectList(wrapper);
        return Result.success("查询选课学生列表成功", studentList);
    }

    @Override
    public Result archiveCourse(Integer id, Boolean archived) {
        return archiveAll(id, null, archived);
    }

    @Override
    public Result getTeacherCourses(String teacherId, Boolean includeArchived) {
        if (includeArchived) {
            return getTeacherCourseList(teacherId);
        }
        return getTeacherActiveCourses(teacherId);
    }

    @Override
    public Result getStudentCourses(String studentId, Boolean includeArchived) {
        QueryWrapper<StudentCourse> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId)
                .eq("status", 1);
        List<StudentCourse> courses = studentCourseMapper.selectList(queryWrapper);

        if (courses.isEmpty()) {
            return Result.success("该学生暂无已选课程", courses);
        }

        if (!includeArchived) {
            List<Integer> courseIds = courses.stream()
                    .map(StudentCourse::getCourseId)
                    .collect(Collectors.toList());
            if (!courseIds.isEmpty()) {
                QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
                courseWrapper.in("id", courseIds)
                        .eq("status", 0);
                List<Course> activeCourses = courseMapper.selectList(courseWrapper);
                List<Integer> activeIds = activeCourses.stream()
                        .map(Course::getId)
                        .collect(Collectors.toList());
                courses = courses.stream()
                        .filter(sc -> activeIds.contains(sc.getCourseId()))
                        .collect(Collectors.toList());
            }
        }

        return Result.success("获取学生课程列表成功", courses);
    }

    @Override
    @Transactional
    public Result joinCourse(String teacherId, String teacherName, String courseNo) {
        QueryWrapper<Course> courseWrapper = new QueryWrapper<>();
        courseWrapper.eq("course_no", courseNo);
        Course course = courseMapper.selectOne(courseWrapper);
        if (course == null) {
            return Result.error("课程不存在，请检查课程号");
        }

        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId).eq("course_id", course.getId());
        TeacherCourse existTc = teacherCourseMapper.selectOne(tcWrapper);
        if (existTc != null) {
            if (existTc.getArchived() == 1) {
                existTc.setArchived(0);
                teacherCourseMapper.updateById(existTc);
                return Result.success("已重新加入该课程", course);
            }
            return Result.error("您已加入该课程");
        }

        QueryWrapper<TeacherCourse> sortWrapper = new QueryWrapper<>();
        sortWrapper.eq("teacher_id", teacherId);
        Integer maxSort = teacherCourseMapper.selectList(sortWrapper).size();

        TeacherCourse teacherCourse = new TeacherCourse();
        teacherCourse.setTeacherId(teacherId);
        teacherCourse.setTeacherName(teacherName != null ? teacherName : "教师");
        teacherCourse.setCourseId(course.getId());
        teacherCourse.setCourseNo(course.getCourseNo());
        teacherCourse.setCourseName(course.getName());
        teacherCourse.setRole(2);
        teacherCourse.setSortOrder(maxSort);
        teacherCourse.setArchived(0);
        teacherCourseMapper.insert(teacherCourse);

        return Result.success("加入课程成功", course);
    }

    @Override
    public Result getTeacherCourseList(String teacherId) {
        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId).orderByAsc("sort_order");
        List<TeacherCourse> tcList = teacherCourseMapper.selectList(tcWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TeacherCourse tc : tcList) {
            Course course = courseMapper.selectById(tc.getCourseId());
            if (course != null) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", course.getId());
                item.put("courseNo", course.getCourseNo());
                item.put("name", course.getName());
                item.put("description", course.getDescription());
                item.put("teacherId", course.getTeacherId());
                item.put("teacherName", course.getTeacherName());
                item.put("department", course.getDepartment());
                item.put("classroom", course.getClassroom());
                item.put("schedule", course.getSchedule());
                item.put("credit", course.getCredit());
                item.put("capacity", course.getCapacity());
                item.put("enrolledCount", course.getEnrolledCount());
                item.put("status", course.getStatus());
                item.put("createTime", course.getCreateTime());
                item.put("updateTime", course.getUpdateTime());
                item.put("role", tc.getRole());
                item.put("sortOrder", tc.getSortOrder());
                item.put("selfArchived", tc.getArchived());
                item.put("isOwner", tc.getRole() == 1);
                result.add(item);
            }
        }

        return Result.success("获取教师课程列表成功", result);
    }

    private Result getTeacherActiveCourses(String teacherId) {
        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId).eq("archived", 0).orderByAsc("sort_order");
        List<TeacherCourse> tcList = teacherCourseMapper.selectList(tcWrapper);

        List<Map<String, Object>> result = new ArrayList<>();
        for (TeacherCourse tc : tcList) {
            Course course = courseMapper.selectById(tc.getCourseId());
            if (course != null && course.getStatus() == 0) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", course.getId());
                item.put("courseNo", course.getCourseNo());
                item.put("name", course.getName());
                item.put("description", course.getDescription());
                item.put("teacherId", course.getTeacherId());
                item.put("teacherName", course.getTeacherName());
                item.put("department", course.getDepartment());
                item.put("classroom", course.getClassroom());
                item.put("schedule", course.getSchedule());
                item.put("credit", course.getCredit());
                item.put("capacity", course.getCapacity());
                item.put("enrolledCount", course.getEnrolledCount());
                item.put("status", course.getStatus());
                item.put("createTime", course.getCreateTime());
                item.put("updateTime", course.getUpdateTime());
                item.put("role", tc.getRole());
                item.put("sortOrder", tc.getSortOrder());
                item.put("selfArchived", tc.getArchived());
                item.put("isOwner", tc.getRole() == 1);
                result.add(item);
            }
        }

        return Result.success("获取教师课程列表成功", result);
    }

    @Override
    public Result getTeacherArchivedCourses(String teacherId) {
        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId).eq("archived", 1).orderByAsc("sort_order");
        List<TeacherCourse> tcList = teacherCourseMapper.selectList(tcWrapper);

        List<Integer> tcCourseIds = tcList.stream().map(TeacherCourse::getCourseId).collect(Collectors.toList());

        QueryWrapper<Course> globalArchivedWrapper = new QueryWrapper<>();
        globalArchivedWrapper.eq("status", 1);
        List<Course> globalArchivedCourses = courseMapper.selectList(globalArchivedWrapper);

        List<Integer> globalArchivedIds = new ArrayList<>();
        for (Course c : globalArchivedCourses) {
            QueryWrapper<TeacherCourse> ownerWrapper = new QueryWrapper<>();
            ownerWrapper.eq("teacher_id", teacherId).eq("course_id", c.getId()).eq("role", 1);
            if (teacherCourseMapper.selectCount(ownerWrapper) > 0) {
                globalArchivedIds.add(c.getId());
            }
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (TeacherCourse tc : tcList) {
            Course course = courseMapper.selectById(tc.getCourseId());
            if (course != null) {
                Map<String, Object> item = buildCourseMap(course, tc);
                item.put("archiveType", "self");
                result.add(item);
            }
        }

        for (Integer cid : globalArchivedIds) {
            if (!tcCourseIds.contains(cid)) {
                Course course = courseMapper.selectById(cid);
                QueryWrapper<TeacherCourse> ownerWrapper = new QueryWrapper<>();
                ownerWrapper.eq("teacher_id", teacherId).eq("course_id", cid);
                TeacherCourse tc = teacherCourseMapper.selectOne(ownerWrapper);
                if (course != null && tc != null) {
                    Map<String, Object> item = buildCourseMap(course, tc);
                    item.put("archiveType", "global");
                    result.add(item);
                }
            }
        }

        return Result.success("获取归档课程列表成功", result);
    }

    private Map<String, Object> buildCourseMap(Course course, TeacherCourse tc) {
        Map<String, Object> item = new HashMap<>();
        item.put("id", course.getId());
        item.put("courseNo", course.getCourseNo());
        item.put("name", course.getName());
        item.put("description", course.getDescription());
        item.put("teacherId", course.getTeacherId());
        item.put("teacherName", course.getTeacherName());
        item.put("department", course.getDepartment());
        item.put("classroom", course.getClassroom());
        item.put("schedule", course.getSchedule());
        item.put("credit", course.getCredit());
        item.put("capacity", course.getCapacity());
        item.put("enrolledCount", course.getEnrolledCount());
        item.put("status", course.getStatus());
        item.put("createTime", course.getCreateTime());
        item.put("updateTime", course.getUpdateTime());
        item.put("role", tc.getRole());
        item.put("sortOrder", tc.getSortOrder());
        item.put("selfArchived", tc.getArchived());
        item.put("isOwner", tc.getRole() == 1);
        return item;
    }

    @Override
    @Transactional
    public Result archiveSelf(Integer courseId, String teacherId, Boolean archived) {
        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId).eq("course_id", courseId);
        TeacherCourse teacherCourse = teacherCourseMapper.selectOne(tcWrapper);

        if (teacherCourse == null) {
            return Result.error("您未加入该课程");
        }

        teacherCourse.setArchived(archived ? 1 : 0);
        teacherCourseMapper.updateById(teacherCourse);

        String msg = archived ? "已归档自己的课程" : "已恢复课程";
        return Result.success(msg, teacherCourse);
    }

    @Override
    @Transactional
    public Result archiveAll(Integer courseId, String teacherId, Boolean archived) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        if (teacherId != null) {
            QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
            tcWrapper.eq("teacher_id", teacherId).eq("course_id", courseId).eq("role", 1);
            if (teacherCourseMapper.selectCount(tcWrapper) == 0) {
                return Result.error("只有课程创建者才能归档全部");
            }
        }

        course.setStatus(archived ? 1 : 0);
        courseMapper.updateById(course);

        String msg = archived ? "课程已全部归档" : "课程已恢复";
        return Result.success(msg, course);
    }

    @Override
    @Transactional
    public Result updateCourseSort(String teacherId, List<Integer> courseIds) {
        for (int i = 0; i < courseIds.size(); i++) {
            Integer courseId = courseIds.get(i);
            QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
            tcWrapper.eq("teacher_id", teacherId).eq("course_id", courseId);
            TeacherCourse tc = teacherCourseMapper.selectOne(tcWrapper);
            if (tc != null) {
                tc.setSortOrder(i);
                teacherCourseMapper.updateById(tc);
            }
        }
        return Result.success("排序更新成功");
    }

    @Override
    @Transactional
    public Result restoreCourse(Integer courseId, String teacherId) {
        QueryWrapper<TeacherCourse> tcWrapper = new QueryWrapper<>();
        tcWrapper.eq("teacher_id", teacherId).eq("course_id", courseId);
        TeacherCourse tc = teacherCourseMapper.selectOne(tcWrapper);

        if (tc == null) {
            return Result.error("您未加入该课程");
        }

        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            return Result.error("课程不存在");
        }

        if (tc.getArchived() == 1) {
            tc.setArchived(0);
            teacherCourseMapper.updateById(tc);
        }

        if (course.getStatus() == 1 && tc.getRole() == 1) {
            course.setStatus(0);
            courseMapper.updateById(course);
        }

        return Result.success("课程已恢复");
    }

    @Override
    @Transactional
    public Result deleteArchivedCourse(Integer courseId, String teacherId) {
        return deleteCourse(courseId, teacherId);
    }
}
