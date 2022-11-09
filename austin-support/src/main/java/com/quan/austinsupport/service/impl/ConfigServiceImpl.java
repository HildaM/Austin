package com.quan.austinsupport.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;
import com.quan.austinsupport.service.ConfigService;
import com.quan.austinsupport.utils.NacosUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Description:
 * date: 2022/11/09 下午 6:47
 *
 * @author Four
 */
@Service
public class ConfigServiceImpl implements ConfigService {

    /**
     * 本地配置
     */
    private static final String PROPERTIES_PATH = "local.properties";
    private Props props = new Props(PROPERTIES_PATH, StandardCharsets.UTF_8);

    /**
     * apollo配置
     */
    @Value("${apollo.bootstrap.enabled}")
    private Boolean enableApollo;
    @Value("${apollo.bootstrap.namespaces}")
    private String namespaces;
    /**
     * nacos配置
     */
    @Value("${austin.nacos.enabled}")
    private Boolean enableNacos;
    @Autowired
    private NacosUtils nacosUtils;

    @Override
    public String getProperty(String key, String defaultValue) {
        // TODO Apollo配置中心引入

        if (enableNacos) {
            return nacosUtils.getProperty(key, defaultValue);
        } else {
            return props.getProperty(key, defaultValue);
        }
    }
}
