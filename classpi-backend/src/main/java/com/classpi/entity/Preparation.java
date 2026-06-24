package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("preparation")
public class Preparation {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String teacherId;

    private String teacherName;

    private String title;

    private String type;

    private String content;

    private Integer courseId;

    private String courseNo;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;
}
