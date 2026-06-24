package com.classpi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.classpi.entity.HomeworkNotice;
import com.classpi.mapper.HomeworkNoticeMapper;
import com.classpi.service.HomeworkNoticeService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class HomeworkNoticeServiceImpl extends ServiceImpl<HomeworkNoticeMapper, HomeworkNotice> implements HomeworkNoticeService {

    @Override
    public void batchCreateNotice(Long homeworkId, List<Long> studentIds, String type) {
        List<HomeworkNotice> noticeList = new ArrayList<>();
        for (Long sid : studentIds) {
            HomeworkNotice notice = new HomeworkNotice();
            notice.setHomeworkId(homeworkId);
            notice.setStudentId(sid);
            notice.setNoticeType(type);
            notice.setIsRead(0);
            noticeList.add(notice);
        }
        this.saveBatch(noticeList);
    }
}
