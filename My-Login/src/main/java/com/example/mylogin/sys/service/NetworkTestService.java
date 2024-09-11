package com.example.mylogin.sys.service;

import com.example.mylogin.sys.entity.TestResult;
import java.util.List;

public interface NetworkTestService {
    double performThroughputTest();
    double performLatencyTest();
    double performPacketLossTest();
    void saveTestResults(List<TestResult> results);
}