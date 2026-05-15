package com.flashsale.sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SyncWorkerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SyncWorkerApplication.class, args);
    }
}
