package com.example.mylogin.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.mylogin.sys.service.HeterogeneousTestService;
import com.example.mylogin.model.TestRequest;
import com.example.mylogin.model.TestResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class HeterogeneousTestController {

    @Autowired
    private HeterogeneousTestService heterogeneousTestService;

    @PostMapping("/execute-test-heterogeneous")
    public ResponseEntity<TestResponse> executeHeterogeneousTest(@RequestBody TestRequest request) {
        try {
            log.info("Received heterogeneous test request for device: {}, modules: {}", 
                    request.getDevice(), request.getModules());
            
            TestResponse response = heterogeneousTestService.executeTest(request);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error executing heterogeneous test", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new TestResponse(false, e.getMessage()));
        }
    }
}
