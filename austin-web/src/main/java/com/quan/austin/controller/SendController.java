package com.quan.austin.controller;

import com.quan.austin.dto.SendRequest;
import com.quan.austin.dto.SendResponse;
import com.quan.austin.feigin.ServiceClient;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(tags = "发送接口")
@RestController
@RequestMapping(value = "/web", method = {RequestMethod.GET,RequestMethod.POST})
public class SendController {

    @Autowired
    private ServiceClient serviceClient;

    @ApiOperation(value = "send")
    @PostMapping("/send")
    public SendResponse send(@RequestBody SendRequest sendRequest) {
        return serviceClient.send(sendRequest);
    }
}
