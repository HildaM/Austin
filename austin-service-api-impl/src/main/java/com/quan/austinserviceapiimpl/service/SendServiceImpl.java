package com.quan.austinserviceapiimpl.service;

import com.quan.austincommon.vo.BasicResultVO;
import com.quan.austinserviceapi.domain.SendRequest;
import com.quan.austinserviceapi.domain.SendResponse;
import com.quan.austinserviceapi.service.SendService;
import com.quan.austinserviceapiimpl.domain.SendTaskModel;
import com.quan.austinsupport.pipeline.ProcessContext;
import com.quan.austinsupport.pipeline.ProcessController;
import com.quan.austinsupport.pipeline.ProcessModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SendServiceImpl implements SendService {

    @Autowired
    private ProcessController processController;

    @Override
    public SendResponse send(SendRequest request) {
        // 解包request请求，将其进行再封装
        SendTaskModel sendTaskModel = SendTaskModel.builder()
                .messageTemplateId(request.getMessageTemplateId())
                .messageParamList(Collections.singletonList(request.getMessageParam()))
                .build();

        ProcessContext context = ProcessContext.builder()
                .code(request.getCode())
                .processModel(sendTaskModel)
                .needBreak(false)
                .response(BasicResultVO.success())
                .build();

        // 调用发送处理器处理结果
        ProcessContext process = processController.process(context);

        return new SendResponse(process.getResponse().getStatus(), process.getResponse().getMsg());
    }

    @Override
    public SendResponse batchSend(SendRequest request) {
        return null;
    }
}
