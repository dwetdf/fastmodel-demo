package com.example.mylogin.sys.controller;
import java.util.List;
import com.example.mylogin.sys.entity.TestResult;
import org.springframework.web.bind.annotation.*;
import com.example.mylogin.sys.service.NetworkTestService;

@RestController
@RequestMapping("/network")
public class NetworkTestController {

    private final NetworkTestService networkTestService;

    public NetworkTestController(NetworkTestService networkTestService) {
        this.networkTestService = networkTestService;
    }

    @GetMapping("/throughput")
    public Result<Double> getThroughput() {
        double throughput = networkTestService.performThroughputTest();
        return Result.success(throughput);
    }

    @GetMapping("/latency")
    public Result<Double> getLatency() {
        double latency = networkTestService.performLatencyTest();
        return Result.success(latency);
    }

    @GetMapping("/packetloss")
    public Result<Double> getPacketLoss() {
        double packetLoss = networkTestService.performPacketLossTest();
        return Result.success(packetLoss);
    }

    @PostMapping("/save-results")
    public Result<Void> saveResults(@RequestBody List<TestResult> results) {
        networkTestService.saveTestResults(results);
        return Result.success();
    }
}
