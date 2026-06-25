import request from '@/utils/request'
// 获取课程全部资源树
export function getResourceTree(courseId) {
    return request({
        url: `/course/resource/list/${courseId}`,
        method: 'get'
    })
}
// 教师新建文件夹
export function createFolder(data) {
    return request({
        url: '/course/resource/folder',
        method: 'post',
        data
    })
}
// 教师新增外链资源
export function addLink(data) {
    return request({
        url: '/course/resource/link',
        method: 'post',
        data
    })
}
// 教师移动资源/文件夹
export function moveResource(data) {
    return request({
        url: '/course/resource/move',
        method: 'put',
        data
    })
}
// 教师删除资源
export function deleteResource(resId) {
    return request({
        url: `/course/resource/${resId}`,
        method: 'delete'
    })
}
// 下载附件文件
export function downloadResource(resId) {
    window.open(`/api/course/resource/download/${resId}`)
}
// 上传附件后新增资源
export function uploadResource(data) {
    return request({
        url: '/course/resource/upload',
        method: 'post',
        data
    })
}