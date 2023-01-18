package com.spring.blog.monitor;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class LoggerService implements HealthIndicator {

    private final String LOGGER_SERVICE = "LoggerService";

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        if (getLoggerHealth()) {
            return Health.up()
                         .withDetail(LOGGER_SERVICE, "Service is up and running")
                         .build();
        }
        return Health.down()
                     .withDetail(LOGGER_SERVICE, "Service is down")
                     .build();
    }

    private boolean getLoggerHealth() {
        return true;
    }
}
