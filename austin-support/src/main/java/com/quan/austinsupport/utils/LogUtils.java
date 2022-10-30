package com.quan.austinsupport.utils;

import cn.monitor4all.logRecord.bean.LogDTO;
import cn.monitor4all.logRecord.service.CustomLogListener;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.quan.austincommon.domain.AnchorInfo;
import com.quan.austincommon.domain.LogParam;
import com.quan.austinsupport.mq.SendMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
    消息队列日志处理工具
 */
@Slf4j
@Component
public class LogUtils extends CustomLogListener {

    @Autowired
    private SendMqService sendMqService;

    @Value("${austin.business.log.topic.name}")
    private String topicName;


    /*
        方法切面的日志由 @OperationLog 所产生
     */
    @Override
    public void createLog(LogDTO logDTO) throws Exception {
        log.info(JSON.toJSONString(logDTO));
    }

    /*
        记录当前对象的信息
     */
    public void print(LogParam logParam) {
        logParam.setTimestamp(System.currentTimeMillis());
        log.info(JSON.toJSONString(logParam));
    }

    /*
        记录打点信息
     */
    public void print(AnchorInfo anchorInfo) {
        anchorInfo.setTimestamp(System.currentTimeMillis());
        String message = JSON.toJSONString(anchorInfo);
        log.info(message);

        /*
            需要将日志发送到消息队列中，以备日后使用日志收集工具处理
         */
        try {
            sendMqService.send(topicName, message);
        } catch (Exception e) {
            log.error("LogUtils#print send mq fail! e:{},params:{}", Throwables.getStackTraceAsString(e)
                    , JSON.toJSONString(anchorInfo));
        }
    }

    /*
        记录当前对象信息与打点信息
     */
    public void print(LogParam logParam, AnchorInfo anchorInfo) {
        print(logParam);
        print(anchorInfo);
    }
}
