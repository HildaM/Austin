package com.quan.austin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Builder
public class MessageParam {
    /*
        接收者：
        可以有多个接收者，用逗号(,)隔开
     */
    private String receiver;

    /*
        可选参数
     */
    private Map<String, String> variables;

    /*
        扩展参数
     */
    private Map<String, String> extendParam;
}
