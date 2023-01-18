package com.spring.blog.monitor;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class DatabaseService implements HealthIndicator {

    private final String DATABASE_SERVICE = "DatabaseService";

    @Override
    public Health getHealth(boolean includeDetails) {
        return HealthIndicator.super.getHealth(includeDetails);
    }

    @Override
    public Health health() {
        if (getDatabaseHealth()) {
            return Health.up()
                         .withDetail(DATABASE_SERVICE, "Service is up and running")
                         .build();
        }
        return Health.down()
                     .withDetail(DATABASE_SERVICE, "Service is down")
                     .build();
    }

    private boolean getDatabaseHealth() {
        return true;
    }
}
