package com.quan.austinhandler.pending;

import com.dtp.core.thread.DtpExecutor;
import com.quan.austinhandler.config.HandlerThreadPoolConfig;
import com.quan.austinhandler.utils.GroupIdMappingUtils;
import com.quan.austinsupport.utils.ThreadPoolUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Component
public class TaskPendingHolder {

    @Autowired
    private ThreadPoolUtils threadPoolUtils;

    private Map<String, ExecutorService> taskPendingHolder = new HashMap<>();

    private static List<String> groupIds = GroupIdMappingUtils.getAllGroupIds();


    /*
        为每种消息类型初始化一个线程池
     */
    @PostConstruct
    public void init() {
        for (String groupId : groupIds) {
            DtpExecutor executor = HandlerThreadPoolConfig.getExecutor(groupId);
            threadPoolUtils.register(executor);
            taskPendingHolder.put(groupId, executor);
        }
    }


    public Executor route(String topicGroupId) {
        return taskPendingHolder.get(topicGroupId);
    }
}
