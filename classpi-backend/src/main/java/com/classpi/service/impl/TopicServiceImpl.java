package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.classpi.common.PageResult;
import com.classpi.common.Result;
import com.classpi.entity.Topic;
import com.classpi.entity.TopicReply;
import com.classpi.mapper.TopicMapper;
import com.classpi.mapper.TopicReplyMapper;
import com.classpi.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicReplyMapper topicReplyMapper;

    @Override
    @Transactional
    public Result createTopic(Integer courseId, String courseNo, String title, String content, String authorId, String authorName, Integer authorType, Integer isAnonymous) {
        Topic topic = new Topic();
        topic.setCourseId(courseId);
        topic.setCourseNo(courseNo);
        topic.setTitle(title);
        topic.setContent(content);
        topic.setAuthorId(authorId);
        topic.setAuthorName(authorName);
        topic.setAuthorType(authorType);
        topic.setIsAnonymous(isAnonymous);
        topic.setIsTop(0);
        topic.setReplyCount(0);
        topic.setStatus(0);

        topicMapper.insert(topic);
        return Result.success("话题发布成功", topic);
    }

    @Override
    @Transactional
    public Result editTopic(Integer id, String title, String content, String authorId, String identity) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !topic.getAuthorId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权编辑他人的话题");
        }

        topic.setTitle(title);
        topic.setContent(content);
        topicMapper.updateById(topic);
        return Result.success("话题编辑成功", topic);
    }

    @Override
    @Transactional
    public Result deleteTopic(Integer id, String authorId, String identity) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !topic.getAuthorId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除他人的话题");
        }

        QueryWrapper<TopicReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", id);
        topicReplyMapper.delete(queryWrapper);

        topicMapper.deleteById(id);
        return Result.success("话题删除成功");
    }

    @Override
    @Transactional
    public Result topTopic(Integer id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        topic.setIsTop(1);
        topicMapper.updateById(topic);
        return Result.success("话题置顶成功", topic);
    }

    @Override
    @Transactional
    public Result cancelTopTopic(Integer id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        topic.setIsTop(0);
        topicMapper.updateById(topic);
        return Result.success("取消置顶成功", topic);
    }

    @Override
    @Transactional
    public Result disableTopic(Integer id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        topic.setStatus(1);
        topicMapper.updateById(topic);
        return Result.success("已禁止该话题讨论", topic);
    }

    @Override
    public Result getCourseTopics(Integer courseId, Long page, Long pageSize) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("course_id", courseId);
        queryWrapper.orderByDesc("is_top");
        queryWrapper.orderByDesc("create_time");
        
        Page<Topic> pageObj = new Page<>(page, pageSize);
        Page<Topic> resultPage = topicMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<Topic> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取话题列表成功", pageResult);
    }

    @Override
    public Result getTopicDetail(Integer id) {
        Topic topic = topicMapper.selectById(id);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        QueryWrapper<TopicReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", id);
        queryWrapper.orderByAsc("create_time");
        List<TopicReply> replies = topicReplyMapper.selectList(queryWrapper);

        Map<String, Object> result = new HashMap<>();
        result.put("topic", topic);
        result.put("replies", replies);
        return Result.success("获取话题详情成功", result);
    }

    @Override
    @Transactional
    public Result replyTopic(Integer topicId, String content, String authorId, String authorName, Integer authorType, Integer isAnonymous) {
        Topic topic = topicMapper.selectById(topicId);
        if (topic == null) {
            return Result.error("话题不存在");
        }

        if (topic.getStatus() == 1) {
            return Result.error("该话题已被禁止讨论");
        }

        TopicReply reply = new TopicReply();
        reply.setTopicId(topicId);
        reply.setContent(content);
        reply.setAuthorId(authorId);
        reply.setAuthorName(authorName);
        reply.setAuthorType(authorType);
        reply.setIsAnonymous(isAnonymous);

        topicReplyMapper.insert(reply);

        topic.setReplyCount(topic.getReplyCount() + 1);
        topicMapper.updateById(topic);

        return Result.success("回复成功", reply);
    }

    @Override
    @Transactional
    public Result deleteReply(Integer id, String authorId, String identity) {
        TopicReply reply = topicReplyMapper.selectById(id);
        if (reply == null) {
            return Result.error("回复不存在");
        }

        if (!"teacher".equalsIgnoreCase(identity) && !reply.getAuthorId().equals(authorId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除他人的回复");
        }

        Integer topicId = reply.getTopicId();
        topicReplyMapper.deleteById(id);

        Topic topic = topicMapper.selectById(topicId);
        if (topic != null && topic.getReplyCount() > 0) {
            topic.setReplyCount(topic.getReplyCount() - 1);
            topicMapper.updateById(topic);
        }

        return Result.success("回复删除成功");
    }

    @Override
    public Result getTopicReplies(Integer topicId, Long page, Long pageSize) {
        QueryWrapper<TopicReply> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("topic_id", topicId);
        queryWrapper.orderByAsc("create_time");
        
        Page<TopicReply> pageObj = new Page<>(page, pageSize);
        Page<TopicReply> resultPage = topicReplyMapper.selectPage(pageObj, queryWrapper);
        
        PageResult<TopicReply> pageResult = PageResult.of(
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize(),
            resultPage.getRecords()
        );
        
        return Result.success("获取回复列表成功", pageResult);
    }
}
