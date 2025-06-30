import request from './request'

// 获取病史
export function getHistories(userId) {
  return request.get(`/users/${userId}/histories`)
}

// 新增病史
export function addHistory(userId, data) {
  return request.post(`/users/${userId}/histories`, data)
}

// 更新病史
export function updateHistory(userId, id, data) {
  return request.put(`/users/${userId}/histories/${id}`, data)
}

// 删除病史
export function deleteHistory(userId, id) {
  return request.delete(`/users/${userId}/histories/${id}`)
}