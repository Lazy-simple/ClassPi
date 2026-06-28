package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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

    private String name;          // 文件/链接名称

    private String type;          // 资源类型：folder/file/link

    private String path;          // 文件存储路径

    private String parentId;      // 父文件夹ID

    private String folderName;    // 文件夹名称（当 isFolder=1 时使用）

    private Integer isFolder;     // 是否为文件夹：1-是，0-否

    private String uploaderId;

    private String uploaderName;

    private Long fileSize;

    private String fileType;

    private Date createTime;

    private Date updateTime;

    @TableLogic
    private Integer deleted;      // 逻辑删除字段：1-已删除，0-未删除
}
