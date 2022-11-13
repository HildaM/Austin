package com.quan.austinhandler.deduplication.limit;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.service.AbstractDeduplicationService;

import java.util.ArrayList;
import java.util.List;

/**
 * Description:
 * date: 2022/11/13 下午 7:16
 *
 * @author Four
 */
public abstract class AbstractLimitService implements LimitService{

    // 构筑所有模板的去重key
    protected List<String> deduplicationAllKey(AbstractDeduplicationService service, TaskInfo taskInfo) {
        List<String> keys = new ArrayList<>(taskInfo.getReceiver().size());
        for (String receiver : taskInfo.getReceiver()) {
            String key = service.deduplicationSingleKey(taskInfo, receiver);
            keys.add(key);
        }
        return keys;
    }

    protected String deduplicationSingleKey(AbstractDeduplicationService service, TaskInfo taskInfo, String receiver) {
        return service.deduplicationSingleKey(taskInfo, receiver);
    }
}
