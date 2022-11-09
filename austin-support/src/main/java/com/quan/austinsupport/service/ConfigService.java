package com.quan.austinsupport.service;

/**
 * Description:
 * date: 2022/11/09 下午 6:46
 *
 * @author Four
 */
public interface ConfigService {

    /**
     * 读取配置
     * 1、当启动使用了apollo或者nacos，优先读取远程配置
     * 2、当没有启动远程配置，读取本地 local.properties 配置文件的内容
     * @param key
     * @param defaultValue
     * @return
     */
    String getProperty(String key, String defaultValue);

}
