package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.dto.TopicCreateDTO;
import com.classpi.dto.TopicReplyDTO;
import com.classpi.service.TopicService;
import com.classpi.security.RequireRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/topic")
@CrossOrigin
public class TopicController {

    @Autowired
    private TopicService topicService;

    @PostMapping("/create")
    public Result createTopic(@RequestBody @Validated TopicCreateDTO dto) {
        return topicService.createTopic(
            dto.getCourseId(),
            dto.getCourseNo(),
            dto.getTitle(),
            dto.getContent(),
            dto.getAuthorId(),
            dto.getAuthorName(),
            dto.getAuthorType(),
            dto.getIsAnonymous()
        );
    }

    @PutMapping("/{id}")
    public Result editTopic(@PathVariable Integer id,
                            @RequestParam String title,
                            @RequestParam String content,
                            @RequestParam String authorId,
                            @RequestParam String identity) {
        return topicService.editTopic(id, title, content, authorId, identity);
    }

    @DeleteMapping("/{id}")
    public Result deleteTopic(@PathVariable Integer id,
                              @RequestParam String authorId,
                              @RequestParam String identity) {
        return topicService.deleteTopic(id, authorId, identity);
    }

    @PutMapping("/{id}/top")
    @RequireRole({"teacher"})
    public Result topTopic(@PathVariable Integer id) {
        return topicService.topTopic(id);
    }

    @PutMapping("/{id}/cancel-top")
    @RequireRole({"teacher"})
    public Result cancelTopTopic(@PathVariable Integer id) {
        return topicService.cancelTopTopic(id);
    }

    @PutMapping("/{id}/disable")
    @RequireRole({"teacher"})
    public Result disableTopic(@PathVariable Integer id) {
        return topicService.disableTopic(id);
    }

    @GetMapping("/course/{courseId}")
    public Result getCourseTopics(@PathVariable Integer courseId,
                                   @RequestParam(defaultValue = "1") Long page,
                                   @RequestParam(defaultValue = "10") Long pageSize) {
        return topicService.getCourseTopics(courseId, page, pageSize);
    }

    @GetMapping("/{id}")
    public Result getTopicDetail(@PathVariable Integer id) {
        return topicService.getTopicDetail(id);
    }

    @PostMapping("/{id}/reply")
    public Result replyTopic(@PathVariable Integer id,
                             @RequestBody @Validated TopicReplyDTO dto) {
        return topicService.replyTopic(
            id,
            dto.getContent(),
            dto.getAuthorId(),
            dto.getAuthorName(),
            dto.getAuthorType(),
            dto.getIsAnonymous()
        );
    }

    @DeleteMapping("/reply/{id}")
    public Result deleteReply(@PathVariable Integer id,
                              @RequestParam String authorId,
                              @RequestParam String identity) {
        return topicService.deleteReply(id, authorId, identity);
    }

    @GetMapping("/{id}/replies")
    public Result getTopicReplies(@PathVariable Integer id,
                                   @RequestParam(defaultValue = "1") Long page,
                                   @RequestParam(defaultValue = "10") Long pageSize) {
        return topicService.getTopicReplies(id, page, pageSize);
    }

    /**
     * 禁言话题（教师用）
     * PUT /topic/{id}/disable-comment
     */
    @PutMapping("/{id}/disable-comment")
    @RequireRole({"teacher"})
    public Result disableComment(@PathVariable Integer id) {
        return topicService.disableComment(id);
    }

    /**
     * 解禁话题（教师用）
     * PUT /topic/{id}/enable-comment
     */
    @PutMapping("/{id}/enable-comment")
    @RequireRole({"teacher"})
    public Result enableComment(@PathVariable Integer id) {
        return topicService.enableComment(id);
    }
}
