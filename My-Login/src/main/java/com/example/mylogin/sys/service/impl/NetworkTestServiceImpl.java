package com.example.mylogin.sys.service.impl;

import com.example.mylogin.sys.service.NetworkTestService;
import com.example.mylogin.sys.entity.TestResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Service
public class NetworkTestServiceImpl implements NetworkTestService {

    @Override
    public double performThroughputTest() {
        // 实现吞吐量测试逻辑
        return Math.random() * 100; // 模拟返回 0-100 Mbps
    }

    @Override
    public double performLatencyTest() {
        // 实现延迟测试逻辑
        return Math.random() * 100; // 模拟返回 0-100 ms
    }

    @Override
    public double performPacketLossTest() {
        // 实现丢包率测试逻辑
        return Math.random() * 5; // 模拟返回 0-5%
    }

     @Override
    public void saveTestResults(List<TestResult> results) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();
        try {
            writer.writeValue(new File("network_test_results.json"), results);
            System.out.println("Test results saved to network_test_results.json");
        } catch (IOException e) {
            System.err.println("Error saving test results: " + e.getMessage());
            e.printStackTrace();
        }
    }
}