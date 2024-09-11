package com.example.mylogin.sys.controller;

import com.example.mylogin.model.TestRequest;
import com.example.mylogin.model.TestResponse;
import com.example.mylogin.sys.service.AcceptanceTestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AcceptanceTestController {

    private static final Logger logger = LoggerFactory.getLogger(AcceptanceTestController.class);

    private final AcceptanceTestService testService;

    @Autowired
    public AcceptanceTestController(AcceptanceTestService testService) {
        this.testService = testService;
    }

    @PostMapping("/execute-test")
    public ResponseEntity<TestResponse> executeTest(@RequestBody TestRequest request) {
        logger.info("Received execute-test request with modules: {}", request.getModules());
        try {
            TestResponse response = testService.executeTest(request.getModules());
            logger.info("Test execution completed successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error executing test", e);
            return ResponseEntity.internalServerError().body(new TestResponse(null, "测试执行失败: " + e.getMessage()));
        }
    }
}