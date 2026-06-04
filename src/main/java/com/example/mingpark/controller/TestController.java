package com.example.mingpark.controller; // 이 부분은 네 화면에 있는 거 그대로 둬!

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/")
    public String hello() {
        return "스프링 부트 세팅!";
    }
}