import request from './request'

/**
 * 获取疾病列表
 * params: { illnessName: string, kind: string }
 * 对应后端接口: GET /api/illness/findIllness
 * 后端用 @RequestParam("illnessName")、@RequestParam("kind")
 */
export function getDiseaseList(params) {
  return request.get('/illness/findIllness', { params })
}

/**
 * 获取疾病详情
 * id: number
 * 对应后端接口: GET /api/illness/findIllnessOne?id=xxx
 * 后端用 @RequestParam("id")
 */
export function getDiseaseDetail(id) {
  return request.get('/illness/findIllnessOne', { params: { id } })
}

/**
 * 获取疾病相关药品
 * diseaseId: number
 * 对应后端接口: GET /api/illness/relatedMedicines?id=xxx
 * 后端用 @RequestParam("id")
 */
export function getRelatedMedicines(diseaseId) {
  return request.get('/illness/relatedMedicines', { params: { id: diseaseId } })
}

/**
 * 管理员获取所有疾病
 * 对应后端接口: GET /api/illness/manage
 */
export function getAllDiseases() {
  return request.get('/illness/manage')
}

/**
 * 管理员新增/编辑疾病
 * data: { id?, illnessName, includeReason, illnessSymptom, kindId }
 * 对应后端接口: POST /api/illness/save
 */
export function saveDisease(data) {
  const params = new URLSearchParams()
  Object.keys(data).forEach(key => params.append(key, data[key]))
  return request.post('/illness/save', params)
}

/**
 * 管理员删除疾病
 * id: number
 * 对应后端接口: POST /api/illness/delete
 */
export function deleteDisease(id) {
  const params = new URLSearchParams()
  params.append('id', id)
  return request.post('/illness/delete', params)
}