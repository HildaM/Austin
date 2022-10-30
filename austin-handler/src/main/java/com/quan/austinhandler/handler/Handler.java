package com.quan.austinhandler.handler;

import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinsupport.domain.MessageTemplate;

public interface Handler {

    /**
     * 处理器
     *
     * @param taskInfo
     */
    void doHandler(TaskInfo taskInfo);

    /**
     * 撤回消息
     *
     * @param messageTemplate
     * @return
     */
    void recall(MessageTemplate messageTemplate);
}
