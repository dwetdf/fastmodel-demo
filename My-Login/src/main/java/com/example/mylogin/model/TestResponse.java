package com.example.mylogin.model;

public class TestResponse {
    private String report;
    private String errorMessage;

    public TestResponse() {}

    public TestResponse(String report, String errorMessage) {
        this.report = report;
        this.errorMessage = errorMessage;
    }

    public String getReport() {
        return report;
    }

    public void setReport(String report) {
        this.report = report;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}