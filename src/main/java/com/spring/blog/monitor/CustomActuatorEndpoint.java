package com.spring.blog.monitor;

import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Endpoint(id = "customEndpoint")
public class CustomActuatorEndpoint {

    @ReadOperation
    public Object customEndpoint() {
        Map<String, String> map = new HashMap<>();
        map.put("Custom Endpoint", "Null");
        map.put("timestamp", "CurrentTime");
        return map;
    }
}
