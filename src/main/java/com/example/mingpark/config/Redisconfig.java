package com.example.mingpark.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 환경설정을 담당하는 클래스
 * @Configuration
 * 적용 시 : 스프링 부트가 실행될 때 이 클래스를 "환경설정 파일" 로 인식하여 최우선으로 메모리에 적용
 * 미적용 시 : 스프링 부트가 실행될 때 이 클래스를 일반 자바 클래스로 취급하여 설정들이 적용되지 않음
 */
@Configuration

public class Redisconfig {
    /**
     *스프링과 Redis 서버와 데이터를 주고 받기위한 핵심 객체 세팅
     * 레디스는 기본적으로 이진 데이터로 저장되지만 개발자가 읽고 디버깅이 쉽게하기 위해 문자열 형태로 저장하게끔 직렬화 설정을 추가함
     * @param connectionFactory application.properties에 적용된 정보를 바탕으로 생성된 Redis 서버 연결
     * @return 직렬화 설정이 완료되어 스프링 컨테이너에 등록될 RedisTemplate 객체
     */
    @Bean //
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory){


        RedisTemplate<String, String> template = new RedisTemplate<>(); //1. Redis 통신을 위한 템플릿 객체 생성
        template.setConnectionFactory(connectionFactory); // 2. 템플릿에 Redis 서버 연결 정보 세팅

        template.setKeySerializer(new StringRedisSerializer()); //직렬화 부분
        template.setValueSerializer(new StringRedisSerializer());

        return template;
    }
}
