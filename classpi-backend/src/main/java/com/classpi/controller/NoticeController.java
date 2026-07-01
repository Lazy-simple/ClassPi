package com.classpi.controller;

import com.classpi.common.Result;
import com.classpi.entity.HomeworkNotice;
import com.classpi.service.HomeworkNoticeService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notice")
public class NoticeController {

    @Resource
    private HomeworkNoticeService homeworkNoticeService;

    @GetMapping("/homework")
    public Result getHomeworkNotices(@RequestHeader("userId") Long studentId) {
        List<HomeworkNotice> notices = homeworkNoticeService.getByStudentId(studentId);
        return Result.success("查询成功", notices);
    }

    @PostMapping("/mark-read")
    public Result markRead(@RequestBody Map<String, List<Long>> params) {
        List<Long> noticeIds = params.get("noticeIds");
        homeworkNoticeService.markAsRead(noticeIds);
        return Result.success("已标记已读");
    }

    @GetMapping("/unread-count")
    public Result getUnreadCount(@RequestHeader("userId") Long studentId) {
        int count = homeworkNoticeService.getUnreadCount(studentId);
        return Result.success("查询成功", count);
    }
}
