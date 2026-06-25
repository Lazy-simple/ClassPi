package com.classpi.service.impl;

import com.classpi.common.Result;
import com.classpi.service.TopicService;
import org.springframework.stereotype.Service;

@Service
public class TopicServiceImpl implements TopicService {

    @Override
    public Result createTopic(Integer courseId, String courseNo, String title, String content, String authorId, String authorName, Integer authorType, Integer isAnonymous) {
        return Result.success("创建话题成功");
    }

    @Override
    public Result editTopic(Integer id, String title, String content, String authorId, String identity) {
        return Result.success("编辑话题成功");
    }

    @Override
    public Result deleteTopic(Integer id, String authorId, String identity) {
        return Result.success("删除话题成功");
    }

    @Override
    public Result topTopic(Integer id) {
        return Result.success("置顶成功");
    }

    @Override
    public Result cancelTopTopic(Integer id) {
        return Result.success("取消置顶成功");
    }

    @Override
    public Result disableTopic(Integer id) {
        return Result.success("禁用话题成功");
    }

    @Override
    public Result getCourseTopics(Integer courseId, Long page, Long pageSize) {
        return Result.success("查询课程话题列表成功");
    }

    @Override
    public Result getTopicDetail(Integer id) {
        return Result.success("获取话题详情成功");
    }

    @Override
    public Result replyTopic(Integer topicId, String content, String authorId, String authorName, Integer authorType, Integer isAnonymous) {
        return Result.success("评论成功");
    }

    @Override
    public Result deleteReply(Integer id, String authorId, String identity) {
        return Result.success("删除评论成功");
    }

    @Override
    public Result getTopicReplies(Integer topicId, Long page, Long pageSize) {
        return Result.success("获取评论列表成功");
    }
}
