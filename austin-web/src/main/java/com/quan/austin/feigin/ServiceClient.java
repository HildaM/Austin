package com.quan.austin.feigin;

import com.quan.austin.dto.SendRequest;
import com.quan.austin.dto.SendResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@FeignClient(value = "austin-service", configuration = FeignOkhttpConfig.class)
public interface ServiceClient {


    @PostMapping("/service/send")
    // 发送单条消息
    SendResponse send(@RequestBody SendRequest sendRequest);


    @PostMapping("/service/batchsend")
    // 发送多条消息
    SendResponse batchSend(@RequestBody SendRequest request);
}
