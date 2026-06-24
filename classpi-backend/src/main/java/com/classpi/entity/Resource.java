package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("resource")
public class Resource {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer courseId;

    private String courseNo;

    private String name;

    private String type;

    private String path;

    private String parentId;

    private String folderName;

    private Integer isFolder;

    private String uploaderId;

    private String uploaderName;

    private Long fileSize;

    private String fileType;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;
}
