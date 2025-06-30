import request from './request'

/**
 * 发送智能对话请求
 * data: { content: string }
 * 对应后端接口: POST /api/message/query
 * 后端用 @RequestParam("content")
 */
export function sendChatQuery(userId, content) {
    const params = new URLSearchParams()
    params.append('userId', userId)
    params.append('content', content)
    return request.post('/message/query', params)
}

/**
 * 获取历史聊天记录
 * 对应后端接口: GET /api/message/history
 */
export function getChatHistory() {
    return request.get('/message/history')
}

/**
 * 清空聊天记录
 * 对应后端接口: POST /api/message/clear
 */
export function clearChatHistory() {
    return request.post('/message/clear')
}