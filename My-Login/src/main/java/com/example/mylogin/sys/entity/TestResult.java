package com.example.mylogin.sys.entity;

import lombok.Data;

@Data
public class TestResult {
    private boolean success;
    private String details;

    public TestResult(boolean success, String details) {
        this.success = success;
        this.details = details;
    }
}