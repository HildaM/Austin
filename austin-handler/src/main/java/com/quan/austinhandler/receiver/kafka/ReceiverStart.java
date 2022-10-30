package com.quan.austinhandler.receiver.kafka;


import com.quan.austinhandler.utils.GroupIdMappingUtils;
import com.quan.austinsupport.constant.MessageQueuePipeline;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListenerAnnotationBeanPostProcessor;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

/*
    启动Kafka消费者
 */
@Service
@ConditionalOnProperty(name = "austin.mq.pipeline", havingValue = MessageQueuePipeline.KAFKA)
@Slf4j
public class ReceiverStart {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ConsumerFactory consumerFactory;

    /**
     * receiver的消费方法常量
     */
    private static final String RECEIVER_METHOD_NAME = "Receiver.consumer";

    // GroupId集合
    private static List<String> groupIds = GroupIdMappingUtils.getAllGroupIds();

    // GroupIds迭代下标
    private static Integer index = 0;

    /*
        针对不同消息类型创建一个Receiver对象
     */
    @PostConstruct
    public void init() {
        for (int i = 0; i < groupIds.size(); i++) {
            context.getBean(Receiver.class);
        }
    }

    /*
        给每个Receiver对象的中标记了 @KafkaListener 的方法
        赋值相应的groupId
     */
    @Bean
    public static KafkaListenerAnnotationBeanPostProcessor.AnnotationEnhancer groupIdEnhancer() {
        return (attrs, element) -> {
            if (element instanceof Method) {
                // 获取当前调用类的类名、调用方法名，并将它们组合
                String name = ((Method) element).getDeclaringClass().getSimpleName() + "." + ((Method) element).getName();
                // 只有当Receiver类中的consumer方法才可以执行
                if (RECEIVER_METHOD_NAME.equals(name)) {
                    // 依次给每一个Receiver中的consumer方法的 @KafkaListener 注入 groupId 注解参数
                    attrs.put("groupId", groupIds.get(index++));
                }
            }
            return attrs;
        };
    }

    /*
        针对Tag进行过滤
     */
    @Bean
    public ConcurrentKafkaListenerContainerFactory filterContainerFactory(@Value("${austin.business.tagId.key}") String tagIdKey,
                                                                          @Value("${austin.business.tagId.value}") String tagIdValue) {
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);
        factory.setAckDiscarded(true);

        factory.setRecordFilterStrategy(consumerRecord -> {
            if (Optional.ofNullable(consumerRecord.value()).isPresent()) {
                for (Header header : consumerRecord.headers()) {
                    if (header.key().equals(tagIdKey) && new String(header.value()).equals(new String(tagIdValue.getBytes(StandardCharsets.UTF_8)))) {
                        return false;
                    }
                }
            }
            // 返回true后会被丢弃
            /*
                只有tag与header的对应才不会被丢弃
             */
            return true;
        });

        return factory;
    }
}
