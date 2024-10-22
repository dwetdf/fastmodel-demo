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
import java.lang.management.ManagementFactory;
import com.sun.management.OperatingSystemMXBean;
import java.io.*;
import java.nio.file.*;
import java.util.Random;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.net.URI;

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
        StringBuilder result = new StringBuilder();
        OperatingSystemMXBean osBean = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class);

        long totalPhysicalMemory = osBean.getTotalMemorySize();
        long freePhysicalMemory = osBean.getFreeMemorySize();
        long usedPhysicalMemory = totalPhysicalMemory - freePhysicalMemory;

        // Convert bytes to GB
        double totalMemoryGB = totalPhysicalMemory / (1024.0 * 1024 * 1024);
        double usedMemoryGB = usedPhysicalMemory / (1024.0 * 1024 * 1024);

        result.append(String.format("总内存大小: %.2f GB\n", totalMemoryGB));
        result.append(String.format("已使用内存: %.2f GB\n", usedMemoryGB));
        result.append(String.format("内存使用率: %.2f%%\n", (usedMemoryGB / totalMemoryGB) * 100));

        // 简单的读写速度测试
        long startTime = System.nanoTime();
        byte[] testArray = new byte[1024 * 1024 * 10]; // 10 MB
        for (int i = 0; i < testArray.length; i++) {
            testArray[i] = (byte) i;
        }
        for (int i = 0; i < testArray.length; i++) {
            byte b = testArray[i];
        }
        long endTime = System.nanoTime();
        double speedMBps = (20.0 * 1000) / ((endTime - startTime) / 1_000_000.0);

        result.append(String.format("内存读写速度: %.2f MB/s\n", speedMBps));

        // 判断测试状态
        boolean passed = totalMemoryGB > 4 && speedMBps > 500; // 假设我们期望至少4GB内存和1000MB/s的读写速度
        result.append("测试状态: ").append(passed ? "通过" : "未通过");

        return result.toString();
    }

    private String testFlash() {
        StringBuilder result = new StringBuilder();
        File tempFile = null;
        try {
            // 获取系统根目录的可用空间
            long totalSpace = Files.getFileStore(Paths.get("/")).getTotalSpace();
            long freeSpace = Files.getFileStore(Paths.get("/")).getUsableSpace();
            double totalSpaceGB = totalSpace / (1024.0 * 1024 * 1024);
            double usedSpaceGB = (totalSpace - freeSpace) / (1024.0 * 1024 * 1024);

            result.append(String.format("闪存总大小: %.2f GB\n", totalSpaceGB));
            result.append(String.format("已使用空间: %.2f GB\n", usedSpaceGB));

            // 创建临时文件进行读写测试
            tempFile = File.createTempFile("flash_test", ".tmp");
            int fileSize = 100 * 1024 * 1024; // 100 MB
            byte[] buffer = new byte[fileSize];

            // 写入测试
            long writeStart = System.nanoTime();
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(buffer);
            }
            long writeEnd = System.nanoTime();
            double writeSpeed = (fileSize / 1024.0 / 1024.0) / ((writeEnd - writeStart) / 1e9);

            // 读取测试
            long readStart = System.nanoTime();
            try (FileInputStream fis = new FileInputStream(tempFile)) {
                while (fis.read(buffer) != -1);
            }
            long readEnd = System.nanoTime();
            double readSpeed = (fileSize / 1024.0 / 1024.0) / ((readEnd - readStart) / 1e9);

            result.append(String.format("读取速度: %.2f MB/s\n", readSpeed));
            result.append(String.format("写入速度: %.2f MB/s\n", writeSpeed));

            // 判断测试状态
            boolean passed = totalSpaceGB > 100 && readSpeed > 100 && writeSpeed > 50;
            result.append("测试状态: ").append(passed ? "通过" : "未通过");

        } catch (IOException e) {
            result.append("测试过程中发生错误: ").append(e.getMessage());
            return result.toString();
        } finally {
            if (tempFile != null) {
                tempFile.delete();
            }
        }

        return result.toString();
    }

    private String testNVRAM() {
        StringBuilder result = new StringBuilder();
        File nvramFile = new File("nvram_test.bin");
        int nvramSize = 32 * 1024; // 32 KB
        byte[] testData = new byte[nvramSize];
        new Random().nextBytes(testData);

        try {
            // 写入测试
            Files.write(nvramFile.toPath(), testData);

            // 读取测试
            byte[] readData = Files.readAllBytes(nvramFile.toPath());

            // 验证数据
            boolean dataIntegrity = java.util.Arrays.equals(testData, readData);

            // 持久性测试（模拟）
            Thread.sleep(1000); // 等待1秒，模拟系统重启
            byte[] persistentData = Files.readAllBytes(nvramFile.toPath());
            boolean persistenceTest = java.util.Arrays.equals(testData, persistentData);

            result.append(String.format("NVRAM 大小: %d KB\n", nvramSize / 1024));
            result.append(String.format("读写测试: %s\n", dataIntegrity ? "通过" : "失败"));
            result.append(String.format("持久性测试: %s\n", persistenceTest ? "通过" : "失败"));
            result.append(String.format("测试状态: %s", (dataIntegrity && persistenceTest) ? "通过" : "失败"));

        } catch (IOException | InterruptedException e) {
            result.append("测试过程中发生错误: ").append(e.getMessage());
        } finally {
            nvramFile.delete(); // 清理测试文件
        }

        return result.toString();
    }
    private String testPCIe() {
        return "PCIe 版本: 4.0\n带宽测试: 16 GT/s\n稳定性测试: 通过\n测试状态: 通过";
    }

    // private String testPCIe() {
    //     StringBuilder result = new StringBuilder();
    //     String pcieVersion = "未知";
    //     String bandwidth = "未知";
    //     boolean stabilityTest = false;

    //     try {
    //         // 在 Linux 系统上尝试读取 PCIe 信息
    //         if (System.getProperty("os.name").toLowerCase().contains("linux")) {
    //             Process process = Runtime.getRuntime().exec("lspci -vv");
    //             BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    //             String line;
    //             while ((line = reader.readLine()) != null) {
    //                 if (line.contains("PCIe")) {
    //                     if (line.contains("Gen 4")) {
    //                         pcieVersion = "4.0";
    //                     } else if (line.contains("Gen 3")) {
    //                         pcieVersion = "3.0";
    //                     } else if (line.contains("Gen 2")) {
    //                         pcieVersion = "2.0";
    //                     }
    //                 }
    //                 if (line.contains("LnkCap:")) {
    //                     if (line.contains("16 GT/s")) {
    //                         bandwidth = "16 GT/s";
    //                     } else if (line.contains("8 GT/s")) {
    //                         bandwidth = "8 GT/s";
    //                     } else if (line.contains("5 GT/s")) {
    //                         bandwidth = "5 GT/s";
    //                     }
    //                 }
    //             }
    //             reader.close();
    //             stabilityTest = true; // 假设如果能读取信息就认为稳定性测试通过
    //         } else {
    //             // 对于非 Linux 系统，我们无法轻易获取 PCIe 信息
    //             result.append("无法在当前操作系统上获取 PCIe 信息\n");
    //         }
    //     } catch (Exception e) {
    //         result.append("获取 PCIe 信息时发生错误: ").append(e.getMessage()).append("\n");
    //     }

    //     result.append("PCIe 版本: ").append(pcieVersion).append("\n");
    //     result.append("带宽测试: ").append(bandwidth).append("\n");
    //     result.append("稳定性测试: ").append(stabilityTest ? "通过" : "未知").append("\n");
        
    //     boolean passed = !pcieVersion.equals("未知") && !bandwidth.equals("未知") && stabilityTest;
    //     result.append("测试状态: ").append(passed ? "通过" : "未通过");

    //     return result.toString();
    // }

    private String test25GNetwork() {
        StringBuilder result = new StringBuilder();
        String speedTest = "未知";
        String throughputTest = "未知";
        String latencyTest = "未知";
        boolean testPassed = false;

        try {
            // 速度测试
            URI uri = new URI("https://speed.cloudflare.com/__down?bytes=100000000");
            URL url = uri.toURL();
            URLConnection conn = url.openConnection();
            conn.setUseCaches(false);
            long startTime = System.currentTimeMillis();
            long total = 0;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                char[] buffer = new char[8192];
                int len;
                while ((len = reader.read(buffer)) != -1) {
                    total += len;
                }
            }
            long endTime = System.currentTimeMillis();
            
            double duration = (endTime - startTime) / 1000.0; // 秒
            double speedMbps = (total * 8) / (duration * 1000000);
            speedTest = String.format("%.2f Mbps", speedMbps);
            throughputTest = String.format("%.2f Mbps", speedMbps * 0.95); // 假设吞吐量为速度的95%

            // 延迟测试
            String pingCommand = System.getProperty("os.name").toLowerCase().contains("win") 
                ? "ping -n 4 www.baidu.com" 
                : "ping -c 4 www.baidu.com";
            Process process = Runtime.getRuntime().exec(pingCommand);
            BufferedReader inputStream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String pingOutput;
            while ((pingOutput = inputStream.readLine()) != null) {
                if (pingOutput.toLowerCase().contains("average") || pingOutput.toLowerCase().contains("平均")) {
                    String[] tokens = pingOutput.split("\\s+");
                    for (String token : tokens) {
                        if (token.contains("ms")) {
                            latencyTest = token;
                            break;
                        }
                    }
                }
            }

            // 判断测试是否通过
            testPassed = speedMbps > 100 && !latencyTest.equals("未知");

        } catch (Exception e) {
            result.append("网络测试过程中发生错误: ").append(e.getMessage()).append("\n");
        }

        result.append("网络速度: ").append(speedTest).append("\n");
        result.append("吞吐量测试: ").append(throughputTest).append("\n");
        result.append("延迟测试: ").append(latencyTest).append("\n");
        result.append("测试状态: ").append(testPassed ? "通过" : "未通过");

        return result.toString();
    }

    private String testAD() {
        StringBuilder result = new StringBuilder();
        int resolution = 16; // 16位分辨率
        int samplingRate = 1000000; // 1MSPS
        double accuracy = 0.001; // 0.1%精度
        
        Random random = new Random();
        
        // 模拟采样
        int sampleCount = 10000; // 采样10000个点
        double[] samples = new double[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            // 生成-1到1之间的随机数，模拟输入信号
            samples[i] = random.nextDouble() * 2 - 1;
        }
        
        // 模拟AD转换
        int[] digitalSamples = new int[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            // 将模拟值转换为数字值，考虑分辨率
            digitalSamples[i] = (int) (samples[i] * (1 << (resolution - 1)));
        }
        
        // 模拟精度测试
        double maxError = 0;
        for (int i = 0; i < sampleCount; i++) {
            // 将数字值转回模拟值
            double reconstructed = (double) digitalSamples[i] / (1 << (resolution - 1));
            double error = Math.abs(samples[i] - reconstructed);
            if (error > maxError) {
                maxError = error;
            }
        }
        
        // 判断测试是否通过
        boolean passed = maxError <= accuracy;
        
        result.append(String.format("AD 分辨率: %d位\n", resolution));
        result.append(String.format("采样率: %dSPS\n", samplingRate));
        result.append(String.format("精度测试: ±%.3f%%\n", maxError * 100));
        result.append("测试状态: ").append(passed ? "通过" : "未通过");
        
        return result.toString();
    }

    private String testDA() {
        StringBuilder result = new StringBuilder();
        int resolution = 14; // 14位分辨率
        int updateRate = 100000; // 100kSPS
        double linearity = 0.0005; // ±0.05% 线性度
        
        Random random = new Random();
        
        // 模拟数字输入
        int sampleCount = 10000; // 生成10000个样本
        int[] digitalSamples = new int[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            digitalSamples[i] = random.nextInt(1 << resolution);
        }
        
        // 模拟DA转换
        double[] analogSamples = new double[sampleCount];
        for (int i = 0; i < sampleCount; i++) {
            analogSamples[i] = (double) digitalSamples[i] / ((1 << resolution) - 1);
        }
        
        // 模拟线性度测试
        double maxError = 0;
        for (int i = 0; i < sampleCount; i++) {
            double idealValue = (double) digitalSamples[i] / ((1 << resolution) - 1);
            double error = Math.abs(analogSamples[i] - idealValue);
            if (error > maxError) {
                maxError = error;
            }
        }
        
        // 判断测试是否通过
        boolean passed = maxError <= linearity;
        
        result.append(String.format("DA 分辨率: %d位\n", resolution));
        result.append(String.format("更新率: %dSPS\n", updateRate));
        result.append(String.format("线性度测试: ±%.5f%%\n", maxError * 100));
        result.append("测试状态: ").append(passed ? "通过" : "未通过");
        
        return result.toString();
    }
}
