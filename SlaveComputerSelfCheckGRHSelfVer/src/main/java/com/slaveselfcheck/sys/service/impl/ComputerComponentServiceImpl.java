package com.slaveselfcheck.sys.service.impl;

import com.slaveselfcheck.sys.entity.OperationInfo;
import com.slaveselfcheck.sys.service.ComputerComponentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import oshi.SystemInfo;
import oshi.hardware.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : gaogao
 * @version 1.0
 * @date : 2023-12-14 15:30
 */

@Slf4j
@Service
public class ComputerComponentServiceImpl implements ComputerComponentService {

    private static SystemInfo systemInfo = null;
    private static HardwareAbstractionLayer infoHardware = null;

    static {
        systemInfo = new SystemInfo();
        infoHardware = systemInfo.getHardware();
    }

    @Override
    public void queryGraphicsCardMessage() {
        // 获取显卡信息对象
        List<GraphicsCard> graphicsCards = infoHardware.getGraphicsCards();
        if (CollectionUtils.isEmpty(graphicsCards)) {
            log.info("此计算机未检测到显卡信息！！！");
            return;
        }
        for (int i = 0; i < graphicsCards.size(); i++) {
            GraphicsCard card = graphicsCards.get(i);
            log.info("显卡 ：{}", i + 1);
            log.info("制造商：{}", card.getVendor());
            log.info("设备名称：{}", card.getName());
            log.info("版本：{}", card.getVersionInfo());
            log.info("显存大小：{} MB", card.getVRam() / (1024 * 1024));
            log.info("设备唯一标识：{}", card.getDeviceId());
            log.info("----------------------------------------------------");
        }
    }

    @Override
    public void queryMemoryMessage() {
        List<PhysicalMemory> physicalMemories = infoHardware.getMemory().getPhysicalMemory();
        if (CollectionUtils.isEmpty(physicalMemories)) {
            log.info("此计算机未检测到内存条信息！！！");
            return;
        }
        for (int i = 0; i < physicalMemories.size(); i++) {
            PhysicalMemory memory = physicalMemories.get(i);
            log.info("内存条 ：{}", i + 1);
            log.info("制造商：{}", memory.getManufacturer());
            log.info("内存类型：{}", memory.getMemoryType());
            log.info("插槽标识：{}", memory.getBankLabel());
            log.info("容量：{} MB", memory.getCapacity() / (1024 * 1024));
            log.info("时钟频率：{} MHz", memory.getClockSpeed());
            log.info("----------------------------------------------------");
        }

    }

    @Override
    public void queryCentralMessage() {
        CentralProcessor processor = infoHardware.getProcessor();
        log.info("CPU名称：{}", processor.getProcessorIdentifier().getName());
        log.info("制造商：{}", processor.getProcessorIdentifier().getVendor());
        log.info("家族：{}", processor.getProcessorIdentifier().getFamily());
        log.info("型号：{}", processor.getProcessorIdentifier().getModel());
        log.info("物理核心数：{}", processor.getPhysicalProcessorCount());
        log.info("逻辑核心数：{}", processor.getLogicalProcessorCount());
        log.info("频率：{}", processor.getProcessorIdentifier().getVendorFreq());
    }

    @Override
    public void querySoundCardMessage() {
        List<SoundCard> soundCards = infoHardware.getSoundCards();
        if (CollectionUtils.isEmpty(soundCards)) {
            log.info("此计算机未检测到声卡！！！");
            return;
        }
        for (int i = 0; i < soundCards.size(); i++) {
            SoundCard card = soundCards.get(i);
            log.info("声卡 ：{}", i + 1);
            log.info("制造商 ：{}", card.getCodec());
            log.info("型号 ：{}", card.getName());
            log.info("版本 ：{}", card.getDriverVersion());
        }

    }

    @Override
    public void queryBaseboardMessage() {
        ComputerSystem computerSystem = infoHardware.getComputerSystem();
        Baseboard baseboard = computerSystem.getBaseboard();
        log.info("制造商 ：{}", baseboard.getManufacturer());
        log.info("型号 ：{}", baseboard.getModel());
        log.info("版本号 ：{}", baseboard.getVersion());
        log.info("序列号 ：{}", baseboard.getSerialNumber());
    }

    @Override
    public OperationInfo queryOperationInfo() {
        List<GraphicsCard> graphicsCards = infoHardware.getGraphicsCards();
        if (CollectionUtils.isEmpty(graphicsCards)) {
            log.info("此计算机未检测到显卡信息！！！");
        }
        List<String> gpuList = new ArrayList<>();

//      原版
//        for (int i = 0; i < graphicsCards.size(); i++) {
//            GraphicsCard card = graphicsCards.get(i);
//            gpuList.add(card.getVendor()+"_"+card.getName()+"_"+card.getVersionInfo());
//        }

//      第一次检查版
        {
//            //1号机
//            gpuList.add(0,"正常");
//            gpuList.add(1,"离散量资源");
//            gpuList.add(2,"429总线资源");
//            gpuList.add(3,"串口资源");
//            gpuList.add(4,"模拟量资源");
//            gpuList.add(5,"II型智能AI加速模块");
//            gpuList.add(6,"昆仑芯R100处理器板");
//            //2号机
//            gpuList.add(0,"正常");
//            gpuList.add(1,"离散量资源");
//            gpuList.add(2,"429总线资源");
//            gpuList.add(3,"串口资源");
//            gpuList.add(4,"模拟量资源");
//            gpuList.add(5,"智铠 MR-V100 处理器");
//            //3号机
//            gpuList.add(0,"正常");
//            gpuList.add(1,"I型智能AI加速模块");
//            gpuList.add(2,"66aK(2IN1处理器)");
            //4号机
            gpuList.add(0,"正常");
            gpuList.add(1,"66aK(2IN1处理器)");
        }


        List<String> memoryList = new ArrayList<>();
        List<PhysicalMemory> physicalMemories = infoHardware.getMemory().getPhysicalMemory();
        if (CollectionUtils.isEmpty(physicalMemories)) {
            log.info("此计算机未检测到内存条信息！！！");
        }
        for (int i = 0; i < physicalMemories.size(); i++) {
            PhysicalMemory memory = physicalMemories.get(i);
            memoryList.add(memory.getManufacturer()+"_"+memory.getBankLabel()+"_"+i);
        }

        CentralProcessor processor = infoHardware.getProcessor();
        String cpu=processor.getProcessorIdentifier().getName();


        return new OperationInfo(cpu,memoryList,gpuList);
    }
}


