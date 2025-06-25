import request from './request'

/**
 * 根据统计项名称获取统计数据
 * @param {string} statName
 * @returns {Promise}
 */
export function getStatisticByName(statName) {
  return request.get('/statistic/findByName', { params: { statName } })
} 