package com.quan.austinhandler.deduplication.build;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.unit.DataUnit;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.AnchorState;
import com.quan.austincommon.enums.DeduplicationType;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Description:
 * date: 2022/11/13 下午 6:55
 *
 * @author Four
 */
@Service
public class FrequencyDeduplicationBuilder extends AbstractDeduplicationBuilder implements Builder{

    public FrequencyDeduplicationBuilder() {
        this.deduplicationType = DeduplicationType.FREQUENCY.getCode();
    }

    @Override
    public DeduplicationParam build(String deduplication, TaskInfo taskInfo) {
        DeduplicationParam deduplicationParam = getParamsFromConfig(deduplicationType, deduplication, taskInfo);
        if (deduplicationParam == null) {
            return null;
        }
        // 设置过期时间
        deduplicationParam.setDeduplicationTime((DateUtil.endOfDay(new Date()).getTime() - DateUtil.current()) / 1000);
        deduplicationParam.setAnchorState(AnchorState.RULE_DEDUPLICATION);
        return deduplicationParam;
    }
}
