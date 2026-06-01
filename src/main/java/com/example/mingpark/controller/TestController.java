package com.example.mingpark.controller; // 이 부분은 네 화면에 있는 거 그대로 둬!

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController // 스프링아, 이 녀석이 웹 브라우저랑 소통할 컨트롤러야!
public class TestController {

    @GetMapping("/") // 누군가 주소창에 기본 주소("/")만 치고 들어오면 이 코드를 실행해!
    public String hello() {
        return "스프링 부트 세팅!";
    }
}