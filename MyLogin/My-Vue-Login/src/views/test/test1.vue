<template>
  <div class="test-container">
    <el-card id="select1">
      <el-row>
        <el-col :span="6">
          <h3>请选择信号处理算法</h3>
          <el-radio v-model="id1" label="1">FFT</el-radio>
          <el-radio v-model="id1" label="2">FIR</el-radio>
        </el-col>
        <el-col :span="13" align="right">
          <el-select v-model="value" placeholder="模式" style="width: 200px;">
            <el-option
              v-for="item in options"
              :key="item.value"
              :label="item.label"
              :value="item.value">
            </el-option>
          </el-select>
        </el-col>
        <el-col :span="3" align="right">
          <el-button type="primary" round @click="signaltest()">自适应测试</el-button>
        </el-col>
        <el-col :span="1" align="right" style="display: flex; justify-content: center; align-items: center;">
          <el-tag v-if="id1 === '1'" style="height: 40px; display: flex; justify-content: center; align-items: center; transition: none;">模块:{{ ida }}</el-tag>
          <el-tag v-if="id1 === '2'" style="height: 40px; display: flex; justify-content: center; align-items: center; transition: none;">模块:{{ idb }}</el-tag>
        </el-col>
      </el-row>
      <el-divider />
      <el-row>
        <el-col :span="4" align="left">
          <input v-show="id1 === '1'" type="file" accept=".txt" @change="handleFileChange1">
        </el-col>
        <el-col :span="4" align="left">
          <input v-show="id1 === '2'" type="file" accept=".txt" @change="handleFileChange2">
        </el-col>

        <el-col :span="20" align="right">
          <el-input v-model="id" placeholder="模块ID" clearable @input="handleInput" />
          <el-button type="primary" round icon="el-icon-s-promotion" @click="signaltestById()">发送测试</el-button>
        </el-col>
      </el-row>
      <el-divider />
      <el-row>
        <div>
          <div>
            <pre>{{ id1 === '1'? txt1:txt2 }}</pre>
          </div>
        </div>
        <el-col :span="15" align="left">
          <el-button v-show="id1 === '1'" type="primary" round icon="el-icon-document" :disabled="isButtonDisabled1" @click="getoutput1()">查看文件</el-button>
          <el-button v-show="id1 === '1'" type="primary" round icon="el-icon-document" :disabled="isButtonDisabled1" @click="deleteoutput1()">隐藏文件</el-button>
          <el-button v-show="id1 === '1'" type="success" round icon="el-icon-download" :disabled="isButtonDisabled1" @click="downloadFile('FFT')">下载文件</el-button>
        </el-col>
        <el-col :span="15" align="left">
          <el-button v-show="id1 === '2'" type="primary" round icon="el-icon-document" :disabled="isButtonDisabled2" @click="getoutput2()">查看文件</el-button>
          <el-button v-show="id1 === '2'" type="primary" round icon="el-icon-document" :disabled="isButtonDisabled2" @click="deleteoutput2()">隐藏文件</el-button>
          <el-button v-show="id1 === '2'" type="success" round icon="el-icon-download" :disabled="isButtonDisabled2" @click="downloadFile('FIR')">下载文件</el-button>
        </el-col>
      </el-row>
    </el-card>

    <el-card class="history-card">
      <div slot="header" class="card-header">
        <span>测试历史</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="refreshHistory">刷新</el-button>
      </div>
      <el-table :data="testHistory" style="width: 100%" :row-class-name="tableRowClassName">
        <el-table-column prop="id" label="测试ID" min-width="25%"></el-table-column>
        <el-table-column prop="status" label="状态" min-width="15%">
          <template slot-scope="scope">
            <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="module" label="测试模块" min-width="20%"></el-table-column>
        <el-table-column prop="algorithm" label="测试算法" min-width="20%"></el-table-column>
        <el-table-column label="操作" min-width="20%">
          <template slot-scope="scope">
            <el-button type="text" size="small" icon="el-icon-download" @click="downloadHistoryFile(scope.row)">下载</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-container">
        <el-pagination
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-sizes="[10, 20, 30, 50]"
          :page-size="pageSize"
          layout="total, sizes, prev, pager, next, jumper"
          :total="totalTests">
        </el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
import signaltest from '@/api/signaltest'

export default {
  data() {
    return {
      id: null,
      id1: '1',
      ida: null,
      idb: null,
      selectedFile1: null,
      selectedFile2: null,
      output1: null,
      output2: null,
      isButtonDisabled1: true,
      isButtonDisabled2: true,
      txt1: null,
      txt2: null,
      options: [{
        value: '1',
        label: 'CPU最优'
      }, {
        value: '2',
        label: '内存最优'
      }, {
        value: '3',
        label: '网络最优'
      }],
      value: '1',
      fits: ['fill'],
      testHistory: [],
      currentPage: 1,
      pageSize: 10,
      totalTests: 0,
    }
  },
  methods: {
    getoutput1() {
      console.log("getoutput1 called, output1:", this.output1);
      if (this.output1 && this.output1.fileContent) {
        console.log("File content found, type:", typeof this.output1.fileContent);
        this.txt1 = this.output1.fileContent;
        console.log("File content (first 100 chars):", this.txt1.substring(0, 100));
      } else if (this.output1 && typeof this.output1 === 'string') {
        console.log("String content found:", this.output1.substring(0, 100));
        this.txt1 = this.output1;
      } else {
        console.log("No file content available", this.output1);
        this.txt1 = "No content available";
      }
    },
    getoutput2() {
      console.log("getoutput2 called, output2:", this.output2);
      if (this.output2 && this.output2.fileContent) {
        console.log("File content found, type:", typeof this.output2.fileContent);
        // 直接使用文件内容，不进行Base64解码
        this.txt2 = this.output2.fileContent;
        console.log("File content (first 100 chars):", this.txt2.substring(0, 100));
      } else if (this.output2 && typeof this.output2 === 'string') {
        console.log("String content found:", this.output2.substring(0, 100));
        this.txt2 = this.output2;
      } else {
        console.log("No file content available", this.output2);
        this.txt2 = "No content available";
      }
    },
    deleteoutput1() {
      this.txt1 = null
    },
    deleteoutput2() {
      this.txt2 = null
    },
    handleInput() {
      const value = parseInt(this.id)
      if (isNaN(value)) {
        // 如果不是整数，则将输入的值设置为null
        this.id = null
      } else {
        // 如果是整数，则更新输入的值为整数
        this.id = value
      }
    },
    handleFileChange1(event) {
      const file = event.target.files[0]
      if (!file) {
        this.selectedFile1 = null
        return // 用户取消选择文件时，file置为null，直接返回
      }
      // 检查文件类型
      if (!file.name.endsWith('.txt')) {
        this.$message({ message: '请选择一个.txt文件', type: 'error', center: true })
        // 清空文件输入框
        event.target.value = ''
        this.selectedFile1 = null
        return
      }
      // 可以在这做其文件校验或处理
      this.selectedFile1 = file
    },
    handleFileChange2(event) {
      const file = event.target.files[0]
      if (!file) {
        this.selectedFile2 = null
        return // 用户取消选择文件时，file置为null，直接返回
      }
      // 检查文件类型
      if (!file.name.endsWith('.txt')) {
        this.$message({ message: '请选择一个.txt文件', type: 'error', center: true })
        // 清空文件输入框
        event.target.value = ''
        this.selectedFile2 = null
        return
      }
      // 可以在这里做其他文件校验或处理
      this.selectedFile2 = file
    },
    async signaltestById() {
      if (this.id1 === '1') {
        this.txt1 = null
        if (this.selectedFile1 === null) {
          this.$message({ message: '文件不能为空', type: 'error', center: true })
          this.output1 = null
          this.ida = null
          this.isButtonDisabled1 = true
          return null
        }
        const { data } = await signaltest.FFTById(this.id, this.selectedFile1)
        if (data !== null) {
          this.$message({ message: '处理成功', type: 'success', center: true })
          this.isButtonDisabled1 = false
        } else {
          this.$message({ message: '请检查模块ID或网络连接', type: 'warning', center: true })
          this.isButtonDisabled2 = true
        }
        this.output2 = data
        this.ida = null
        this.addTestHistory('FFT', this.id)
      }
      if (this.id1 === '2') {
        this.txt2 = null
        if (this.selectedFile2 === null) {
          this.$message({ message: '文件不能为空', type: 'error', center: true })
          this.output2 = null
          this.idb = null
          this.isButtonDisabled2 = true
          return null
        }
        const { data } = await signaltest.FIRById(this.id, this.selectedFile2)
        if (data !== null) {
          this.$message({ message: '处理成功', type: 'success', center: true })
          this.isButtonDisabled2 = false
        } else {
          this.$message({ message: '请检查模块ID或网络连接', type: 'warning', center: true })
          this.isButtonDisabled2 = true
        }
        this.output2 = data
        this.idb = null
        this.addTestHistory('FIR', this.id)
      }
    },
    async signaltest() {
      if (this.id1 === '1') {
        if (this.selectedFile1 === null) {
          this.$message({ message: '文件不能为空', type: 'error', center: true });
          this.output1 = null;
          this.ida = null;
          this.isButtonDisabled1 = true;
          return null;
        }
        const response = await signaltest.FFT(this.value, this.selectedFile1);
        console.log("FFT response:", response);
        if (response.data !== null) {
          console.log("FFT data:", response.data);
          this.$message({ message: '处理成功', type: 'success', center: true });
          this.isButtonDisabled1 = false;
          this.output1 = response.data;
          console.log("Set output1:", this.output1);
          this.getoutput1(); // 立即调用 getoutput1
        } else {
          this.$message({ message: '请检查模块ID或网络连接', type: 'warning', center: true });
          this.isButtonDisabled1 = true;
        }
        this.ida = response.data.id;
        this.addTestHistory('FFT', this.ida);
      }
      if (this.id1 === '2') {
        if (this.selectedFile2 === null) {
          this.$message({ message: '文件不能为空', type: 'error', center: true })
          this.output2 = null
          this.idb = null
          this.isButtonDisabled2 = true
          return null
        }
        const response = await signaltest.FIR(this.value, this.selectedFile2);
        console.log("FIR response:", response);
        if (response.data !== null) {
          console.log("FIR data:", response.data);
          this.$message({ message: '处理成功', type: 'success', center: true });
          this.isButtonDisabled2 = false;
          this.output2 = response.data;
          console.log("Set output2:", this.output2);
          this.getoutput2(); // 立即调用 getoutput2
        } else {
          this.$message({ message: '请检查模块ID或网络连接', type: 'warning', center: true });
          this.isButtonDisabled2 = true;
        }
        this.idb = response.data.id;
        this.addTestHistory('FIR', this.idb);
      }
    },
    addTestHistory(algorithm, moduleId) {
      const now = new Date();
      const testId = now.getFullYear().toString().substr(-2) +
                     (now.getMonth() + 1).toString().padStart(2, '0') +
                     now.getDate().toString().padStart(2, '0') +
                     Math.floor(Math.random() * 1000).toString().padStart(3, '0');

      this.testHistory.unshift({
        id: testId,
        status: '处理中',
        module: moduleId,
        algorithm: algorithm
      });

      // Simulate processing time
      setTimeout(() => {
        const index = this.testHistory.findIndex(test => test.id === testId);
        if (index !== -1) {
          this.testHistory[index].status = '已处理';
        }
      }, 3000);

      this.totalTests++;
    },
    getStatusType(status) {
      switch (status) {
        case '处理中': return 'warning';
        case '已处理': return 'success';
        default: return 'info';
      }
    },
    tableRowClassName({row, rowIndex}) {
      if (row.status === '处理中') {
        return 'processing-row';
      }
      return '';
    },
    handleSizeChange(val) {
      this.pageSize = val;
      // Implement logic to fetch data based on new page size
    },
    handleCurrentChange(val) {
      this.currentPage = val;
      // Implement logic to fetch data for the new page
    },
    refreshHistory() {
      // Implement the logic to refresh the test history
      // This could involve fetching the latest data from the server
      this.$message.success('刷新成功');
    },
    downloadFile(algorithm) {
      let content;
      if (algorithm === 'FFT') {
        content = this.output1 && this.output1.fileContent ? this.output1.fileContent : this.output1;
      } else {
        content = this.output2 && this.output2.fileContent ? this.output2.fileContent : this.output2;
      }

      if (!content) {
        this.$message.error('没有可下载的文件内容');
        return;
      }

      console.log(`Downloading ${algorithm} content (first 100 chars):`, content.substring(0, 100));

      const blob = new Blob([content], { type: 'text/plain' });
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = `${algorithm}_output.txt`;
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
      URL.revokeObjectURL(link.href);

      this.$message.success('文件下载成功');
    },
    async downloadHistoryFile(row) {
      try {
        // Assuming you have an API endpoint to fetch the file content
        const { data } = await signaltest.getTestResultFile(row.id);
        if (data) {
          const blob = new Blob([data], { type: 'text/plain' });
          const link = document.createElement('a');
          link.href = URL.createObjectURL(blob);
          link.download = `${row.algorithm}_${row.id}_output.txt`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          URL.revokeObjectURL(link.href);
          this.$message.success('文件下载成功');
        } else {
          this.$message.error('无法获取文件内容');
        }
      } catch (error) {
        console.error('Download error:', error);
        this.$message.error('下载失败，请稍后重试');
      }
    }
  }
}
</script>

<style scoped>
#select1 .el-input {
  width: 100px;
  margin-right: 10px;
}
.el-dialog .el-input{
  width: 85%;
}
.el-button {
  width: 120px;
  margin-right: 10px;
  margin-bottom: 10px;
}
.test-container {
  padding: 20px;
}
.history-card {
  margin-top: 20px;
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.el-table .processing-row {
  background-color: #fdf6ec;
}
.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: center;
}
</style>

<style scoped lang="scss">

</style>












