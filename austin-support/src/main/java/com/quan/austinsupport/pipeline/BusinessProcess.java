package com.quan.austinsupport.pipeline;

/*
    业务处理器
 */
public interface BusinessProcess<T extends ProcessModel> {

    void process(ProcessContext<T> context);
}
