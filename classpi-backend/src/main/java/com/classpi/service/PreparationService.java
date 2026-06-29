package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.dto.ImportPreparationDTO;

public interface PreparationService {
    Result addPreparation(String teacherId, String teacherName, String title, String type, String content);
    
    Result deletePreparation(Integer id, String teacherId, String identity);
    
    Result updatePreparation(Integer id, String title, String content, String teacherId, String identity);
    
    Result getTeacherPreparations(String teacherId, Long page, Long pageSize);
    
    Result getTeacherPreparationsByType(String teacherId, String type, Long page, Long pageSize);
    
    Result getUnassignedPreparations(String teacherId, Long page, Long pageSize);
    
    Result assignToCourse(Integer id, Integer courseId, String courseNo, String teacherId, String identity);
    
    Result getPreparationById(Integer id, String teacherId, String identity);

    /**
     * 从备课区导入资源
     */
    Result importResource(ImportPreparationDTO dto, String userId);

    /**
     * 从备课区导入作业
     */
    Result importHomework(ImportPreparationDTO dto, String userId);

    /**
     * 从备课区导入话题
     */
    Result importTopic(ImportPreparationDTO dto, String userId);
}
