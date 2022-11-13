package com.quan.austinhandler.deduplication.build;

import com.alibaba.fastjson.JSONObject;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.DeduplicationHolder;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/**
 * Description:
 * date: 2022/11/13 下午 6:43
 *
 * @author Four
 */
public abstract class AbstractDeduplicationBuilder implements Builder{

    protected Integer deduplicationType;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    @PostConstruct
    public void init() {
        deduplicationHolder.putBuilder(deduplicationType, this);
    }

    public DeduplicationParam getParamsFromConfig(Integer key, String duplicationConfig, TaskInfo taskInfo) {
        JSONObject jsonObject = JSONObject.parseObject(duplicationConfig);
        if (jsonObject == null) {
            return null;
        }

        // 获取指定元素并构造对应的类
        DeduplicationParam deduplicationParam = JSONObject.parseObject(jsonObject.getString(DEDUPLICATION_CONFIG_PRE + key), DeduplicationParam.class);
        if (deduplicationParam == null) {
            return null;
        }
        deduplicationParam.setTaskInfo(taskInfo);
        return deduplicationParam;
    }
}
