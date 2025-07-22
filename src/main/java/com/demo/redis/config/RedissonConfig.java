package com.demo.redis.config;

import java.util.List;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class RedissonConfig {
	// Redisson
	// Redis를 좀 더 풍부하게 활용할 수 있게 해주는 Redis Client Library.
	// 단순한 key–value 접근을 넘어, 분산 락, 분산 컬렉션, 메시징(Topics), 스케줄링, 원격 실행 서비스 등
	// 다양한 고수준(High‑Level) API를 제공

	private final RedisClusterProperties redisClusterProperties;

	private static final String REDISSON_PREFIX = "redis://";

	@Bean
	public RedissonClient redissonClient() {
		final Config config = new Config();

		ClusterServersConfig csc = config.useClusterServers()
			.setScanInterval(2000) //Cluster의 토폴로지 스캔 간격을 설정
			.setConnectTimeout(100) //Connection 연결에 대한 타임아웃 설정
			.setTimeout(3000) //Command에 대한 타임아웃 설정
			.setRetryAttempts(3); // 명령 재시도 횟수 설정
			// .setRetryInterval(1500);
		// csc.setRetryDelay(new EqualJitterDelay(Duration.ofSeconds(1), Duration.ofSeconds(2)));

		List<String> nodes = redisClusterProperties.getNodes();
		nodes.forEach(
			node -> csc.addNodeAddress(REDISSON_PREFIX + node));

		return Redisson.create(config);
	}
}
