<template>
  <el-container class="acceptance-test-page">
    <el-main>
      <el-row :gutter="20">
        <el-col :span="16">
          <el-card class="test-selection">
            <h2 class="title">验收测试</h2>

            <el-form :model="form" label-width="120px">
              <el-form-item label="选择测试设备">
                <el-radio-group v-model="form.testDevice">
                  <el-radio 
                    v-for="option in deviceOptions" 
                    :key="option.value" 
                    :label="option.value"
                  >
                    {{ option.label }}
                  </el-radio>
                </el-radio-group>
              </el-form-item>

              <el-form-item label="选择测试模块">
                <el-button 
                  size="small" 
                  @click="toggleAllModules" 
                  style="margin-bottom: 10px;"
                  :disabled="availableModules.length === 0"
                >
                  {{ allSelected ? '取消全选' : '全选' }}
                </el-button>
                <el-checkbox-group v-model="form.selectedModules">
                  <el-checkbox 
                    v-for="module in availableModules" 
                    :key="module.id" 
                    :label="module.id"
                  >
                    {{ module.name }}
                  </el-checkbox>
                </el-checkbox-group>
              </el-form-item>

              <el-form-item>
                <el-button type="primary" @click="executeTest" :loading="isLoading" :disabled="!hasSelectedModules">
                  执行测试
                </el-button>
                <el-button @click="reset" :disabled="isLoading">重置</el-button>
              </el-form-item>
            </el-form>
          </el-card>

          <!-- Add new self-check card -->
          <el-card class="self-check" style="margin-top: 20px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
              <h3>下位机自检</h3>
              <el-button type="primary" size="small" @click="showIPSettingDialog">设置IP地址</el-button>
            </div>
            <el-form :inline="true" class="demo-form-inline">
              <el-form-item label="选择设备">
                <el-select v-model="selectedDevice" placeholder="请选择设备">
                  <el-option label="全部设备" value="all"></el-option>
                  <el-option
                    v-for="device in devices"
                    :key="device.id"
                    :label="device.name"
                    :value="device.id">
                  </el-option>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="performSelfCheck" :loading="isSelfChecking">
                  执行自检
                </el-button>
              </el-form-item>
            </el-form>
            <el-table :data="selfCheckResults" style="width: 100%">
              <el-table-column prop="id" label="下位机ID" min-width="120"></el-table-column>
              <el-table-column prop="status" label="状态" min-width="100">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === 'online' ? 'success' : 'danger'">
                    {{ scope.row.status === 'online' ? '在线' : '离线' }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="info" label="板卡信息" min-width="180">
                <template slot-scope="scope">
                  <el-popover trigger="hover" placement="top">
                    <div>
                      <p>PCI设备列表:</p>
                      <el-collapse>
                        <el-collapse-item v-for="device in scope.row.info.pciDevices" :key="device.address">
                          <template slot="title">
                            <span style="margin-right: 10px">{{ device.address }}</span>
                            <el-tag size="small">{{ device.type }}</el-tag>
                          </template>
                          <p>{{ device.details }}</p>
                        </el-collapse-item>
                      </el-collapse>
                    </div>
                    <div slot="reference" class="name-wrapper">
                      <el-tag size="medium">查看详情</el-tag>
                    </div>
                  </el-popover>
                </template>
              </el-table-column>
            </el-table>
          </el-card>

          <el-card class="test-history" style="margin-top: 20px;">
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 15px;">
              <h3>测试历史</h3>
              <el-button type="danger" size="small" @click="clearHistory" :disabled="testHistory.length === 0">
                清空历史
              </el-button>
            </div>
            <el-table :data="testHistory" style="width: 100%">
              <el-table-column prop="date" label="日期" width="180"></el-table-column>
              <el-table-column prop="modules" label="测试模块"></el-table-column>
              <el-table-column prop="status" label="状态">
                <template slot-scope="scope">
                  <el-tag :type="scope.row.status === '成功' ? 'success' : 'warning'">
                    {{ scope.row.status }}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column label="操作" width="100">
                <template slot-scope="scope">
                  <el-button
                    size="mini"
                    type="primary"
                    icon="el-icon-download"
                    circle
                    @click="downloadHistoryReport(scope.row)"
                    :disabled="!scope.row.report"
                  ></el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>

        <el-col :span="8">
          <el-card class="test-statistics">
            <h3>测试统计</h3>
            <el-row>
              <el-col :span="12">
                <div id="testChart" style="width: 100%; height: 200px;"></div>
              </el-col>
              <el-col :span="12">
                <div class="quick-info">
                  <p><strong>总测试次数：</strong>{{ totalTests }}</p>
                  <p><strong>成功率：</strong>{{ successRate }}%</p>
                  <p><strong>最后测试时间：</strong></p>
                  <p>{{ lastTestTime }}</p>
                </div>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </el-main>

    <el-dialog title="测试报告" :visible.sync="dialogVisible" width="80%">
      <pre v-if="testReportContent" class="test-report">{{ testReportContent }}</pre>
      <el-empty v-else description="暂无测试报告"></el-empty>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-dropdown @command="handleDownload" style="margin-left: 10px;">
          <el-button type="primary" :disabled="!testReportContent">
            下载报告<i class="el-icon-arrow-down el-icon--right"></i>
          </el-button>
          <el-dropdown-menu slot="dropdown">
            <el-dropdown-item command="txt">TXT格式</el-dropdown-item>
            <el-dropdown-item command="json">JSON格式</el-dropdown-item>
            <el-dropdown-item command="docx">DOCX格式</el-dropdown-item>
          </el-dropdown-menu>
        </el-dropdown>
      </span>
    </el-dialog>

    <el-dialog
      title="自检进度"
      :visible.sync="showProgressDialog"
      :close-on-click-modal="false"
      :show-close="false"
      width="30%">
      <el-progress :percentage="checkProgress" :format="progressFormat"></el-progress>
    </el-dialog>

    <!-- IP设置对话框 -->
    <el-dialog title="设置IP地址" :visible.sync="ipSettingDialogVisible" width="30%">
      <el-form :model="ipSettings" label-width="100px">
        <el-form-item v-for="(ip, device) in ipSettings" :key="device" :label="device">
          <el-input v-model="ipSettings[device]" placeholder="请输入IP地址"></el-input>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="ipSettingDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveIPSettings">确定</el-button>
      </span>
    </el-dialog>
  </el-container>
</template>

<script>
import axios from 'axios';
import * as echarts from 'echarts';

export default {
  name: 'AcceptanceTest',
  data() {
    return {
      deviceOptions: [
        { value: 'local', label: '本机' },
        { value: 'heterogeneous1', label: '通用异构单元1' },
        { value: 'heterogeneous2', label: '通用异构单元2' },
        { value: 'remote1', label: '远程连接单元1' },
        { value: 'remote2', label: '远程连接单元2' }
      ],
      modules: {
        local: [
          { id: 'cpu', name: 'CPU性能测试' },
          { id: 'memory', name: '内存读写测试' },
          { id: 'disk', name: '磁盘IO测试' },
          { id: 'network', name: '网络性能测试' }
        ],
        heterogeneous1: [
          { id: 'pcie', name: '66Ak测试' },
          { id: 'fpga', name: '串口功能测试' },
          { id: 'ddr', name: 'DDR带宽测试' },
          { id: 'network', name: '25G以太网测试' }
        ],
        heterogeneous2: [
          { id: 'pcie', name: 'PCIe通信测试' },
          { id: 'gpu', name: 'GPU计算测试' },
          { id: 'ddr', name: 'DDR带宽测试' },
          { id: 'network', name: '25G以太网测试' }
        ],
        remote1: [
          { id: 'ad', name: 'AD采集测试' },
          { id: 'da', name: 'DA输出测试' },
          { id: 'dio', name: '数字IO测试' },
          { id: 'uart', name: '串口通信测试' }
        ],
        remote2: [
          { id: 'ad', name: 'AD采集测试' },
          { id: 'da', name: 'DA输出测试' },
          { id: 'can', name: 'CAN总线测试' },
          { id: 'rs485', name: 'RS485通信测试' }
        ]
      },
      form: {
        selectedModules: [],
        testDevice: 'local' // 默认选择本机
      },
      isLoading: false,
      testReport: {
        report: ''
      },
      dialogVisible: false,
      testHistory: [],
      totalTests: 0,
      successRate: 0,
      lastTestTime: '',
      chart: null,
      selfCheckResults: [],
      isSelfChecking: false,
      selectedDevice: 'all',
      devices: [
        { id: 'device1', name: '设备1' },
        { id: 'device2', name: '设备2' },
        { id: 'device3', name: '设备3' },
        { id: 'device4', name: '设备4' },
      ],
      checkProgress: 0,
      showProgressDialog: false,
      ipSettingDialogVisible: false,
      ipSettings: {
        device1: '',
        device2: '',
        device3: '',
        device4: ''
      }
    }
  },
  computed: {
    hasSelectedModules() {
      return this.form.selectedModules.length > 0;
    },
    testReportContent() {
      return this.testReport && this.testReport.report ? this.testReport.report : '';
    },
    availableModules() {
      return this.modules[this.form.testDevice] || [];
    },
    allSelected() {
      return this.availableModules.length > 0 && 
             this.form.selectedModules.length === this.availableModules.length;
    }
  },
  watch: {
    'form.testDevice': {
      handler(newDevice) {
        this.form.selectedModules = []; // 切换设备时清空已选择的模块
      }
    }
  },
  created() {
    this.loadHistoryAndStatistics();
    this.getCurrentIPSettings(); // 在组件创建时获取当前IP设置
  },
  mounted() {
    this.initTestChart();
    window.addEventListener('resize', this.handleResize);
  },
  beforeDestroy() {
    this.saveHistoryAndStatistics();
    window.removeEventListener('resize', this.handleResize);
  },
  methods: {
    toggleAllModules() {
      if (this.allSelected) {
        this.form.selectedModules = [];
      } else {
        this.form.selectedModules = this.availableModules.map(module => module.id);
      }
    },
    async executeTest() {
      if (!this.hasSelectedModules) {
        this.$message.warning('请至少选择一个测试模块');
        return;
      }

      this.isLoading = true;
      try {
        const apiEndpoint = this.getApiEndpoint();
        
        const response = await axios.post(apiEndpoint, {
          modules: this.form.selectedModules,
          device: this.form.testDevice
        });

        this.handleTestResponse(response);
      } catch (error) {
        console.error('测试执行失败:', error);
        this.$message.error('测试执行失败：' + (error.response?.data?.message || error.message));
      } finally {
        this.isLoading = false;
      }
    },
    reset() {
      this.form.selectedModules = [];
      this.testReport = { report: '' };
      this.$message.info('已重置选择');
    },
    handleDownload(format) {
      this.downloadReport(this.testReportContent, format);
    },
    downloadReport(content, format) {
      if (!content) {
        this.$message.warning('报告内容为空，无法下载');
        return;
      }
      let mimeType, fileExtension;
      switch (format) {
        case 'json':
          content = JSON.stringify(JSON.parse(content), null, 2);
          mimeType = 'application/json';
          fileExtension = 'json';
          break;
        case 'docx':
          this.$message.warning('DOCX格式下载功能尚未实现');
          return;
        case 'txt':
        default:
          mimeType = 'text/plain';
          fileExtension = 'txt';
      }
      const blob = new Blob([content], { type: mimeType });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = `test_report_${new Date().toISOString().slice(0,19).replace(/:/g, '-')}.${fileExtension}`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(link.href);
      this.$message.success(`报告已下载为${fileExtension.toUpperCase()}格式`);
    },
    downloadHistoryReport(historyItem) {
      this.downloadReport(historyItem.report, 'txt');
    },
    initTestChart() {
      this.chart = echarts.init(document.getElementById('testChart'));
      this.updateChart();
    },
    updateChart() {
      if (!this.chart) {
        return;
      }
      const successValue = (this.successRate / 100) * this.totalTests;
      const failureValue = this.totalTests - successValue;
      const option = {
        title: {
          text: '测试结果',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        series: [
          {
            name: '测试结果',
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: [
              { value: failureValue, name: '失败' },
              { value: successValue, name: '成功' }
            ]
          }
        ]
      };
      this.chart.setOption(option);
    },
    updateTestHistory(isSuccess) {
      console.log('Updating test history. Is successful:', isSuccess);
      const newHistoryItem = {
        date: new Date().toLocaleString(),
        modules: this.form.selectedModules.join(', '),
        status: isSuccess ? '成功' : '未生成报告',
        report: this.testReportContent
      };
      console.log('New history item:', newHistoryItem);
      this.testHistory.unshift(newHistoryItem);
      this.testHistory = this.testHistory.slice(0, 10); // 保留最近10条记录
      console.log('Updated test history:', this.testHistory);
      this.saveHistoryAndStatistics();
    },
    updateTestStatistics(isSuccess) {
      this.totalTests++;
      const successfulTests = isSuccess ?
        (this.successRate / 100 * (this.totalTests - 1) + 1) :
        (this.successRate / 100 * (this.totalTests - 1));
      this.successRate = (successfulTests / this.totalTests * 100).toFixed(2);
      this.lastTestTime = new Date().toLocaleString();
      console.log('Updated statistics:', {
        totalTests: this.totalTests,
        successRate: this.successRate,
        lastTestTime: this.lastTestTime
      });
      this.updateChart();
      this.saveHistoryAndStatistics();
    },
    handleResize() {
      if (this.chart) {
        this.chart.resize();
      }
    },
    clearHistory() {
      this.$confirm('确定要清空测试历史和统计数据吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.testHistory = [];
        this.totalTests = 0;
        this.successRate = 0;
        this.lastTestTime = '';
        this.updateChart();
        this.saveHistoryAndStatistics();
        this.$message.success('测试历史和统计数据已清空');
      }).catch(() => {
        this.$message.info('已取消清空操作');
      });
    },
    loadHistoryAndStatistics() {
      const savedHistory = localStorage.getItem('testHistory');
      if (savedHistory) {
        this.testHistory = JSON.parse(savedHistory);
      }

      const savedStatistics = localStorage.getItem('testStatistics');
      if (savedStatistics) {
        const { totalTests, successRate, lastTestTime } = JSON.parse(savedStatistics);
        this.totalTests = parseInt(totalTests, 10) || 0;
        this.successRate = parseFloat(successRate) || 0;
        this.lastTestTime = lastTestTime || '';
      }
    },
    saveHistoryAndStatistics() {
      localStorage.setItem('testHistory', JSON.stringify(this.testHistory));
      localStorage.setItem('testStatistics', JSON.stringify({
        totalTests: this.totalTests,
        successRate: this.successRate,
        lastTestTime: this.lastTestTime
      }));
    },
    async performSelfCheck() {
      this.showProgressDialog = true;
      this.checkProgress = 0;
      
      const simulateProgress = () => {
        const interval = setInterval(() => {
          if (this.checkProgress < 90) {
            this.checkProgress += 10;
          } else {
            clearInterval(interval);
          }
        }, 300);
        return interval;
      };

      const progressInterval = simulateProgress();

      try {
        const response = await axios.get('/api/self-check', {
          params: { deviceId: this.selectedDevice }
        });
        console.log('Self-check response:', response.data);
        this.selfCheckResults = response.data;
        
        clearInterval(progressInterval);
        this.checkProgress = 100;
        
        setTimeout(() => {
          this.showProgressDialog = false;
          if (this.selfCheckResults.length === 0) {
            this.$message.warning('没有找到设备或所有设备离线');
          } else {
            this.$message.success('自检完成');
          }
        }, 500);
      } catch (error) {
        clearInterval(progressInterval);
        this.showProgressDialog = false;
        console.error('自检失败:', error);
        this.$message.error('自检失败：' + (error.response?.data?.message || error.message));
      }
    },
    progressFormat(percentage) {
      return percentage === 100 ? '完成' : `${percentage}%`;
    },
    showIPSettingDialog() {
      this.ipSettingDialogVisible = true;
    },
    async saveIPSettings() {
      try {
        const response = await axios.post('/api/update-ip-settings', this.ipSettings);
        if (response.data.success) {
          this.$message.success('IP地址设置已更新');
          this.ipSettingDialogVisible = false;
        } else {
          this.$message.error('更新IP地址设置失败');
        }
      } catch (error) {
        console.error('更新IP地址设置时出错:', error);
        this.$message.error('更新IP地址设置失败: ' + error.message);
      }
    },
    async getCurrentIPSettings() {
      try {
        const response = await axios.get('/api/get-ip-settings');
        this.ipSettings = response.data;
      } catch (error) {
        console.error('获取IP设置时出错:', error);
        this.$message.error('获取IP设置失败: ' + error.message);
      }
    },
    getApiEndpoint() {
      const endpoints = {
        local: '/api/execute-test',
        heterogeneous1: '/api/execute-test-heterogeneous',
        heterogeneous2: '/api/execute-test-heterogeneous',
        remote1: '/api/execute-test-remote',
        remote2: '/api/execute-test-remote'
      };
      return endpoints[this.form.testDevice] || '/api/execute-test';
    },
    handleTestResponse(response) {
      console.log('Backend response:', response.data);
      this.testReport = response.data;

      const isSuccess = this.testReport.report && this.testReport.report.trim() !== '';
      
      this.updateTestHistory(isSuccess);
      this.updateTestStatistics(isSuccess);

      if (isSuccess) {
        this.$message.success('测试执行成功');
      } else {
        this.$message.warning('��试执行完成，但未生成报告');
      }
      this.dialogVisible = true;
    }
  }
}
</script>

<style scoped>
.acceptance-test-page {
  padding: 20px;
}
.title {
  text-align: center;
  margin-bottom: 20px;
  color: #409EFF;
  font-size: 24px;
}
.test-report {
  white-space: pre-wrap;
  word-wrap: break-word;
  font-family: monospace;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
  max-height: 400px;
  overflow-y: auto;
}
.el-checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
  margin-top: 10px;
}
.el-checkbox {
  margin-right: 0;
  margin-bottom: 0;
}
.quick-info {
  padding: 10px;
}
.quick-info p {
  margin: 5px 0;
}
.self-check {
  margin-top: 20px;
}

.self-check h3 {
  margin-bottom: 15px;
}

.demo-form-inline {
  margin-bottom: 20px;
}

.el-table {
  width: 100%;
  table-layout: fixed;
}

.el-table .cell {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.el-table th {
  background-color: #f5f7fa;
  color: #606266;
  font-weight: bold;
  text-align: center;
}

.el-table td {
  text-align: center;
}

.el-tag {
  margin: 0 auto;
  display: block;
  width: fit-content;
}

.el-radio-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
}
</style>
