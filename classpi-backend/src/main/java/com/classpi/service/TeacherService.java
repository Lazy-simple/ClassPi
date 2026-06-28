package com.classpi.service;

import com.classpi.entity.User;
import com.classpi.entity.StudentHomework;
import java.util.List;
import java.util.Map;

public interface TeacherService {

    Map<String, Object> getDashboardStats(Long teacherId);

    List<User> getTeacherStudents(Long teacherId);

    List<StudentHomework> getPendingHomeworks(Long teacherId);

    Double getAvgScore(Long teacherId);
}
