package com.classpi.service;

import com.classpi.common.Result;

public interface TopicService {
    Result createTopic(Integer courseId, String courseNo, String title, String content, String authorId, String authorName, Integer authorType, Integer isAnonymous);
    
    Result editTopic(Integer id, String title, String content, String authorId, String identity);
    
    Result deleteTopic(Integer id, String authorId, String identity);
    
    Result topTopic(Integer id);
    
    Result cancelTopTopic(Integer id);
    
    Result disableTopic(Integer id);
    
    Result getCourseTopics(Integer courseId, Long page, Long pageSize);
    
    Result getTopicDetail(Integer id);
    
    Result replyTopic(Integer topicId, String content, String authorId, String authorName, Integer authorType, Integer isAnonymous);
    
    Result deleteReply(Integer id, String authorId, String identity);
    
    Result getTopicReplies(Integer topicId, Long page, Long pageSize);

    /**
     * 禁言话题（禁止评论）
     */
    Result disableComment(Integer id);

    /**
     * 解禁话题（允许评论）
     */
    Result enableComment(Integer id);
}
