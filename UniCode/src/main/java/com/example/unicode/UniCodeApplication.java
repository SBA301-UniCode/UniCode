package com.example.unicode;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class UniCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(UniCodeApplication.class, args);
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
    }

}
