import request from './request'

export function submitFeedback(userId, content) {
    const params = new URLSearchParams()
    params.append('userId', userId)
    params.append('content', content)
    return request.post('/feedback/submit', params)
}

export function getUserFeedbacks(userId) {
    return request.get('/feedback/userFeedbacks', { params: { userId } })
}

/**
 * 管理员获取所有反馈
 * 对应后端接口: GET /api/feedback/all
 */
export function getAllFeedback() {
  return request.get('/feedback/all')
}

/**
 * 管理员按状态获取反馈
 * status: 'Pending' | 'Resolved'
 * 对应后端接口: GET /api/feedback/statusFeedbacks
 */
export function getStatusFeedbacks(status) {
  return request.get('/feedback/statusFeedbacks', { params: { status } })
}

/**
 * 管理员修改反馈状态
 * id: number, status: 'Pending' | 'Resolved'
 * 对应后端接口: POST /api/feedback/updateStatus
 */
export function updateFeedbackStatus(id, status) {
    const params = new URLSearchParams()
    params.append('id', Number(id)) // 强制数字
    params.append('status', String(status).trim()) // 去空格
    return request.post('/feedback/updateStatus', params)
  }

/**
 * 管理员删除反馈
 * id: number
 * 对应后端接口: POST /api/feedback/delete
 */
export function deleteFeedback(id) {
  const params = new URLSearchParams()
  params.append('id', id)
  return request.post('/feedback/delete', params)
}

const updateStatus = async (row, newStatus) => {
  await updateFeedbackStatus(row.id, newStatus)
  // ...
}