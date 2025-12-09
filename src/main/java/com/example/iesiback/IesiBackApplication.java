package com.example.iesiback;

import nu.pattern.OpenCV;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class IesiBackApplication {

//    static{
//        OpenCV.loadLocally();
//    }

    public static void main(String[] args) {
        SpringApplication.run(IesiBackApplication.class, args);
    }

}
