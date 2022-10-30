package com.quan.austinserviceapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum BusinessCode {
    // 普通发送
    COMMON_SEND("send", "普通发送"),

    // 撤回消息
    RECALL("recall", "撤回消息");

    private String code;

    private String description;
}
