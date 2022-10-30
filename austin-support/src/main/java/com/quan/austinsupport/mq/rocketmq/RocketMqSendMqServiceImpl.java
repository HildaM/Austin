package com.quan.austinsupport.mq.rocketmq;

import cn.hutool.core.util.StrUtil;
import com.quan.austinsupport.constant.MessageQueuePipeline;
import com.quan.austinsupport.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.ROCKET_MQ)
public class RocketMqSendMqServiceImpl implements SendMqService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    public void send(String topic, String jsonValue, String tagId) {
        if (StrUtil.isNotBlank(tagId)) {
            topic = topic + ":" + tagId;
        }
        send(topic, jsonValue);
    }

    @Override
    public void send(String topic, String jsonValue) {
        rocketMQTemplate.send(topic, MessageBuilder.withPayload(jsonValue).build());
    }
}
