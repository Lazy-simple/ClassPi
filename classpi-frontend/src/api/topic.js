import request from '@/utils/request'

// 获取课程话题列表
export function getTopicList(courseId) {
    return request({
        url: `/topic/course/${courseId}`,  // 改这里
        method: 'get'
    })
}

// 发布话题
export function publishTopic(data) {
    return request({
        url: '/topic/create',  // 改这里
        method: 'post',
        data
    })
}

// 编辑话题
export function editTopic(data) {
    return request({
        url: `/topic/${data.id}`,  // 改这里
        method: 'put',
        params: {
            title: data.title,
            content: data.content,
            authorId: data.authorId,
            identity: data.identity
        }
    })
}

// 删除话题
export function deleteTopic(topicId, authorId, identity) {
    return request({
        url: `/topic/${topicId}`,  // 改这里
        method: 'delete',
        params: { authorId, identity }
    })
}

// 置顶
export function setTopicTop(topicId, isTop) {
    const url = isTop === 1 ? `/topic/${topicId}/top` : `/topic/${topicId}/cancel-top`
    return request({
        url: url,  // 改这里
        method: 'put'
    })
}

// 发布评论
export function addComment(data) {
    return request({
        url: `/topic/${data.topicId}/reply`,  // 改这里
        method: 'post',
        data: {
            content: data.content,
            authorId: data.authorId,
            authorName: data.authorName,
            authorType: data.authorType,
            isAnonymous: data.isAnonymous
        }
    })
}

// 获取话题评论
export function getCommentList(topicId) {
    return request({
        url: `/topic/${topicId}/replies`,  // 改这里
        method: 'get'
    })
}

// 开关课程讨论
export function switchDiscuss(courseId, allowDiscuss) {
    return request({
        url: `/topic/switch/${courseId}`,  // 改这里
        method: 'put',
        params: { allowDiscuss }
    })
}