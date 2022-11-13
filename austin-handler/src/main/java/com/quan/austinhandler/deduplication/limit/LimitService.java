package com.quan.austinhandler.deduplication.limit;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.deduplication.DeduplicationParam;
import com.quan.austinhandler.deduplication.service.AbstractDeduplicationService;

import java.util.Set;

/**
 * Description:
 * date: 2022/11/13 下午 7:01
 *
 * @author Four
 */
public interface LimitService {

    /* 去重限制
    / * @param service 去重器对象
    / * @param taskInfo
    / * @param param 去重参数
    / * @return 返回不符合条件的手机号码
    */
    Set<String> limitFilter(AbstractDeduplicationService service, TaskInfo taskInfo, DeduplicationParam param);

}
