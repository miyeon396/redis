package com.demo.redis.config;

import java.util.List;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import lombok.Getter;

@Getter
@Setter
@Service
@Configuration
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
public class RedisClusterProperties {
	private String password;
	private int maxRedirects;
	private List<String> nodes;
}
