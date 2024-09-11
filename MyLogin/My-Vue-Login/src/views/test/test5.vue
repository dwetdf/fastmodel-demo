<template>
  <div>
    <el-card class="test-card">
      <el-row>
        <el-col :span="8">
          <el-button type="primary" round @click="runThroughputTest">吞吐量测试</el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" round @click="runLatencyTest">延迟测试</el-button>
        </el-col>
        <el-col :span="8">
          <el-button type="primary" round @click="runPacketLossTest">丢包率测试</el-button>
        </el-col>
      </el-row>
    </el-card>
    
    <el-table :data="testResults" style="width: 100%">
      <el-table-column prop="testType" label="测试类型" width="180"></el-table-column>
      <el-table-column prop="result" label="测试结果" width="180"></el-table-column>
      <el-table-column prop="timestamp" label="测试时间"></el-table-column>
    </el-table>
    
    <el-button type="success" @click="saveResults">保存测试结果</el-button>
  </div>
</template>

<script>
import networkTestApi from '@/api/networkTest' // 假设我们有一个新的API模块用于网络测试

export default {
  data() {
    return {
      testResults: []
    }
  },
  methods: {
    async runThroughputTest() {
      try {
        const { data } = await networkTestApi.throughputTest()
        this.addTestResult('吞吐量测试', data + ' Mbps')
      } catch (error) {
        console.error('吞吐量测试失败:', error)
        this.$message.error('吞吐量测试失败')
      }
    },
    async runLatencyTest() {
      try {
        const { data } = await networkTestApi.latencyTest()
        this.addTestResult('延迟测试', data + ' ms')
      } catch (error) {
        console.error('延迟测试失败:', error)
        this.$message.error('延迟测试失败')
      }
    },
    async runPacketLossTest() {
      try {
        const { data } = await networkTestApi.packetLossTest()
        this.addTestResult('丢包率测试', data + '%')
      } catch (error) {
        console.error('丢包率测试失败:', error)
        this.$message.error('丢包率测试失败')
      }
    },
    addTestResult(testType, result) {
      this.testResults.push({
        testType,
        result,
        timestamp: new Date().toLocaleString()
      })
      this.$message.success(testType + '完成')
    },
    async saveResults() {
      try {
        await networkTestApi.saveTestResults(this.testResults)
        this.$message.success('测试结果已保存')
      } catch (error) {
        console.error('保存测试结果失败:', error)
        this.$message.error('保存测试结果失败')
      }
    }
  }
}
</script>

<style scoped>
.test-card {
  margin-bottom: 20px;
}
.el-button {
  margin-right: 10px;
}
</style>
