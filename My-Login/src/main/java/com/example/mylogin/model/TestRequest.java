package com.example.mylogin.model;

import java.util.List;

public class TestRequest {
    private String device;
    private List<String> modules;

    public TestRequest() {}

    public TestRequest(List<String> modules) {
        this.modules = modules;
    }

    public String getDevice() {
        return device;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}