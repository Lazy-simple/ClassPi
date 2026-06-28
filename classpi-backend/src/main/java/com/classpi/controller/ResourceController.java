package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.ResourceCreateDTO;
import com.classpi.entity.Resource;
import com.classpi.service.ResourceService;
import com.classpi.security.RequireRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

@RestController
@RequestMapping("/resource")
@CrossOrigin(origins = "*")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    // ========== 添加这个测试接口 ==========
    @GetMapping("/ping")
    public String ping() {
        System.out.println("========== ping 接口被调用 ==========");
        return "pong";
    }
    // =====================================

    @PostMapping("/folder")
    public Result createFolder(@RequestParam Integer courseId,
            @RequestParam String courseNo,
            @RequestParam String folderName,
            @RequestParam(required = false) String parentId,
            @RequestParam String uploaderId,
            @RequestParam String uploaderName) {
        return resourceService.createFolder(courseId, courseNo, folderName, parentId, uploaderId, uploaderName);
    }

    @PostMapping("/upload")
    public Result uploadAttachment(@RequestParam Integer courseId,
            @RequestParam String courseNo,
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) String parentId,
            @RequestParam String uploaderId,
            @RequestParam String uploaderName) {
        return resourceService.uploadAttachment(courseId, courseNo, file, parentId, uploaderId, uploaderName);
    }

    @PostMapping("/link")
    public Result addLinkResource(@RequestBody @Validated ResourceCreateDTO dto) {
        return resourceService.addLinkResource(
                dto.getCourseId(),
                dto.getCourseNo(),
                dto.getName(),
                dto.getUrl(),
                dto.getParentId(),
                dto.getUploaderId(),
                dto.getUploaderName());
    }

    @DeleteMapping("/{id}")
    @RequireRole({ "teacher" })
    public Result deleteResource(@PathVariable Integer id) {
        return resourceService.deleteResource(id);
    }

    @PutMapping("/{id}/move")
    public Result moveResource(@PathVariable Integer id,
            @RequestParam String targetFolderId) {
        return resourceService.moveResource(id, targetFolderId);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<byte[]> downloadResource(@PathVariable Integer id) {
        try {
            Resource resource = resourceService.getResourceById(id);
            if (resource == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String fileName = resource.getName();
            String contentType = resource.getFileType();
            if (contentType == null || contentType.isEmpty()) {
                contentType = "application/octet-stream";
            }

            byte[] fileContent = resourceService.getFileContent(resource.getPath());

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(contentType));
            headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, StandardCharsets.UTF_8));
            headers.setContentLength(fileContent.length);

            return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/course/{courseId}")
    public Result getCourseResources(@PathVariable Integer courseId,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize) {
        // 添加这行，看方法是否被调用
        System.out.println("========== 1. Controller.getCourseResources 被调用 ==========");
        System.out.println("courseId: " + courseId + ", page: " + page + ", pageSize: " + pageSize);

        try {
            System.out.println("========== 2. 准备调用 Service ==========");
            Result result = resourceService.getCourseResources(courseId, page, pageSize);
            System.out.println("========== 3. Service 返回结果 ==========");
            return result;
        } catch (Exception e) {
            System.err.println("========== 4. Controller 捕获异常 ==========");
            e.printStackTrace();
            return Result.error("获取资源失败：" + e.getMessage());
        }
    }

    @GetMapping("/folder/{courseId}")
    public Result getFolderResources(@PathVariable Integer courseId,
            @RequestParam String parentId,
            @RequestParam(defaultValue = "1") Long page,
            @RequestParam(defaultValue = "10") Long pageSize) {
        return resourceService.getFolderResources(courseId, parentId, page, pageSize);
    }
}
