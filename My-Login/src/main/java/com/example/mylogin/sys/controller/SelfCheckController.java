package com.example.mylogin.sys.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import java.util.List;
import com.example.mylogin.sys.service.SelfCheckService;
import com.example.mylogin.sys.service.DeviceStatus;

@RestController
@RequestMapping("/api")
public class SelfCheckController {

    @Autowired
    private SelfCheckService selfCheckService;

    @GetMapping("/self-check")
    public ResponseEntity<List<DeviceStatus>> performSelfCheck(@RequestParam(required = false) String deviceId) {
        List<DeviceStatus> results;
        if (deviceId != null && !deviceId.equals("all")) {
            results = List.of(selfCheckService.checkDevice(deviceId));
        } else {
            results = selfCheckService.checkAllDevices();
        } 
        for(int i=0;i<results.size();i++){
            System.out.println("***"+results.get(i).getInfo());
        }
        return ResponseEntity.ok(results);
    }
}



