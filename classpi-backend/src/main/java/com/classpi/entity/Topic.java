package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("topic")
public class Topic {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer courseId;

    private String courseNo;

    private String title;

    private String content;

    private String authorId;

    private String authorName;

    private Integer authorType;

    private Integer isTop;

    private Integer isAnonymous;

    private Integer replyCount;

    private Integer status;

    private Date createTime;

    private Date updateTime;

    private Integer deleted;
}
