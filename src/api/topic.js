import request from '@/utils/request'
// 发布话题
export function publishTopic(data) {
    return request({
        url: '/course/topic/publish',
        method: 'post',
        data
    })
}
// 编辑话题
export function editTopic(data) {
    return request({
        url: '/course/topic/edit',
        method: 'put',
        data
    })
}
// 删除话题
export function deleteTopic(topicId) {
    return request({
        url: `/course/topic/${topicId}`,
        method: 'delete'
    })
}
// 置顶/取消置顶
export function setTopicTop(topicId, isTop) {
    return request({
        url: `/course/topic/top/${topicId}`,
        method: 'put',
        params: { isTop }
    })
}
// 发布评论
export function addComment(data) {
    return request({
        url: '/course/topic/comment',
        method: 'post',
        data
    })
}
// 获取课程话题列表
export function getTopicList(courseId) {
    return request({
        url: `/course/topic/list/${courseId}`,
        method: 'get'
    })
}
// 获取话题评论
export function getCommentList(topicId) {
    return request({
        url: `/course/topic/comment/list/${topicId}`,
        method: 'get'
    })
}
// 开关课程讨论
export function switchDiscuss(courseId, allowDiscuss) {
    return request({
        url: `/course/topic/switch/${courseId}`,
        method: 'put',
        params: { allowDiscuss }
    })
}