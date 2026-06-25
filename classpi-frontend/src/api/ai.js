import request from '@/utils/request'

export function aiEvaluate(data) {
  return request({
    url: '/ai/evaluate',
    method: 'post',
    data
  })
}

export function getHomeworkList(params) {
  return request({
    url: '/homework/list',
    method: 'get',
    params
  })
}

export function aiChat(data) {
  return request({
    url: '/ai/chat',
    method: 'post',
    data
  })
}

export function getChatHistory(chatId) {
  return request({
    url: `/ai/chat/${chatId}`,
    method: 'get'
  })
}

export function getUserChats(userId) {
  return request({
    url: `/ai/chats/${userId}`,
    method: 'get'
  })
}

export function deleteChat(chatId) {
  return request({
    url: `/ai/chat/${chatId}`,
    method: 'delete'
  })
}