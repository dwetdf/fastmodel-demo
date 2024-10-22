import request from '@/utils/request'

const signalApi = {
  FFTById(id, selectedFile) {
    const formData = new FormData()
    formData.append('id', id)
    formData.append('selectedFile', selectedFile)
    return request({
      url: '/test1/FFT',
      method: 'post',
      data: formData
    })
  },
  FIRById(id, selectedFile) {
    const formData = new FormData()
    formData.append('id', id)
    formData.append('selectedFile', selectedFile)
    return request({
      url: '/test1/FIR',
      method: 'post',
      data: formData
    })
  },
  FFT(value, selectedFile) {
    const formData = new FormData()
    formData.append('value', value)
    formData.append('selectedFile', selectedFile)
    return request({
      url: '/test1/FFTA',
      method: 'post',
      data: formData
    })
  },
  FIR(value, selectedFile) {
    const formData = new FormData()
    formData.append('value', value)
    formData.append('selectedFile', selectedFile)
    return request({
      url: '/test1/FIRA',
      method: 'post',
      data: formData
    })
  },
  downloadTestResult(testId) {
    console.log('Sending request to download test result for ID:', testId);
    return new Promise((resolve, reject) => {
      const xhr = new XMLHttpRequest();
      xhr.open('GET', `/test1/download/${testId}`, true);
      xhr.responseType = 'blob';
      
      xhr.onload = function() {
        if (this.status === 200) {
          resolve(this.response);
        } else {
          reject(new Error(`HTTP error! status: ${this.status}`));
        }
      };
      
      xhr.onerror = function() {
        reject(new Error('Network error occurred'));
      };
      
      xhr.send();
    }).then(response => {
      console.log('Response received:', response);
      return response;
    }).catch(error => {
      console.error('Request failed in signaltest.js:', error);
      throw error;
    });
  }
}
export default signalApi
