package com.slaveselfcheck.sys.controller;

import com.slaveselfcheck.sys.entity.OperationInfo;
import com.slaveselfcheck.sys.service.ComputerComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author wow
 * @since 2024-04-15
 */

@RestController
@RequestMapping("/heartBeat")
public class SelfCheckController {


    @Autowired
    private ComputerComponentService computerComponentService;
    @GetMapping("/info")
    //参数是在url后面的，用@RequestParam可以获取到
    public Result<OperationInfo> getUserInfo(){
        System.out.println("开始输出显卡信息---------------");
        computerComponentService.queryGraphicsCardMessage();
        System.out.println("结束显卡信息---------------");

        System.out.println("开始输出内存条信息---------------");
        computerComponentService.queryMemoryMessage();
        System.out.println("结束内存条信息---------------");

        System.out.println("开始输出CPU信息---------------");
        computerComponentService.queryCentralMessage();
        System.out.println("结束CPU信息---------------");

        System.out.println("开始输出声卡信息---------------");
        computerComponentService.querySoundCardMessage();
        System.out.println("结束声卡信息---------------");

        System.out.println("开始输出主板信息---------------");
        computerComponentService.queryBaseboardMessage();
        System.out.println("结束主板信息---------------");
        OperationInfo operationInfo=computerComponentService.queryOperationInfo();
        return Result.success(operationInfo);
    }


}
