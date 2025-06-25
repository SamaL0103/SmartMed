import request from './request'

/**
 * 获取药品列表
 * params: { name: string, efficacy: string }
 * 对应后端接口: GET /api/medicine/findMedicines
 * 后端用 @RequestParam("name")、@RequestParam("efficacy")
 */
export function getMedicineList(params) {
  return request.get('/medicine/findMedicines', { params })
}

/**
 * 获取药品详情
 * id: number
 * 对应后端接口: GET /api/medicine/findMedicineOne?id=xxx
 * 后端用 @RequestParam("id")
 */
export function getMedicineDetail(id) {
  return request.get('/medicine/findMedicineOne', { params: { id } })
}

/**
 * 新增或更新药品（管理员用）
 * data: { id?, name, category, efficacy, usageMethod, contraindications, sideEffects }
 * 对应后端接口: POST /api/medicine/save
 * 后端用 @RequestParam 逐个接收字段
 */
export function saveMedicine(data) {
  const params = new URLSearchParams()
  Object.keys(data).forEach(key => params.append(key, data[key]))
  return request.post('/medicine/save', params)
}

/**
 * 删除药品（管理员用）
 * id: number
 * 对应后端接口: POST /api/medicine/delete
 * 后端用 @RequestParam("id")
 */
export function deleteMedicine(id) {
  const params = new URLSearchParams()
  params.append('id', id)
  return request.post('/medicine/delete', params)
}