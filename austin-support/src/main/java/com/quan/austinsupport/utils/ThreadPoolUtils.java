package com.quan.austinsupport.utils;

import com.dtp.core.DtpRegistry;
import com.dtp.core.thread.DtpExecutor;
import com.quan.austinsupport.config.ThreadPoolExecutorShutdownDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/*
    线程池工具类
 */
@Component
public class ThreadPoolUtils {
    /*
        线程池优雅关闭实现
     */
    @Autowired
    private ThreadPoolExecutorShutdownDefinition shutdownDefinition;

    private static final String SOURCE_NAME = "austin";

    /*
        1. 将当前线程池加入到动态线程池中
        2. 注册线程池到Spring中，方便执行优雅关闭
     */
    public void register(DtpExecutor dtpExecutor) {
        DtpRegistry.registerDtp(dtpExecutor, SOURCE_NAME);
        shutdownDefinition.registryExecutor(dtpExecutor);
    }
}
