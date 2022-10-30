package com.quan.austinsupport.exceptions;

import com.quan.austincommon.enums.RespStatusEnum;
import com.quan.austinsupport.pipeline.ProcessContext;

public class ProcessException extends RuntimeException{
    // 处理上下文信息
    private final ProcessContext processContext;

    public ProcessException(ProcessContext processContext) {
        super();
        this.processContext = processContext;
    }

    public ProcessException(ProcessContext processContext, Throwable cause) {
        super(cause);
        this.processContext = processContext;
    }

    @Override
    public String getMessage() {
        if (processContext != null) {
            return this.processContext.getResponse().getMsg();
        }
        return RespStatusEnum.CONTEXT_IS_NULL.getMsg();
    }


    public ProcessContext getProcessContext() {
        return processContext;
    }
}
