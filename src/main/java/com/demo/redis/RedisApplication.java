package com.demo.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

@Slf4j
@SpringBootApplication
public class RedisApplication {

	public static void main(String[] args) {
		SpringApplication.run(RedisApplication.class, args);
	}

	@EventListener(ApplicationReadyEvent.class)
	public void onApplicationReady() {
		log.info("Simple Redis Cluster Application Started");
		log.info("Test endpoints:");
		log.info("- POST /api/redis/set/{{key}} with body: value");
		log.info("- GET /api/redis/get/{{key}}");
		log.info("- GET /api/redis/cluster/info");
		log.info("- POST /api/redis/test/failover");
		log.info("Redis cluster nodes: 127.0.0.1:7000-7005");
	}

}
