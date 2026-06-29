import request from '@/utils/request'

// 获取课程话题列表
export function getTopicList(courseId) {
    return request({
        url: `/topic/course/${courseId}`,
        method: 'get'
    })
}

// 发布话题
export function publishTopic(data) {
    return request({
        url: '/topic/create',
        method: 'post',
        data: {
            courseId: Number(data.courseId),
            courseNo: String(data.courseNo || ''),
            title: String(data.title || ''),
            content: String(data.content || ''),
            authorId: String(data.authorId || ''),  // ✅ 确保传了
            authorName: String(data.authorName || ''),
            authorType: Number(data.authorType || 2),
            isAnonymous: Number(data.isAnonymous || 0)
        }
    })
}

// 编辑话题
export function editTopic(data) {
    return request({
        url: `/topic/${data.id}`,
        method: 'put',
        params: {
            title: String(data.title || ''),
            content: String(data.content || ''),
            authorId: String(data.authorId || ''),
            identity: String(data.identity || 'student')
        }
    })
}

// 删除话题
export function deleteTopic(topicId, authorId, identity) {
    return request({
        url: `/topic/${topicId}`,
        method: 'delete',
        params: {
            authorId: String(authorId || ''),
            identity: String(identity || 'student')
        }
    })
}

// 置顶/取消置顶
export function setTopicTop(topicId, isTop) {
    const url = isTop === 1 ? `/topic/${topicId}/top` : `/topic/${topicId}/cancel-top`
    return request({
        url: url,
        method: 'put'
    })
}

// 发布评论
export function addComment(data) {
    const requestData = {
        topicId: Number(data.topicId),
        content: String(data.content || ''),
        authorId: String(data.authorId || ''),
        authorName: String(data.authorName || ''),
        authorType: Number(data.authorType || 2),
        isAnonymous: Number(data.isAnonymous || 0)
    }

    console.log('========== 发表评论 ==========')
    console.log('请求数据:', requestData)

    return request({
        url: `/topic/${data.topicId}/reply`,
        method: 'post',
        data: requestData
    })
}

// 获取话题评论
export function getCommentList(topicId) {
    return request({
        url: `/topic/${topicId}/replies`,
        method: 'get'
    })
}

// 开关课程讨论
export function switchDiscuss(courseId, allowDiscuss) {
    return request({
        url: `/topic/switch/${courseId}`,
        method: 'put',
        params: { allowDiscuss }
    })
}

// 禁言话题
export function disableComment(topicId) {
    return request({
        url: `/topic/${topicId}/disable-comment`,
        method: 'put'
    })
}

// 解禁话题
export function enableComment(topicId) {
    return request({
        url: `/topic/${topicId}/enable-comment`,
        method: 'put'
    })
}