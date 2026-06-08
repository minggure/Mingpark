package com.example.mingpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication
public class MingparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingparkApplication.class, args);
    }

}
