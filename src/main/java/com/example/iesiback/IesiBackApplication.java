package com.example.iesiback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IesiBackApplication {
    public static void main(String[] args) {
        SpringApplication.run(IesiBackApplication.class, args);
    }
}
