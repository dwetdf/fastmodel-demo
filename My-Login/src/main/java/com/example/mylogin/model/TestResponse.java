package com.example.mylogin.model;

import com.example.mylogin.sys.entity.TestResult;
import java.util.Map;

public class TestResponse {
    private String report;
    private Map<String, TestResult> results;
    private String errorMessage;
    private String status;
    private String message;

    public TestResponse() {}

    public TestResponse(String report, Map<String, TestResult> results) {
        this.report = report;
        this.results = results;
    }

    public TestResponse(String message) {
        this.message = message;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public Map<String, TestResult> getResults() {
        return results;
    }

    public void setResults(Map<String, TestResult> results) {
        this.results = results;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public TestResponse(boolean success, String message) {
        this.status = success ? "success" : "failure";
        this.message = message;
    }       
}