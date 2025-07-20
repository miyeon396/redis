package com.demo.redis;

import com.demo.redis.Service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisController {

    private final CallService redisService;

    @PostMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value) {
        redisService.setValue(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return redisService.getValue(key);
    }
}