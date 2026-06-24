package com.classpi.service;

import com.classpi.common.Result;

public interface ResourceService {
    Result createFolder(Integer courseId, String courseNo, String folderName, String parentId, String uploaderId, String uploaderName);
    
    Result uploadAttachment(Integer courseId, String courseNo, org.springframework.web.multipart.MultipartFile file, String parentId, String uploaderId, String uploaderName);
    
    Result addLinkResource(Integer courseId, String courseNo, String name, String url, String parentId, String uploaderId, String uploaderName);
    
    Result deleteResource(Integer id);
    
    Result moveResource(Integer id, String targetFolderId);
    
    Result downloadResource(Integer id);
    
    Result getCourseResources(Integer courseId, Long page, Long pageSize);
    
    Result getFolderResources(Integer courseId, String parentId, Long page, Long pageSize);
}
