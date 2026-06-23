package com.classpi.dto.file;

import lombok.Data;

@Data
public class FileUploadDTO {
    private String fileUrl;
    private String fileName;
    private String fileType;
}