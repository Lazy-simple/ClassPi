package com.classpi.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.classpi.entity.HomeworkNotice;
import java.util.List;

public interface HomeworkNoticeService extends IService<HomeworkNotice> {
    /**
     * 批量创建通知
     * @param homeworkId 作业id
     * @param studentIds 学生id集合
     * @param type 类型 publish/remind
     */
    void batchCreateNotice(Long homeworkId, List<Long> studentIds, String type);
}
