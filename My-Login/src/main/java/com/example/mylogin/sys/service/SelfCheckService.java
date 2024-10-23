package com.example.mylogin.sys.service;
import com.example.mylogin.sys.entity.OperationInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class SelfCheckService {

    // 这里应该注入实际与下位机通信的服务
    @Autowired
    private LowerMachineCommService commService;

    public List<DeviceStatus> checkAllDevices() {
        List<DeviceStatus> results = new ArrayList<>();
        // 获取所有下位机列表，并对每个进行检查
        List<String> deviceIds = commService.getAllDeviceIds();
        for (String id : deviceIds) {
            results.add(checkDevice(id));
        }
        return results;
    }

    public DeviceStatus checkDevice(String deviceId) {
        DeviceStatus status = new DeviceStatus();
        status.setId(deviceId);

        try {
            // Set a timeout of 30 seconds
            CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
                return commService.getUserInfo(deviceId);
            });

            String infoString = future.get(10, TimeUnit.SECONDS);
            System.out.println(infoString);
            ObjectMapper mapper = new ObjectMapper();
            DeviceStatus info = mapper.readValue(infoString, DeviceStatus.class);
            // Thread.sleep(3000);
            status.setStatus("online");
            status.setInfo(info.getInfo());
            status.setId(info.getId());
        } catch (TimeoutException e) {
            status.setStatus("offline");
            status.setInfo(new OperationInfo()); // Empty info
            System.out.println("Device " + deviceId + " check timed out after 10 seconds");
        } catch (Exception e) {
            status.setStatus("offline");
            status.setInfo(new OperationInfo()); // Empty info
            System.out.println("Error checking device " + deviceId + ": " + e.getMessage());
        }

        return status;
    }

}
