package com.slaveselfcheck.sys.service;


import com.slaveselfcheck.sys.entity.OperationInfo;

/**
 * @author : gaogao
 * @version 1.0
 * @date : 2023-12-14 15:28
 */

public interface ComputerComponentService {

    /**
     * 获取显卡信息
     */
    void queryGraphicsCardMessage();

    /**
     * 获取内存条信息
     */
    void queryMemoryMessage();

    /**
     * 获取CPU信息
     */
    void queryCentralMessage();

    /**
     * 获取声卡信息
     */
    void querySoundCardMessage();

    /**
     * 获取主板信息
     */
    void queryBaseboardMessage();

    OperationInfo queryOperationInfo();

}

