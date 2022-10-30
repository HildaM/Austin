package com.quan.austinhandler.receiver.service;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinsupport.domain.MessageTemplate;

import java.util.List;

public interface ConsumeService {
    // 从MQ中拉取消息，然后发送消息
    void consumeAndSend(List<TaskInfo> taskInfos);

    // 从MQ中拉去消息，然后撤回消息
    void consumeAndRecall(MessageTemplate messageTemplate);
}
