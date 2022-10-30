package com.quan.austinhandler.receiver.kafka;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson.JSON;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austinhandler.receiver.service.ConsumeService;
import com.quan.austinhandler.utils.GroupIdMappingUtils;
import com.quan.austinsupport.constant.MessageQueuePipeline;
import com.quan.austinsupport.domain.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Scope;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
public class Receiver {
    
    @Autowired
    private ConsumeService consumeService;
    
    /*
        发送消息
     */
    @KafkaListener(topics = "#{'${austin.business.topic.name}'}", containerFactory = "filterContainerFactory")
    public void consumer(ConsumerRecord<?, String> consumerRecord, @Header(KafkaHeaders.GROUP_ID) String topicGroupId) {
        Optional<String> kafkaMessage = Optional.ofNullable(consumerRecord.value());
        if (kafkaMessage.isPresent()) {
            // 获取topicGroup下的所有taskInfo，解封装成List
            List<TaskInfo> taskInfos = JSON.parseArray(kafkaMessage.get(), TaskInfo.class);

            // 获取当前taskInfoList的topicGroupId
            String messageGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfos.iterator()));
            
            // 每个消费者组只消费指定的消息
            if (topicGroupId.equals(messageGroupId)) {
                consumeService.consumeAndSend(taskInfos);
            }
        }
    }
    
    /*
        撤回消息
     */
    @KafkaListener(topics = "#{'${austin.business.recall.topic.name}'}",groupId = "#{'${austin.business.recall.group.name}'}",containerFactory = "filterContainerFactory")
    public void recall(ConsumerRecord<?, String> consumerRecord) {
        Optional<String> message = Optional.ofNullable(consumerRecord.value());
        if (message.isPresent()) {
            MessageTemplate messageTemplate = JSON.parseObject(message.get(), MessageTemplate.class);
            consumeService.consumeAndRecall(messageTemplate);
        }
    }
}
