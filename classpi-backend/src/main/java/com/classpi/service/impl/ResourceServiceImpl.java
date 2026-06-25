package com.classpi.service.impl;

import com.classpi.common.Result;
import com.classpi.service.ResourceService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 资源服务实现类
 */
@Service // 关键：添加@Service注解，让Spring识别为Bean
public class ResourceServiceImpl implements ResourceService {

    @Override
    public Result createFolder(Integer courseId, String courseNo, String folderName, String parentId, String uploaderId, String uploaderName) {
        // 补充实际业务逻辑（示例）
        return Result.success("创建文件夹成功");
    }

    @Override
    public Result uploadAttachment(Integer courseId, String courseNo, MultipartFile file, String parentId, String uploaderId, String uploaderName) {
        // 补充实际业务逻辑（示例：文件上传、路径存储等）
        return Result.success("附件上传成功");
    }

    @Override
    public Result addLinkResource(Integer courseId, String courseNo, String name, String url, String parentId, String uploaderId, String uploaderName) {
        // 补充实际业务逻辑（示例）
        return Result.success("添加链接资源成功");
    }

    @Override
    public Result deleteResource(Integer id) {
        // 补充实际业务逻辑（示例）
        return Result.success("删除资源成功");
    }

    @Override
    public Result moveResource(Integer id, String targetFolderId) {
        // 补充实际业务逻辑（示例）
        return Result.success("移动资源成功");
    }

    @Override
    public Result downloadResource(Integer id) {
        // 补充实际业务逻辑（示例：文件下载流处理）
        return Result.success("下载资源成功");
    }

    @Override
    public Result getCourseResources(Integer courseId, Long page, Long pageSize) {
        // 补充实际业务逻辑（示例：分页查询课程资源）
        return Result.success("获取课程资源列表成功");
    }

    @Override
    public Result getFolderResources(Integer courseId, String parentId, Long page, Long pageSize) {
        // 补充实际业务逻辑（示例：分页查询文件夹下资源）
        return Result.success("获取文件夹资源列表成功");
    }
}