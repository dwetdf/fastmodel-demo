package com.example.mylogin.sys.service;

import com.example.mylogin.model.TestRequest;
import com.example.mylogin.model.TestResponse;

public interface HeterogeneousTestService {
    TestResponse executeTest(TestRequest request) throws Exception;
}
