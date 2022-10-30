package com.quan.austinsupport.pipeline;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
    责任链执行模板（将处理流程类串联起来）
 */

public class ProcessTemplate {
    private List<BusinessProcess> processList;

    public List<BusinessProcess> getProcessList() {
        return processList;
    }
    public void setProcessList(List<BusinessProcess> processList) {
        this.processList = processList;
    }
}
