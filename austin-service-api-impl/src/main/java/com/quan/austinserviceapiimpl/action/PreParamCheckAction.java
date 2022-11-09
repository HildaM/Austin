package com.quan.austinserviceapiimpl.action;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.quan.austincommon.enums.RespStatusEnum;
import com.quan.austincommon.vo.BasicResultVO;
import com.quan.austinserviceapi.domain.MessageParam;
import com.quan.austinserviceapiimpl.domain.SendTaskModel;
import com.quan.austinsupport.pipeline.BusinessProcess;
import com.quan.austinsupport.pipeline.ProcessContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PreParamCheckAction implements BusinessProcess<SendTaskModel> {

    /**
     * 最大的人数
     * Nacos 配置
     */
    @Value("${batch_receiver_size:10}")
    private Integer BATCH_RECEIVER_SIZE;


    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel model = context.getProcessModel();

        Long messageTemplateId = model.getMessageTemplateId();
        List<MessageParam> messageParamList = model.getMessageParamList();

        // 1. 模板ID为空 || 没有传递参数
        if (messageTemplateId == null || CollUtil.isEmpty(messageParamList)) {
            context
                    .setNeedBreak(true)
                    .setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }


        // 2. 过滤receiver为空的参数
        List<MessageParam> processedList = messageParamList.stream()
                .filter(messageParam -> !StrUtil.isBlank(messageParam.getReceiver()))
                .collect(Collectors.toList());
        if (CollUtil.isEmpty(processedList)) {
            context
                    .setNeedBreak(true)
                    .setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }
        model.setMessageParamList(processedList);


        // 3. 过滤大于BATCH_RECEIVER_SIZE的请求
        if (processedList.stream().anyMatch(messageParam -> messageParam.getReceiver().split(StrUtil.COMMA).length > BATCH_RECEIVER_SIZE)) {
            context
                    .setNeedBreak(true)
                    .setResponse(BasicResultVO.fail(RespStatusEnum.CLIENT_BAD_PARAMETERS));
            return;
        }
    }
}
