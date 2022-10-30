package com.quan.austinserviceapi.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/*
    发送信息请求
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class SendRequest {
    /*
        执行业务类型
        send:   发送消息
        recall: 撤回消息
     */
    private String code;

    /*
        消息模板ID
     */
    private Long messageTemplateId;

    /*
        消息模板参数
     */
    private MessageParam messageParam;
}
