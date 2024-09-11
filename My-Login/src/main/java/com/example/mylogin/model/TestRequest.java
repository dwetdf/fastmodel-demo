package com.example.mylogin.model;

import java.util.List;

public class TestRequest {
    private List<String> modules;

    public TestRequest() {}

    public TestRequest(List<String> modules) {
        this.modules = modules;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}