package com.quan.austinserviceapi.controller;


import com.quan.austinserviceapi.domain.SendRequest;
import com.quan.austinserviceapi.domain.SendResponse;
import com.quan.austinserviceapi.service.SendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/service", method = {RequestMethod.GET,RequestMethod.POST})
public class SendController {

    @Autowired(required = false)
    private SendService sendService;

    @PostMapping("/send")
    public SendResponse send(@RequestBody SendRequest sendRequest) {
        return sendService.send(sendRequest);
    }
}
