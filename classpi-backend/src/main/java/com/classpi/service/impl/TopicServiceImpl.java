package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.classpi.common.Result;
import com.classpi.entity.Topic;
import com.classpi.entity.TopicReply;
import com.classpi.mapper.TopicMapper;
import com.classpi.mapper.TopicReplyMapper;
import com.classpi.service.TopicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class TopicServiceImpl implements TopicService {

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private TopicReplyMapper topicReplyMapper;

    @Override
    @Transactional
    public Result createTopic(Integer courseId, String courseNo, String title, String content,
                              String authorId, String authorName, Integer authorType, Integer isAnonymous) {
        try {
            Topic topic = new Topic();
            topic.setCourseId(courseId);
            topic.setCourseNo(courseNo);
            topic.setTitle(title);
            topic.setContent(content);
            topic.setAuthorId(authorId);
            topic.setAuthorName(authorName);
            topic.setAuthorType(authorType);
            topic.setIsTop(0);
            topic.setIsAnonymous(isAnonymous != null ? isAnonymous : 0);
            topic.setReplyCount(0);
            topic.setStatus(0);
            topic.setCreateTime(new Date());  // 改用 Date
            topic.setDeleted(0);

            int result = topicMapper.insert(topic);
            if (result > 0) {
                return Result.success("创建话题成功", topic);
            } else {
                return Result.error("创建话题失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("创建话题失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result editTopic(Integer id, String title, String content, String authorId, String identity) {
        try {
            Topic topic = topicMapper.selectById(id);
            if (topic == null) {
                return Result.error("话题不存在");
            }

            if (!"teacher".equals(identity) && !topic.getAuthorId().equals(authorId)) {
                return Result.error("无权限编辑该话题");
            }

            topic.setTitle(title);
            topic.setContent(content);
            topic.setUpdateTime(new Date());  // 改用 Date

            int result = topicMapper.updateById(topic);
            if (result > 0) {
                return Result.success("编辑话题成功", topic);
            } else {
                return Result.error("编辑话题失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("编辑话题失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result deleteTopic(Integer id, String authorId, String identity) {
        try {
            Topic topic = topicMapper.selectById(id);
            if (topic == null) {
                return Result.error("话题不存在");
            }

            if (!"teacher".equals(identity) && !topic.getAuthorId().equals(authorId)) {
                return Result.error("无权限删除该话题");
            }

            topic.setDeleted(1);
            int result = topicMapper.updateById(topic);
            if (result > 0) {
                return Result.success("删除话题成功");
            } else {
                return Result.error("删除话题失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除话题失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result topTopic(Integer id) {
        try {
            Topic topic = topicMapper.selectById(id);
            if (topic == null) {
                return Result.error("话题不存在");
            }

            topic.setIsTop(1);
            topic.setUpdateTime(new Date());  // 改用 Date

            int result = topicMapper.updateById(topic);
            if (result > 0) {
                return Result.success("置顶成功");
            } else {
                return Result.error("置顶失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("置顶失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result cancelTopTopic(Integer id) {
        try {
            Topic topic = topicMapper.selectById(id);
            if (topic == null) {
                return Result.error("话题不存在");
            }

            topic.setIsTop(0);
            topic.setUpdateTime(new Date());  // 改用 Date

            int result = topicMapper.updateById(topic);
            if (result > 0) {
                return Result.success("取消置顶成功");
            } else {
                return Result.error("取消置顶失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("取消置顶失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result disableTopic(Integer id) {
        try {
            Topic topic = topicMapper.selectById(id);
            if (topic == null) {
                return Result.error("话题不存在");
            }

            topic.setStatus(1);
            topic.setUpdateTime(new Date());  // 改用 Date

            int result = topicMapper.updateById(topic);
            if (result > 0) {
                return Result.success("禁用话题成功");
            } else {
                return Result.error("禁用话题失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("禁用话题失败：" + e.getMessage());
        }
    }

    @Override
    public Result getCourseTopics(Integer courseId, Long page, Long pageSize) {
        try {
            QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("course_id", courseId)
                    .eq("deleted", 0)
                    .orderByDesc("is_top")
                    .orderByDesc("create_time");

            List<Topic> topics = topicMapper.selectList(queryWrapper);

            for (Topic topic : topics) {
                QueryWrapper<TopicReply> replyWrapper = new QueryWrapper<>();
                replyWrapper.eq("topic_id", topic.getId())
                        .eq("deleted", 0);
                Long count = topicReplyMapper.selectCount(replyWrapper);
                topic.setReplyCount(count.intValue());
            }

            return Result.success("查询课程话题列表成功", topics);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("查询话题失败：" + e.getMessage());
        }
    }

    @Override
    public Result getTopicDetail(Integer id) {
        try {
            Topic topic = topicMapper.selectById(id);
            if (topic == null) {
                return Result.error("话题不存在");
            }
            return Result.success("获取话题详情成功", topic);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取话题详情失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result replyTopic(Integer topicId, String content, String authorId,
                             String authorName, Integer authorType, Integer isAnonymous) {
        try {
            Topic topic = topicMapper.selectById(topicId);
            if (topic == null) {
                return Result.error("话题不存在");
            }

            TopicReply reply = new TopicReply();
            reply.setTopicId(topicId);
            reply.setContent(content);
            reply.setAuthorId(authorId);
            reply.setAuthorName(authorName);
            reply.setAuthorType(authorType);
            reply.setIsAnonymous(isAnonymous != null ? isAnonymous : 0);
            reply.setCreateTime(new Date());  // 改用 Date
            reply.setDeleted(0);

            int result = topicReplyMapper.insert(reply);
            if (result > 0) {
                topic.setReplyCount(topic.getReplyCount() + 1);
                topicMapper.updateById(topic);
                return Result.success("评论成功", reply);
            } else {
                return Result.error("评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("评论失败：" + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Result deleteReply(Integer id, String authorId, String identity) {
        try {
            TopicReply reply = topicReplyMapper.selectById(id);
            if (reply == null) {
                return Result.error("评论不存在");
            }

            if (!"teacher".equals(identity) && !reply.getAuthorId().equals(authorId)) {
                return Result.error("无权限删除该评论");
            }

            reply.setDeleted(1);
            int result = topicReplyMapper.updateById(reply);
            if (result > 0) {
                Topic topic = topicMapper.selectById(reply.getTopicId());
                if (topic != null && topic.getReplyCount() > 0) {
                    topic.setReplyCount(topic.getReplyCount() - 1);
                    topicMapper.updateById(topic);
                }
                return Result.success("删除评论成功");
            } else {
                return Result.error("删除评论失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除评论失败：" + e.getMessage());
        }
    }

    @Override
    public Result getTopicReplies(Integer topicId, Long page, Long pageSize) {
        try {
            QueryWrapper<TopicReply> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("topic_id", topicId)
                    .eq("deleted", 0)
                    .orderByAsc("create_time");

            List<TopicReply> replies = topicReplyMapper.selectList(queryWrapper);
            return Result.success("获取评论列表成功", replies);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取评论列表失败：" + e.getMessage());
        }
    }
}
