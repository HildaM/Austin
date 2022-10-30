package com.quan.austinserviceapiimpl.config;


import com.quan.austinserviceapi.enums.BusinessCode;
import com.quan.austinserviceapiimpl.action.AfterParamCheckAction;
import com.quan.austinserviceapiimpl.action.AssembleAction;
import com.quan.austinserviceapiimpl.action.PreParamCheckAction;
import com.quan.austinserviceapiimpl.action.SendMqAction;
import com.quan.austinsupport.pipeline.ProcessController;
import com.quan.austinsupport.pipeline.ProcessTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.*;

/*
    我们需要在service-api中设置pipeline，而不是在support中进行设置
    因为我们希望可以自定义发送流程，所以将设置前移到业务层，而不是服务层
 */


@Configuration
public class PipelineConfig {

    @Autowired
    private PreParamCheckAction preParamCheckAction;
    @Autowired
    private AssembleAction assembleAction;
    @Autowired
    private AfterParamCheckAction afterParamCheckAction;
    @Autowired
    private SendMqAction sendMqAction;


    /*
        流水线控制器，在业务层进行配置，方便针对业务变化进行调整
     */
    @Bean
    public ProcessController processController() {
        ProcessController processController = new ProcessController();
        Map<String, ProcessTemplate> templateConfig = new HashMap<>();
        templateConfig.put(BusinessCode.COMMON_SEND.getCode(), commonSendTemplate());
        templateConfig.put(BusinessCode.RECALL.getCode(), recallMessageTemplate());
        processController.setTemplateConfig(templateConfig);
        return processController;
    }

    /**
     * 普通发送执行流程
     * 1. 前置参数校验
     * 2. 组装参数
     * 3. 后置参数校验
     * 4. 发送消息至MQ
     */
    @Bean("commonSendTemplate")
    public ProcessTemplate commonSendTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(
                preParamCheckAction, assembleAction, afterParamCheckAction, sendMqAction
        ));
        return processTemplate;
    }


    /**
     * 消息撤回执行流程
     * 1.组装参数
     * 2.发送MQ
     */
    @Bean("recallMessageTemplate")
    public ProcessTemplate recallMessageTemplate() {
        ProcessTemplate processTemplate = new ProcessTemplate();
        processTemplate.setProcessList(Arrays.asList(
                assembleAction, sendMqAction
        ));
        return processTemplate;
    }

}
