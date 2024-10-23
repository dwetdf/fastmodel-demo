package com.example.mylogin.sys.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.mylogin.sys.service.LowerMachineCommService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DeviceController {

    @Autowired
    private LowerMachineCommService lowerMachineCommService;

    @PostMapping("/update-ip-settings")
    public ResponseEntity<?> updateIPSettings(@RequestBody Map<String, String> ipSettings) {
        try {
            lowerMachineCommService.updateDeviceUrls(ipSettings);
            return ResponseEntity.ok().body(Map.of("success", true));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", e.getMessage()));
        }
    }
}
