package com.quan.austinhandler.deduplication.service;

import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSON;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.DeduplicationType;
import com.quan.austinhandler.deduplication.limit.LimitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Description:  内容去重服务（默认5分钟相同的文案发给相同的用户去重）
 * date: 2022/11/13 下午 7:10
 *
 * @author Four
 */
@Service
public class ContentDeduplicationService extends AbstractDeduplicationService implements DeduplicationService{

    // 注入去重的核心逻辑（服务）
    @Autowired
    public ContentDeduplicationService(@Qualifier("SlideWindowLimitService") LimitService limitService) {
        this.limitService = limitService;
        deduplicationType = DeduplicationType.CONTENT.getCode();
    }

    /**
     * 内容去重 构建key
     * <p>
     * key: md5(templateId + receiver + content)
     * <p>
     * 相同的内容相同的模板短时间内发给同一个人
     *
     * @param taskInfo
     * @return
     */
    @Override
    public String deduplicationSingleKey(TaskInfo taskInfo, String receiver) {
        return DigestUtil.md5Hex(taskInfo.getMessageTemplateId() + receiver
                + JSON.toJSONString(taskInfo.getContentModel()));
    }
}
