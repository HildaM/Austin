package com.quan.austinhandler.handler;

import com.quan.austincommon.domain.AnchorInfo;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.enums.AnchorState;
import com.quan.austinsupport.domain.MessageTemplate;
import com.quan.austinsupport.utils.LogUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

/*
    各个渠道的发送Handler基类
 */
public abstract class BaseHandler implements Handler{

    @Autowired
    private HandlerHolder handlerHolder;
    @Autowired
    private LogUtils logUtils;

//    @Autowired
//    private FlowControlFactory flowControlFactory;

    /**
     * 标识渠道的Code
     * 子类初始化的时候指定
     */
    protected Integer channelCode;

    /**
     * 限流相关的参数
     * 子类初始化的时候指定
     */
    //protected FlowControlParam flowControlParam;


    /*
        初始化当前渠道与channelCode的映射
     */
    @PostConstruct
    public void init() {
        handlerHolder.putHandler(channelCode, this);
    }


    /*
        执行发送逻辑
     */
    @Override
    public void doHandler(TaskInfo taskInfo) {
        if (handler(taskInfo)) {
            logUtils.print(
                    AnchorInfo.builder()
                            .state(AnchorState.SEND_SUCCESS.getCode())
                            .businessId(taskInfo.getBusinessId())
                            .ids(taskInfo.getReceiver())
                            .build()
            );
            return;
        }
        logUtils.print(
                AnchorInfo.builder()
                        .state(AnchorState.SEND_FAIL.getCode())
                        .businessId(taskInfo.getBusinessId())
                        .ids(taskInfo.getReceiver())
                        .build()
        );
    }

    // 处理抽象类
    public abstract boolean handler(TaskInfo taskInfo);
}
