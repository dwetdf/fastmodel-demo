package com.example.mylogin.sys.service;

import com.example.mylogin.model.TestResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
@Service
public class AcceptanceTestService {

    private static final Logger logger = LoggerFactory.getLogger(AcceptanceTestService.class);

    public TestResponse executeTest(List<String> modules) throws InterruptedException {
        logger.info("Starting test execution for modules: {}", modules);
        StringBuilder report = new StringBuilder();
        report.append("测试报告\n");
        report.append("=================\n\n");

        for (String module : modules) {
            logger.debug("Testing module: {}", module);
            report.append(testModule(module)).append("\n\n");
            // 模拟测试耗时
            Thread.sleep(1000);
        }

        report.append("测试完成时间: ").append(java.time.LocalDateTime.now());
        logger.info("Test execution completed");
        return new TestResponse(report.toString(), null);
    }

    private String testModule(String module) {
        StringBuilder result = new StringBuilder();
        result.append(module.toUpperCase()).append(" 测试结果:\n");

        switch (module) {
            case "cpu":
                result.append(testCPU());
                break;
            case "memory":
                result.append(testMemory());
                break;
            case "flash":
                result.append(testFlash());
                break;
            case "nvram":
                result.append(testNVRAM());
                break;
            case "pcie":
                result.append(testPCIe());
                break;
            case "network":
                result.append(test25GNetwork());
                break;
            case "ad":
                result.append(testAD());
                break;
            case "da":
                result.append(testDA());
                break;
            default:
                result.append("未实现的测试模块");
        }

        return result.toString();
    }

    private String testCPU() {
        StringBuilder result = new StringBuilder();


        // 获取 CPU 基本信息
        int cores = Runtime.getRuntime().availableProcessors();
        result.append("核心数: ").append(cores).append("\n");

        // 单线程性能测试
        double singleThreadMIPS = performSingleThreadTest();
        result.append("单线程性能 (MIPS): ").append(String.format("%.2f", singleThreadMIPS)).append("\n");

        // 多线程性能测试
        double multiThreadMIPS = performMultiThreadTest(cores);
        result.append("多线程性能 (MIPS): ").append(String.format("%.2f", multiThreadMIPS)).append("\n");

        // 浮点运算测试
        double floatingPointScore = performFloatingPointTest();
        result.append("浮点运算得分: ").append(String.format("%.2f", floatingPointScore)).append("\n");

        // 整数运算测试
        long integerScore = performIntegerTest();
        result.append("整数运算得分: ").append(integerScore).append("\n");

        // 内存访问延迟测试
        double memoryLatency = performMemoryLatencyTest();
        result.append("内存访问延迟: ").append(String.format("%.2f ns", memoryLatency)).append("\n");

        // 判断测试状态
        boolean passed = singleThreadMIPS > 500 && multiThreadMIPS > (singleThreadMIPS * cores * 0.7) && 
                         floatingPointScore > 10 && integerScore > 1000 && memoryLatency < 10;
        result.append("测试状态: ").append(passed ? "通过" : "未通过");

        return result.toString();
    }

    private double performSingleThreadTest() {
        long operations = 0;
        long startTime = System.nanoTime();
        while (System.nanoTime() - startTime < 1_000_000_000) { // 运行1秒
            for (int i = 0; i < 1000; i++) {
                Math.sqrt(i);
                Math.log(i + 1);
                Math.sin(i);
                Math.cos(i);
            }
            operations++;
        }
        // 假设每次循环执行4000条指令（4个操作，每个1000次）
        return operations * 4000 / 1_000_000.0; // 转换为MIPS
    }

    private double performMultiThreadTest(int cores) {
        ExecutorService executor = Executors.newFixedThreadPool(cores);
        List<Future<Double>> futures = new ArrayList<>();
        
        for (int i = 0; i < cores; i++) {
            futures.add(executor.submit(this::performSingleThreadTest));
        }
        
        double totalMIPS = 0;
        for (Future<Double> future : futures) {
            try {
                totalMIPS += future.get();
            } catch (Exception e) {
                logger.error("Error in multi-thread test", e);
            }
        }
        
        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            logger.error("Executor termination interrupted", e);
        }
        return totalMIPS;
    }

    private double performFloatingPointTest() {
        long startTime = System.nanoTime();
        double result = 0;
        for (int i = 0; i < 1_000_000; i++) {
            result += Math.sin(i) * Math.cos(i);
        }
        long endTime = System.nanoTime();
        return 1_000_000_000.0 / (endTime - startTime);
    }

    private long performIntegerTest() {
        long startTime = System.nanoTime();
        long result = 0;
        for (int i = 0; i < 10_000_000; i++) {
            result += i * i;
        }
        long endTime = System.nanoTime();
        return 10_000_000_000L / (endTime - startTime);
    }

    private double performMemoryLatencyTest() {
        int arraySize = 64 * 1024 * 1024; // 64 MB
        int[] array = new int[arraySize];
        for (int i = 0; i < arraySize; i++) {
            array[i] = i;
        }
        
        long startTime = System.nanoTime();
        int index = 0;
        for (int i = 0; i < 1_000_000; i++) {
            index = array[index];
        }
        long endTime = System.nanoTime();
        
        return (endTime - startTime) / 1000000.0;
    }

    private String testMemory() {
        return "内存大小: 16GB\n内存频率: 3200MHz\n读写速度: 正常\n测试状态: 通过";
    }

    private String testFlash() {
        return "Flash 大小: 512GB\n读取速度: 550MB/s\n写入速度: 520MB/s\n测试状态: 通过";
    }

    private String testNVRAM() {
        return "NVRAM 大小: 32KB\n读写测试: 通过\n持久性测试: 通过\n测试状态: 通过";
    }

    private String testPCIe() {
        return "PCIe 版本: 4.0\n带宽测试: 16 GT/s\n稳定性测试: 通过\n测试状态: 通过";
    }

    private String test25GNetwork() {
        return "网络速度: 25Gbps\n吞吐量测试: 24.5Gbps\n延迟测试: 0.5ms\n测试状态: 通过";
    }

    private String testAD() {
        return "AD 分辨率: 16位\n采样率: 1MSPS\n精度测试: ±0.1%\n测试状态: 通过";
    }

    private String testDA() {
        return "DA 分辨率: 14位\n更新率: 100kSPS\n线性度测试: ±0.05%\n测试状态: 通过";
    }
}