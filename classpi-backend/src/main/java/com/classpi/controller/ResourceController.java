package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.ResourceCreateDTO;
import com.classpi.service.ResourceService;
import com.classpi.security.RequireRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resource")
@CrossOrigin
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

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
            dto.getUploaderName()
        );
    }

    @DeleteMapping("/{id}")
    @RequireRole({"teacher"})
    public Result deleteResource(@PathVariable Integer id) {
        return resourceService.deleteResource(id);
    }

    @PutMapping("/{id}/move")
    public Result moveResource(@PathVariable Integer id,
                               @RequestParam String targetFolderId) {
        return resourceService.moveResource(id, targetFolderId);
    }

    @GetMapping("/download/{id}")
    public Result downloadResource(@PathVariable Integer id) {
        return resourceService.downloadResource(id);
    }

    @GetMapping("/course/{courseId}")
    public Result getCourseResources(@PathVariable Integer courseId,
                                     @RequestParam(defaultValue = "1") Long page,
                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return resourceService.getCourseResources(courseId, page, pageSize);
    }

    @GetMapping("/folder/{courseId}")
    public Result getFolderResources(@PathVariable Integer courseId,
                                     @RequestParam String parentId,
                                     @RequestParam(defaultValue = "1") Long page,
                                     @RequestParam(defaultValue = "10") Long pageSize) {
        return resourceService.getFolderResources(courseId, parentId, page, pageSize);
    }
}
