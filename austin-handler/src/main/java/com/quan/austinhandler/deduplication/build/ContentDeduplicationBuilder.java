package com.quan.austinhandler.deduplication.build;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.AnchorState;
import com.quan.austincommon.enums.DeduplicationType;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;

/**
 * Description:
 * date: 2022/11/13 下午 6:53
 *
 * @author Four
 */
@Service
public class ContentDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder{

    public ContentDeduplicationBuilder() {
        this.deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if (deduplicationParam == null) {
            return null;
        }
        // 除过期时间之外的参数都设置了
        deduplicationParam.setAnchorState(AnchorState.CONTENT_DEDUPLICATION);
        return deduplicationParam;
    }
}
