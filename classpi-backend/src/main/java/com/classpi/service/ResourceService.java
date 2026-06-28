package com.classpi.service;

import com.classpi.common.Result;
import com.classpi.entity.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ResourceService {

    Result getCourseResources(Integer courseId, Long page, Long pageSize);

    Result getFolderResources(Integer courseId, String parentId, Long page, Long pageSize);

    Result createFolder(Integer courseId, String courseNo, String folderName,
            String parentId, String uploaderId, String uploaderName);

    Result addLinkResource(Integer courseId, String courseNo, String name, String url,
            String parentId, String uploaderId, String uploaderName);

    Result deleteResource(Integer id);

    Result moveResource(Integer id, String targetFolderId);

    Result downloadResource(Integer id);

    Result uploadAttachment(Integer courseId, String courseNo, MultipartFile file,
            String parentId, String uploaderId, String uploaderName);

    Resource getResourceById(Integer id);

    byte[] getFileContent(String filePath) throws IOException;
}