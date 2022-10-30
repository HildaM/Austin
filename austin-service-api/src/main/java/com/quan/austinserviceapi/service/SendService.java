package com.quan.austinserviceapi.service;

import com.quan.austinserviceapi.domain.SendRequest;
import com.quan.austinserviceapi.domain.SendResponse;


public interface SendService {

    /*
    单文本发送接口
     */
    SendResponse send(SendRequest request);


    /*
    多文本发送接口
     */
    SendResponse batchSend(SendRequest request);
}
