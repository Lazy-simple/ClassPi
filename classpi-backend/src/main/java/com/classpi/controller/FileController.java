package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.file.FileUploadDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.util.UUID;

@RestController
@RequestMapping("/api/file")
public class FileController {

    // 本地存储路径，自行修改
    private final String uploadPath = "E:/upload/";
    private final String prefixUrl = "http://localhost:8080/api/file/static/";

    @PostMapping("/upload")
    public Result<FileUploadDTO> upload(@RequestParam("file") MultipartFile file) {
        try {
            File dir = new File(uploadPath);
            if (!dir.exists()) dir.mkdirs();
            // 原始文件名
            String originalName = file.getOriginalFilename();
            // 后缀
            String suffix = originalName.substring(originalName.lastIndexOf("."));
            String newFileName = UUID.randomUUID() + suffix;
            File target = new File(uploadPath + newFileName);
            file.transferTo(target);

            FileUploadDTO dto = new FileUploadDTO();
            dto.setFileName(originalName);
            dto.setFileType(suffix.replace(".", ""));
            dto.setFileUrl(prefixUrl + newFileName);
            return Result.success("文件上传成功", dto);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("文件上传失败");
        }
    }
}