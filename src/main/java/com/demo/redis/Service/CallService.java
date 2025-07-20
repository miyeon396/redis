package com.demo.redis.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class CallService {

    private final StringRedisTemplate redisTemplate;

    public void setValue(String key, String value) {
        redisTemplate.opsForValue().set(key, value, Duration.ofMinutes(10));
    }

    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    //Redis 마스터(6379), replica(6380), Sentinel(3개)을 실행
    //
    //애플리케이션 실행 후 setValue, getValue 호출 확인
    //
    //6379 종료 (kill) → Sentinel이 감지하여 6380을 마스터로 승격
    //
    //애플리케이션은 끊김 없이 Lettuce가 자동으로 새 마스터로 reconnect
}