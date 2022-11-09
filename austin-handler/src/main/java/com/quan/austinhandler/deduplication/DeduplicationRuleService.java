package com.quan.austinhandler.deduplication;

import com.quan.austincommon.constant.AustinConstant;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.DeduplicationType;
import com.quan.austinsupport.service.ConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Description:
 * date: 2022/11/09 下午 8:07
 *
 * @author Four
 */
@Service
public class DeduplicationRuleService {
    public static final String DEDUPLICATION_RULE_KEY = "deduplicationRule";

    @Autowired
    private ConfigService config;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    public void duplication(TaskInfo taskInfo) {
        // 获取配置
        // 配置样例：{"deduplication_10":{"num":1,"time":300},"deduplication_20":{"num":5}}
        String deduplicationConfig = config.getProperty(DEDUPLICATION_RULE_KEY, AustinConstant.APOLLO_DEFAULT_VALUE_JSON_OBJECT);

        // 去重
        List<Integer> deduplicationList = DeduplicationType.getDeduplicationList();
        for (Integer deduplicationType : deduplicationList) {
            // 组装去重参数

        }
    }

}
