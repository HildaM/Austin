package com.quan.austinhandler.deduplication.service;

import cn.hutool.core.collection.CollUtil;
import com.quan.austincommon.domain.AnchorInfo;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.DeduplicationHolder;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import com.quan.austinhandler.deduplication.limit.LimitService;
import com.quan.austinsupport.utils.LogUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.Set;

/**
 * Description:
 * date: 2022/11/13 下午 7:01
 *
 * @author Four
 */

@Slf4j
public abstract class AbstractDeduplicationService implements DeduplicationService {
    protected Integer deduplicationType;

    protected LimitService limitService;

    @Autowired
    private DeduplicationHolder deduplicationHolder;

    @PostConstruct
    public void init() {
        deduplicationHolder.putService(deduplicationType, this);
    }


    @Autowired
    private LogUtils logUtils;

    @Override
    public void deduplication(DeduplicationParam param) {
        TaskInfo taskInfo = param.getTaskInfo();

        Set<String> filterReceivers = limitService.limitFilter(this, taskInfo, param);

        if (CollUtil.isNotEmpty(filterReceivers)) {
            taskInfo.getReceiver().removeAll(filterReceivers);
            logUtils.print(
                    // 输出发送方的消息，记录到日志
                    AnchorInfo.builder()
                            .businessId(taskInfo.getBusinessId())
                            .ids(filterReceivers)
                            .state(param.getAnchorState().getCode())
                            .build());

        }
    }

    /**
     * 构建去重的Key
     *
     * @param taskInfo
     * @param receiver
     * @return
     */
    public abstract String deduplicationSingleKey(TaskInfo taskInfo, String receiver);
}
