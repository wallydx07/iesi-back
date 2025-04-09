package com.example.iesiback;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class IesiBackApplication {
    static{
        OpenCV.loadLocally();
    }
    public static void main(String[] args) {
        SpringApplication.run(IesiBackApplication.class, args);
    }

}
