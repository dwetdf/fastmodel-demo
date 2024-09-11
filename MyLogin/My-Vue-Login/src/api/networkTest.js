import request from '@/utils/request'

export default {
  throughputTest() {
    return request({
      url: '/network/throughput',
      method: 'get'
    })
  },
  latencyTest() {
    return request({
      url: '/network/latency',
      method: 'get'
    })
  },
  packetLossTest() {
    return request({
      url: '/network/packetloss',
      method: 'get'
    })
  },
  saveTestResults(results) {
    return request({
      url: '/network/save-results',
      method: 'post',
      data: results
    })
  }
}
