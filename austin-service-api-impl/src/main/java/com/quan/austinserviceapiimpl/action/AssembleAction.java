package com.quan.austinserviceapiimpl.action;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Throwables;
import com.quan.austincommon.constant.AustinConstant;
import com.quan.austincommon.domain.TaskInfo;
import com.quan.austincommon.dto.model.ContentModel;
import com.quan.austincommon.enums.ChannelType;
import com.quan.austincommon.enums.RespStatusEnum;
import com.quan.austincommon.vo.BasicResultVO;
import com.quan.austinserviceapi.domain.MessageParam;
import com.quan.austinserviceapi.enums.BusinessCode;
import com.quan.austinserviceapiimpl.domain.SendTaskModel;
import com.quan.austinsupport.dao.MessageTemplateDao;
import com.quan.austinsupport.domain.MessageTemplate;
import com.quan.austinsupport.pipeline.BusinessProcess;
import com.quan.austinsupport.pipeline.ProcessContext;
import com.quan.austinsupport.utils.ContentHolderUtil;
import com.quan.austinsupport.utils.TaskInfoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.*;

@Slf4j
@Service
public class AssembleAction implements BusinessProcess<SendTaskModel> {

    @Autowired
    private MessageTemplateDao messageTemplateDao;

    @Override
    public void process(ProcessContext<SendTaskModel> context) {
        SendTaskModel sendTaskModel = context.getProcessModel();
        Long messageTemplateId = sendTaskModel.getMessageTemplateId();

        try {
            Optional<MessageTemplate> messageTemplate = messageTemplateDao.findById(messageTemplateId);

            // 1. 前置检查
            if (!messageTemplate.isPresent() || messageTemplate.get().getIsDeleted().equals(AustinConstant.TRUE)) {
                context
                        .setNeedBreak(true)
                        .setResponse(BasicResultVO.fail(RespStatusEnum.TEMPLATE_NOT_FOUND));
                return;
            }

            // 2. 参数处理
            if (BusinessCode.COMMON_SEND.getCode().equals(context.getCode())) {
                List<TaskInfo> taskInfos = assembleTaskInfo(sendTaskModel, messageTemplate.get());
                sendTaskModel.setTaskInfo(taskInfos);
            } else if (BusinessCode.RECALL.getCode().equals(context.getCode())) {
                sendTaskModel.setMessageTemplate(messageTemplate.get());
            }

        } catch (Exception e) {
            context.setNeedBreak(true).setResponse(BasicResultVO.fail(RespStatusEnum.SERVICE_ERROR));
            log.error("Assemble task fail! template: {}, error: {}", messageTemplateId, Throwables.getStackTraceAsString(e));
        }
    }

    /*
        组装taskinfo任务消息
     */
    private List<TaskInfo> assembleTaskInfo(SendTaskModel sendTaskModel, MessageTemplate messageTemplate) {
        List<MessageParam> messageParamList = sendTaskModel.getMessageParamList();
        List<TaskInfo> taskInfoList = new ArrayList<>();

        for (MessageParam messageParam : messageParamList) {
            TaskInfo taskInfo = TaskInfo.builder()
                    .messageTemplateId(messageTemplate.getId())
                    // 自定义TaskInfoUtils处理
                    .businessId(TaskInfoUtils.generateBusinessId(messageTemplate.getId(), messageTemplate.getTemplateType()))

                    .receiver(new HashSet<>(Arrays.asList(messageParam.getReceiver().split(String.valueOf(StrUtil.C_COMMA)))))
                    .idType(messageTemplate.getIdType())
                    .sendChannel(messageTemplate.getSendChannel())
                    .templateType(messageTemplate.getTemplateType())
                    .msgType(messageTemplate.getMsgType())
                    .shieldType(messageTemplate.getShieldType())
                    .sendAccount(messageTemplate.getSendAccount())

                    // 私有方法getContentModelValue处理
                    .contentModel(getContentModelValue(messageTemplate, messageParam))
                    .build();

            taskInfoList.add(taskInfo);
        }

        return taskInfoList;
    }

    /*
        获取contentModel，替换模板msgContent中的占位符信息
        处理方法：
            通过反射组装动态组装参数
     */
    private static ContentModel getContentModelValue(MessageTemplate messageTemplate, MessageParam messageParam) {
        // 1. 获取真正的ContentModel类型
        Integer sendChannel = messageTemplate.getSendChannel();
        Class contentModelClass = ChannelType.getChannelModelClassByCode(sendChannel);

        // 2. 得到模板的msgContent和入参
        JSONObject jsonObject = JSON.parseObject(messageTemplate.getMsgContent());
        Map<String, String> variables = messageParam.getVariables();

        // 3. 通过反射组装出contentModel。将参数注入到获取到的Model Class中
        Field[] fields = ReflectUtil.getFields(contentModelClass);  // 获取contentModelClass中所有参数
        ContentModel contentModel = (ContentModel) ReflectUtil.newInstance(contentModelClass);  // 创建一个新的空白类
        for (Field field : fields) {
            // 0. MsgContent中 key=field 的value。根据msgContent的定义，value格式为 {$val}
            String originValue = jsonObject.getString(field.getName());

            if (StrUtil.isNotBlank(originValue)) {
                // 1. 将含有占位符格式的originValue字符串，使用variables进行替换，最终结果为resultValue
                String resultValue = ContentHolderUtil.replacePlaceHolder(originValue, variables);
                // 2. 判断resultValue是否一个类，如果是则将其构造出来
                Object resultObj = JSONUtil.isJsonObj(resultValue) ? JSONUtil.toBean(resultValue, field.getType()) : resultValue;
                // 3. 将contentModel中的field参数，设置为resultObj
                ReflectUtil.setFieldValue(contentModel, field, resultObj);
            }
        }

        // 4. 如果url字段存在，则在url拼接对应的埋点参数
        String url = (String) ReflectUtil.getFieldValue(contentModel, "url");
        if (StrUtil.isNotBlank(url)) {
            String resultUrl = TaskInfoUtils.generateUrl(url, messageTemplate.getId(), messageTemplate.getTemplateType());
            ReflectUtil.setFieldValue(contentModel, "url", resultUrl);
        }

        return contentModel;
    }
}
