package com.quan.austinhandler.handler;


import com.quan.austinhandler.receiver.kafka.Receiver;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/*
    channel -> Handler 映射关系
 */
@Component
public class HandlerHolder {

    private Map<Integer, Handler> handlers = new HashMap<>(128);

    public void putHandler(Integer channelCode, Handler handler) {
        handlers.put(channelCode, handler);
    }

    public Handler route(Integer sendChannel) {
        return handlers.get(sendChannel);
    }
}
