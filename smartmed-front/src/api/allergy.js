import request from './request'

// 获取过敏史
export function getAllergies(userId) {
  return request.get(`/users/${userId}/allergies`)
}

// 新增过敏史
export function addAllergy(userId, data) {
  return request.post(`/users/${userId}/allergies`, data)
}

// 更新过敏史
export function updateAllergy(userId, id, data) {
  return request.put(`/users/${userId}/allergies/${id}`, data)
}

// 删除过敏史
export function deleteAllergy(userId, id) {
  return request.delete(`/users/${userId}/allergies/${id}`)
}