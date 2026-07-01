import request from '@/utils/request'

// 获取当前学生的作业通知
export const getHomeworkNotices = () => {
    return request({
        url: '/api/notice/homework',
        method: 'GET'
    })
}

// 标记通知已读
export const markNoticeRead = (noticeIds) => {
    return request({
        url: '/api/notice/mark-read',
        method: 'POST',
        data: { noticeIds }
    })
}

// 获取未读通知数量
export const getUnreadNoticeCount = () => {
    return request({
        url: '/api/notice/unread-count',
        method: 'GET'
    })
}