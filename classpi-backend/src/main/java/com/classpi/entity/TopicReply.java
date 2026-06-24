package com.classpi.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("topic_reply")
public class TopicReply {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private Integer topicId;

    private String content;

    private String authorId;

    private String authorName;

    private Integer authorType;

    private Integer isAnonymous;

    private Date createTime;

    private Integer deleted;
}
