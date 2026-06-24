package com.classpi.service;

import com.classpi.common.Result;

public interface PreparationService {
    Result addPreparation(String teacherId, String teacherName, String title, String type, String content);
    
    Result deletePreparation(Integer id, String teacherId, String identity);
    
    Result updatePreparation(Integer id, String title, String content, String teacherId, String identity);
    
    Result getTeacherPreparations(String teacherId, Long page, Long pageSize);
    
    Result getTeacherPreparationsByType(String teacherId, String type, Long page, Long pageSize);
    
    Result getUnassignedPreparations(String teacherId, Long page, Long pageSize);
    
    Result assignToCourse(Integer id, Integer courseId, String courseNo, String teacherId, String identity);
    
    Result getPreparationById(Integer id, String teacherId, String identity);
}
