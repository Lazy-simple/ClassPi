package com.classpi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.entity.Homework;
import com.classpi.entity.HomeworkNotice;
import com.classpi.mapper.HomeworkMapper;
import com.classpi.mapper.HomeworkNoticeMapper;
import com.classpi.service.HomeworkNoticeService;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeworkNoticeServiceImpl extends ServiceImpl<HomeworkNoticeMapper, HomeworkNotice> implements HomeworkNoticeService {

    @Resource
    private HomeworkMapper homeworkMapper;

    @Override
    public void batchCreateNotice(Long homeworkId, List<Long> studentIds, String type) {
        if (studentIds == null || studentIds.isEmpty()) return;

        Homework homework = homeworkMapper.selectById(homeworkId);
        if (homework == null) return;

        String content = "publish".equals(type)
                ? "📢 新作业「" + homework.getTitle() + "」已发布，请及时查看！"
                : "⏰ 催交提醒：作业「" + homework.getTitle() + "」尚未提交，请尽快完成！";

        List<HomeworkNotice> noticeList = new ArrayList<>();
        for (Long sid : studentIds) {
            HomeworkNotice notice = new HomeworkNotice();
            notice.setHomeworkId(homeworkId);
            notice.setStudentId(sid);
            notice.setNoticeType(type);
            notice.setContent(content);
            notice.setIsRead(0);
            notice.setCreateTime(LocalDateTime.now());
            notice.setDeleted(0);
            noticeList.add(notice);
        }
        this.saveBatch(noticeList);
    }

    @Override
    public List<HomeworkNotice> getByStudentId(Long studentId) {
        LambdaQueryWrapper<HomeworkNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkNotice::getStudentId, studentId)
                .eq(HomeworkNotice::getDeleted, 0)
                .orderByDesc(HomeworkNotice::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public void markAsRead(List<Long> noticeIds) {
        if (noticeIds == null || noticeIds.isEmpty()) return;
        List<HomeworkNotice> notices = this.listByIds(noticeIds);
        for (HomeworkNotice notice : notices) {
            notice.setIsRead(1);
        }
        this.updateBatchById(notices);
    }

    @Override
    public int getUnreadCount(Long studentId) {
        LambdaQueryWrapper<HomeworkNotice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HomeworkNotice::getStudentId, studentId)
                .eq(HomeworkNotice::getIsRead, 0)
                .eq(HomeworkNotice::getDeleted, 0);
        return (int) this.count(wrapper);
    }
}
