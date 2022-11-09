package com.quan.austinhandler.deduplication.service;

import com.quan.austinhandler.deduplication.DeduplicationParam;

/**
 * Description:
 * date: 2022/11/09 下午 8:33
 *
 * @author Four
 */
public interface DeduplicationService {
    // 去重逻辑
    void deduplication(DeduplicationParam param);
}
