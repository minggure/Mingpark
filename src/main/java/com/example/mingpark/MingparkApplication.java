package com.example.mingpark;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;

@SpringBootApplication // 🎯 괄호 안에 있던 exclude 어쩌구를 싹 지워버려!
public class MingparkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MingparkApplication.class, args);
    }
}