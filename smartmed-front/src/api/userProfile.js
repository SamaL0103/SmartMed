import request from './request'

// 获取当前用户资料
export function getUserProfile() {
  return request.get('/userProfile/info')
}

// 退出登录
export function logout() {
  return request.post('/login/logout')
}

// 更新当前用户资料
export function updateUserProfile(data) {
  const params = new URLSearchParams()
  Object.keys(data).forEach(key => params.append(key, data[key]))
  return request.post('/userProfile/update', params)
}

// 修改密码
export function updatePassword(data) {
  const params = new URLSearchParams()
  Object.keys(data).forEach(key => params.append(key, data[key]))
  return request.post('/userProfile/updatePassword', params)
}

// 上传头像
export function uploadAvatar(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/userProfile/uploadAvatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}