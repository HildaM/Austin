package com.quan.austinhandler.discard;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.quan.austincommon.constant.AustinConstant;
import com.quan.austincommon.domain.AnchorInfo;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.AnchorState;
import com.quan.austinsupport.service.ConfigService;
import com.quan.austinsupport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Description:
 * date: 2022/11/09 下午 5:10
 *
 * @author Four
 */
@Service
public class DiscardMessageService {
    public static final String DISCARD_MESSAGE_KEY = "discardMsgIds";

    @Autowired
    private ConfigService config;

    @Autowired
    private LogUtils logUtils;

    /**
     * 丢弃消息，配置在apollo
     * @param taskInfo
     * @return
     */
    public boolean isDiscard(TaskInfo taskInfo) {
        // 配置示例:	["1","2"]
        // 从nacos配置中心获取配置
        JSONArray array = JSON.parseArray(config.getProperty(DISCARD_MESSAGE_KEY,
                AustinConstant.APOLLO_DEFAULT_VALUE_JSON_ARRAY));

        if (array.contains(String.valueOf(taskInfo.getMessageTemplateId()))) {
            logUtils.print(AnchorInfo.builder().businessId(taskInfo.getBusinessId()).ids(taskInfo.getReceiver()).state(AnchorState.DISCARD.getCode()).build());
            return true;
        }
        return false;
    }
}
