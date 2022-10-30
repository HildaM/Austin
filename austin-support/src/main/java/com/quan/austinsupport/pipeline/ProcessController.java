package com.quan.austinsupport.pipeline;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.quan.austincommon.enums.RespStatusEnum;
import com.quan.austincommon.vo.BasicResultVO;
import com.quan.austinsupport.exceptions.ProcessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class ProcessController {

    // 模板映射map
    private Map<String, ProcessTemplate> templateConfig = null;

    /*
        执行调用链，返回上下文处理结果
     */
    public ProcessContext process(ProcessContext context) {
        // 1. 前置检查
        try {
            preCheck(context);
        } catch (ProcessException e) {
            return e.getProcessContext();
        }

        // 2. 遍历处理步骤
        List<BusinessProcess> processList = templateConfig.get(context.getCode()).getProcessList();
        for (BusinessProcess businessProcess : processList) {
            businessProcess.process(context);
            if (context.getNeedBreak()) break;
        }

        return context;
    }

    /*
        参数前置检查
     */
    private void preCheck(ProcessContext context) {
        // context
        if (context == null) {
            context = new ProcessContext();
            context.setResponse(BasicResultVO.fail(RespStatusEnum.CONTEXT_IS_NULL));
            throw new ProcessException(context);
        }

        // code
        String businessCode = context.getCode();
        if (StrUtil.isBlank(businessCode)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.BUSINESS_CODE_IS_NULL));
            throw new ProcessException(context);
        }

        // template
        ProcessTemplate processTemplate = templateConfig.get(businessCode);
        if (processTemplate == null) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.PROCESS_TEMPLATE_IS_NULL));
            throw new ProcessException(context);
        }

        List<BusinessProcess> processList = processTemplate.getProcessList();
        if (CollUtil.isEmpty(processList)) {
            context.setResponse(BasicResultVO.fail(RespStatusEnum.PROCESS_LIST_IS_NULL));
            throw new ProcessException(context);
        }

    }
}
