import axios from 'axios'
import { ElMessage } from 'element-plus'

const service = axios.create({
  baseURL: '/api',
  timeout: 20000,
  withCredentials: true // 允许携带 cookie/session
})

// 响应拦截器：对所有响应做统一处理
service.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 'SUCCESS') {
      // 如果后端返回的code是SUCCESS，说明请求成功
      // 直接把data部分返回给页面用
      return res.data
    } else {
      // 如果不是SUCCESS，说明有错误
      // 用element-plus的ElMessage弹窗显示错误信息
      ElMessage.error(res.message || '请求失败')
      // 返回一个失败的Promise，页面可以用catch捕获
      return Promise.reject(res)
    }
  },
  error => {
    // 如果请求本身出错（比如网络断了），也弹窗提示
    ElMessage.error(error.message || '网络错误')
    return Promise.reject(error)
  }
)


export default service