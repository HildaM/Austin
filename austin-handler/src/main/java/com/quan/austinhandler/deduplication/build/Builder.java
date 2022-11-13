package com.quan.austinhandler.deduplication.build;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.DeduplicationParam;

/**
 * Description: 简单工厂模式 —— 根据不同参数创建不同的过期Param
 * date: 2022/11/09 下午 8:32
 *
 * @author Four
 */
public interface Builder {

    String DEDUPLICATION_CONFIG_PRE = "deduplication_";

    /**
     * 根据配置构建去重参数
     *
     * @param deduplication
     * @param taskInfo
     * @return
     */
    DeduplicationParam build(String deduplication, TaskInfo taskInfo);
}

