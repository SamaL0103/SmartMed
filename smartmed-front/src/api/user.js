import request from './request'

/**
 * 用户注册
 * data: { userAccount, userName, userPwd, userTel, userAge, userSex }
 * 对应后端接口: POST /api/login/register
 */
export function register(data) {
  const params = new URLSearchParams()
  params.append('userName', data.userName)
  params.append('userPwd', data.userPwd)
  params.append('userTel', data.userTel)
  params.append('userAge', data.userAge)
  params.append('userSex', data.userSex)
  return request.post('/login/register', params)
}

/**
 * 用户登录
 * data: { userAccount, userPwd }
 * 对应后端接口: POST /api/login/login
 */
export function login(data) {
  const params = new URLSearchParams()
  Object.keys(data).forEach(key => params.append(key, data[key]))
  return request.post('/login/login', params)
}

/**
 * 管理员获取所有用户
 * 对应后端接口: GET /api/user/list
 */
export function getUserList() {
  return request.get('/user/list')
}

/**
 * 管理员新增用户
 * data: { username, password, phone, age, gender, isAdmin }
 * 对应后端接口: POST /api/user/add
 */
export function addUser(data) {
  return request.post('/user/add', data)
}

/**
 * 管理员更新用户
 * data: { userId, username, ... }
 * 对应后端接口: POST /api/user/update
 */
export function updateUser(data) {
  return request.post('/user/update', data)
}

/**
 * 管理员删除用户
 * userId: number
 * 对应后端接口: POST /api/user/delete
 */
export function deleteUser(userId) {
  const params = new URLSearchParams()
  params.append('userId', userId)
  return request.post('/user/delete', params)
}