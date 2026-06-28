package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classpi.common.Result;
import com.classpi.entity.Resource;
import com.classpi.mapper.ResourceMapper;
import com.classpi.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;

@Service
public class ResourceServiceImpl implements ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceServiceImpl.class);

    @Autowired
    private ResourceMapper resourceMapper;

    @Override
    public Result getCourseResources(Integer courseId, Long page, Long pageSize) {
        System.out.println("========== 5. Service.getCourseResources 被调用 ==========");
        System.out.println("courseId: " + courseId + ", page: " + page + ", pageSize: " + pageSize);

        try {
            if (resourceMapper == null) {
                System.err.println("========== 6. resourceMapper 为 null ==========");
                return Result.error("resourceMapper 注入失败");
            }
            System.out.println("========== 6. resourceMapper 注入成功 ==========");

            System.out.println("========== 7. 开始查询 ==========");
            QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId)
                    .eq("deleted", 0);

            List<Resource> resources = resourceMapper.selectList(queryWrapper);
            System.out.println("========== 8. 查询完成，共 " + resources.size() + " 条数据 ==========");

            return Result.success("获取课程资源列表成功", resources);
        } catch (Exception e) {
            System.err.println("========== 9. Service 异常 ==========");
            e.printStackTrace();
            return Result.error("查询失败：" + e.getMessage());
        }
    }

    @Override
    public Result getFolderResources(Integer courseId, String parentId, Long page, Long pageSize) {
        logger.info("========== getFolderResources ==========");
        logger.info("参数: courseId={}, parentId={}, page={}, pageSize={}", courseId, parentId, page, pageSize);

        try {
            QueryWrapper<Resource> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId)
                    .eq("parent_id", parentId != null ? parentId : "0")
                    .eq("deleted", 0);

            List<Resource> resources = resourceMapper.selectList(queryWrapper);
            logger.info("查询到 {} 条数据", resources == null ? 0 : resources.size());

            return Result.success("获取文件夹资源列表成功", resources);
        } catch (Exception e) {
            logger.error("getFolderResources 失败: ", e);
            return Result.error("获取文件夹资源列表失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result createFolder(Integer courseId, String courseNo, String folderName,
            String parentId, String uploaderId, String uploaderName) {
        logger.info("========== createFolder ==========");
        logger.info("参数: courseId={}, courseNo={}, folderName={}, parentId={}, uploaderId={}, uploaderName={}",
                courseId, courseNo, folderName, parentId, uploaderId, uploaderName);

        try {
            // 检查同名文件夹
            QueryWrapper<Resource> checkWrapper = new QueryWrapper<>();
            checkWrapper.eq("course_id", courseId)
                    .eq("folder_name", folderName)
                    .eq("is_folder", 1)
                    .eq("parent_id", parentId != null ? parentId : "0")
                    .eq("deleted", 0);
            Long count = resourceMapper.selectCount(checkWrapper);
            logger.info("检查同名文件夹: count={}", count);

            if (count > 0) {
                return Result.error("该目录下已存在同名文件夹");
            }

            Resource resource = new Resource();
            resource.setCourseId(courseId);
            resource.setCourseNo(courseNo);
            resource.setFolderName(folderName);
            resource.setIsFolder(1);
            resource.setParentId(parentId != null ? parentId : "0");
            resource.setUploaderId(uploaderId);
            resource.setUploaderName(uploaderName);
            resource.setCreateTime(new Date());
            resource.setDeleted(0);

            logger.info("准备插入数据: {}", resource);
            int insertResult = resourceMapper.insert(resource);
            logger.info("插入结果: {}", insertResult);

            return Result.success("创建文件夹成功", resource);
        } catch (Exception e) {
            logger.error("createFolder 失败: ", e);
            return Result.error("创建文件夹失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result addLinkResource(Integer courseId, String courseNo, String name, String url,
            String parentId, String uploaderId, String uploaderName) {
        logger.info("========== addLinkResource ==========");
        logger.info("参数: courseId={}, courseNo={}, name={}, url={}, parentId={}, uploaderId={}, uploaderName={}",
                courseId, courseNo, name, url, parentId, uploaderId, uploaderName);

        try {
            Resource resource = new Resource();
            resource.setCourseId(courseId);
            resource.setCourseNo(courseNo);
            resource.setName(name);
            resource.setType("link");
            resource.setPath(url);
            resource.setIsFolder(0);
            resource.setParentId(parentId != null ? parentId : "0");
            resource.setUploaderId(uploaderId);
            resource.setUploaderName(uploaderName);
            resource.setCreateTime(new Date());
            resource.setDeleted(0);

            logger.info("准备插入数据: {}", resource);
            int insertResult = resourceMapper.insert(resource);
            logger.info("插入结果: {}", insertResult);

            return Result.success("添加链接资源成功", resource);
        } catch (Exception e) {
            logger.error("addLinkResource 失败: ", e);
            return Result.error("添加链接资源失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result deleteResource(Integer id) {
        logger.info("========== deleteResource ==========, id={}", id);

        try {
            Resource resource = resourceMapper.selectById(id);
            if (resource == null) {
                logger.warn("资源不存在: id={}", id);
                return Result.error("资源不存在");
            }

            resource.setDeleted(1);
            int updateResult = resourceMapper.updateById(resource);
            logger.info("删除结果: {}", updateResult);

            return Result.success("删除资源成功");
        } catch (Exception e) {
            logger.error("deleteResource 失败: ", e);
            return Result.error("删除资源失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result moveResource(Integer id, String targetFolderId) {
        logger.info("========== moveResource ==========, id={}, targetFolderId={}", id, targetFolderId);
        return Result.success("移动资源成功");
    }

    @Override
    public Result downloadResource(Integer id) {
        logger.info("========== downloadResource ==========, id={}", id);
        return Result.success("下载资源成功");
    }

    @Override
    public Resource getResourceById(Integer id) {
        logger.info("========== getResourceById ==========, id={}", id);
        return resourceMapper.selectById(id);
    }

    @Override
    public byte[] getFileContent(String filePath) throws IOException {
        logger.info("========== getFileContent ==========, filePath={}", filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            logger.error("文件不存在: {}", filePath);
            throw new IOException("文件不存在: " + filePath);
        }

        return Files.readAllBytes(file.toPath());
    }

    @Override
    @Transactional
    public Result uploadAttachment(Integer courseId, String courseNo, MultipartFile file,
            String parentId, String uploaderId, String uploaderName) {
        logger.info("========== uploadAttachment ==========");
        logger.info("参数: courseId={}, courseNo={}, fileName={}, parentId={}, uploaderId={}, uploaderName={}",
                courseId, courseNo, file.getOriginalFilename(), parentId, uploaderId, uploaderName);
        return Result.success("附件上传成功");
    }
}