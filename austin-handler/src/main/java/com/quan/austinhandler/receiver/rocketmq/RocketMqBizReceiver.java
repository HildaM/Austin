package com.quan.austinhandler.receiver.rocketmq;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.receiver.service.ConsumeService;
import com.quan.austinsupport.constant.MessageQueuePipeline;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.annotation.SelectorType;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.ROCKET_MQ)
@RocketMQMessageListener(topic = "${austin.business.topic.name}",
        consumerGroup = "${austin.rocketmq.biz.consumer.group}",
        selectorType = SelectorType.TAG,
        selectorExpression = "${austin.business.tagId.value}"
)
public class RocketMqBizReceiver implements RocketMQListener<String> {

    @Autowired
    private ConsumeService consumeService;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isBlank(message))
            return;
        List<TaskInfo> taskInfos = JSON.parseArray(message, TaskInfo.class);
        consumeService.consumeAndSend(taskInfos);
    }


}
