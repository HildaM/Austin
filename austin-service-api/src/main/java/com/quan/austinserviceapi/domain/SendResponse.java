package com.quan.austinserviceapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain = true)
public class SendResponse {
    /*
        响应状态
     */
    private String code;

    /*
        响应信息
     */
    private String msg;
}
