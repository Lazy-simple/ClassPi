package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classpi.common.PageResult;
import com.classpi.common.Result;
import com.classpi.entity.Resource;
import com.classpi.mapper.ResourceMapper;
import com.classpi.service.ResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    private ResourceMapper resourceMapper;

    private static final String UPLOAD_PATH = "uploads/course_resources/";

    @Override
    @Transactional
    public Result createFolder(Integer courseId, String courseNo, String folderName, String parentId, String uploaderId, String uploaderName) {
        Resource folder = new Resource();
        folder.setCourseId(courseId);
        folder.setCourseNo(courseNo);
        folder.setName(folderName);
        folder.setFolderName(folderName);
        folder.setType("attachment");
        folder.setIsFolder(1);
        folder.setParentId(parentId);
        folder.setUploaderId(uploaderId);
        folder.setUploaderName(uploaderName);

        resourceMapper.insert(folder);
        return Result.success("文件夹创建成功", folder);
    }

    @Override
    @Transactional
    public Result uploadAttachment(Integer courseId, String courseNo, MultipartFile file, String parentId, String uploaderId, String uploaderName) {
        if (file.isEmpty()) {
            return Result.error("请选择要上传的文件");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String newFilename = UUID.randomUUID().toString() + extension;

        File uploadDir = new File(UPLOAD_PATH + courseNo + "/");
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        File dest = new File(UPLOAD_PATH + courseNo + "/" + newFilename);
        try {
            file.transferTo(dest);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }

        Resource resource = new Resource();
        resource.setCourseId(courseId);
        resource.setCourseNo(courseNo);
        resource.setName(originalFilename);
        resource.setType("attachment");
        resource.setPath("/api/resource/download/" + newFilename + "?courseNo=" + courseNo);
        resource.setParentId(parentId);
        resource.setIsFolder(0);
        resource.setUploaderId(uploaderId);
        resource.setUploaderName(uploaderName);
        resource.setFileSize(file.getSize());
        resource.setFileType(extension.substring(1));

        resourceMapper.insert(resource);
        return Result.success("文件上传成功", resource);
    }

    @Override
    @Transactional
    public Result addLinkResource(Integer courseId, String courseNo, String name, String url, String parentId, String uploaderId, String uploaderName) {
        Resource resource = new Resource();
        resource.setCourseId(courseId);
        resource.setCourseNo(courseNo);
        resource.setName(name);
        resource.setType("link");
        resource.setPath(url);
        resource.setParentId(parentId);
        resource.setIsFolder(0);
        resource.setUploaderId(uploaderId);
        resource.setUploaderName(uploaderName);

        resourceMapper.insert(resource);
        return Result.success("外链资源添加成功", resource);
    }

    @Override
    @Transactional
    public Result deleteResource(Integer id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return Result.error("资源不存在");
        }

        if (resource.getIsFolder() == 1) {
            QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("parent_id", id.toString());
            java.util.List<Resource> children = resourceMapper.selectList(queryWrapper);
            for (Resource child : children) {
                deleteResource(child.getId());
            }
        } else if ("attachment".equals(resource.getType())) {
            String filePath = UPLOAD_PATH + resource.getCourseNo() + "/" + resource.getPath().substring(resource.getPath().lastIndexOf("/") + 1);
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }

        resourceMapper.deleteById(id);
        return Result.success("资源删除成功");
    }

    @Override
    @Transactional
    public Result moveResource(Integer id, String targetFolderId) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return Result.error("资源不存在");
        }

        resource.setParentId(targetFolderId);
        resourceMapper.updateById(resource);
        return Result.success("资源移动成功", resource);
    }

    @Override
    public Result downloadResource(Integer id) {
        Resource resource = resourceMapper.selectById(id);
        if (resource == null) {
            return Result.error("资源不存在");
        }

        if ("link".equals(resource.getType())) {
            return Result.success("获取外链地址成功", resource.getPath());
        }

        return Result.success("获取文件路径成功", resource);
    }

    @Override
    public Result getCourseResources(Integer courseId, Long page, Long pageSize) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.isNull("parent_id");
        queryWrapper.orderByDesc("create_time");
        
        Page<Resource> pageObj = new Page<>(page, pageSize);
        Page<Resource> resultPage = resourceMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Resource> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取资源列表成功", pageResult);
    }

    @Override
    public Result getFolderResources(Integer courseId, String parentId, Long page, Long pageSize) {
        QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.eq("parent_id", parentId);
        queryWrapper.orderByDesc("is_folder");
        queryWrapper.orderByDesc("create_time");
        
        Page<Resource> pageObj = new Page<>(page, pageSize);
        Page<Resource> resultPage = resourceMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Resource> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取文件夹资源成功", pageResult);
    }
}
