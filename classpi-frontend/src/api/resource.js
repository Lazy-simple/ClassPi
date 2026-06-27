import request from '@/utils/request'

// 获取课程全部资源树
export function getResourceTree(courseId, page = 1, pageSize = 10) {
    // 参数校验 - 如果 courseId 无效，使用默认值 1
    let id = Number(courseId)
    if (isNaN(id) || id <= 0) {
        console.warn('courseId 无效，使用默认值 1', courseId)
        id = 1
    }

    console.log('========== 发起请求 ==========')
    console.log('courseId:', id)
    console.log('请求URL:', `/resource/course/${id}`)
    console.log('完整地址:', `http://localhost:8080/resource/course/${id}?page=${page}&pageSize=${pageSize}`)

    return request({
        url: `/resource/course/${id}`,
        method: 'get',
        params: { page, pageSize }
    })
}

// 教师新建文件夹
export function createFolder(params) {
    return request({
        url: '/resource/folder',
        method: 'post',
        params: params
    })
}

// 教师新增外链资源
export function addLink(data) {
    return request({
        url: '/resource/link',
        method: 'post',
        data: data
    })
}

// 教师移动资源/文件夹
export function moveResource(id, targetFolderId) {
    return request({
        url: `/resource/${id}/move`,
        method: 'put',
        params: { targetFolderId }
    })
}

// 教师删除资源
export function deleteResource(resId) {
    return request({
        url: `/resource/${resId}`,
        method: 'delete'
    })
}

// 下载附件文件 - 修复路径
export function downloadResource(resId) {
    window.open(`http://localhost:8080/resource/download/${resId}`)
}

// 上传附件后新增资源
export function uploadResource(data) {
    return request({
        url: '/course/resource/upload',
        method: 'post',
        data
    })
}