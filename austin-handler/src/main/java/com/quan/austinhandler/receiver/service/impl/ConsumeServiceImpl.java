package com.quan.austinhandler.receiver.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.quan.austincommon.domain.AnchorInfo;
import com.quan.austincommon.domain.LogParam;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.AnchorState;
import com.quan.austinhandler.handler.HandlerHolder;
import com.quan.austinhandler.pending.Task;
import com.quan.austinhandler.pending.TaskPendingHolder;
import com.quan.austinhandler.receiver.service.ConsumeService;
import com.quan.austinhandler.utils.GroupIdMappingUtils;
import com.quan.austinsupport.domain.MessageTemplate;
import com.quan.austinsupport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConsumeServiceImpl implements ConsumeService {

    private static final String LOG_BIZ_TYPE = "Receiver#consumer";
    private static final String LOG_BIZ_RECALL_TYPE = "Receiver#recall";

    @Autowired
    private ApplicationContext context;

    /*
        消息类型路由器
     */
    @Autowired
    private TaskPendingHolder taskPendingHolder;

    @Autowired
    private LogUtils logUtils;

    @Autowired
    private HandlerHolder handlerHolder;

    @Override
    public void consumeAndSend(List<TaskInfo> taskInfos) {
        // 获取对应的topicGroupId
        String topicGroupId = GroupIdMappingUtils.getGroupIdByTaskInfo(CollUtil.getFirst(taskInfos.iterator()));
        for (TaskInfo taskInfo : taskInfos) {
            // 日志
            logUtils.print(
                    LogParam.builder().bizType(LOG_BIZ_TYPE).object(taskInfo).build(),
                    AnchorInfo.builder().ids(taskInfo.getReceiver()).businessId(taskInfo.getBusinessId()).state(AnchorState.RECEIVE.getCode()).build()
            );
            // 从spring容器中取出创建好的bean，而不是重新创建一个，以达到复用的效果
            Task task = context.getBean(Task.class).setTaskInfo(taskInfo);
            // 使用groupId路由到指定的线程池，然后提交任务执行
            taskPendingHolder.route(topicGroupId).execute(task);
        }
    }

    @Override
    public void consumeAndRecall(MessageTemplate messageTemplate) {
        logUtils.print(
                LogParam.builder().bizType(LOG_BIZ_RECALL_TYPE).object(messageTemplate).build()
        );
        handlerHolder.route(messageTemplate.getSendChannel()).recall(messageTemplate);
    }
}
