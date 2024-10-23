package com.example.mylogin.sys.service;

public class DeviceInfo {
    private String ipAddress;
    private String cpu = "N/A";
    private String memory = "N/A";
    private String model;
    private String disk = "N/A";
    private String status;
    private String errorMessage;
    // Getters and setters
    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
    public String getCpu() { return cpu; }
    public void setCpu(String cpu) { this.cpu = cpu; }
    public String getMemory() { return memory; }
    public void setMemory(String memory) { this.memory = memory; }
    public String getDisk() { return disk; }
    public void setDisk(String disk) { this.disk = disk; }
    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public static DeviceInfo fromString(String infoString) {
        DeviceInfo info = new DeviceInfo();
        // Parse infoString and set fields accordingly
        return info;
    }
}

