package com.demo.redis.config;

import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
public class LettuceConfig {

//    @Value("${spring.redis.cluster.nodes}")
//    private List<String> clusterNodes;
//
//    @Value("${spring.redis.cluster.max-redirects:3}")
//    private int maxRedirects;
//
//    @Value("${spring.redis.timeout:3000}")
//    private int timeout;

    @Bean
    public LettuceConnectionFactory lettuceConnectionFactory() {
        RedisClusterConfiguration clusterConfig = new RedisClusterConfiguration();
        clusterConfig.clusterNode("127.0.0.1", 7000);
        clusterConfig.clusterNode("127.0.0.1", 7001);
        clusterConfig.clusterNode("127.0.0.1", 7002);
        clusterConfig.clusterNode("127.0.0.1", 7003);
        clusterConfig.clusterNode("127.0.0.1", 7004);
        clusterConfig.clusterNode("127.0.0.1", 7005);
        clusterConfig.setMaxRedirects(3);

        // 🧠 클러스터 토폴로지 리프레시 옵션
        ClusterTopologyRefreshOptions topologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
//                .enablePeriodicRefresh(Duration.ofSeconds(10)) // 5초마다 토폴로지 새로고침
                .enableAllAdaptiveRefreshTriggers()           // 장애 발생 시에도 리프레시 트리거
                .build();

        // 🧠 클러스터 클라이언트 옵션
        ClusterClientOptions clientOptions = ClusterClientOptions.builder()
                .topologyRefreshOptions(topologyRefreshOptions)
                .build();

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofMillis(3000))
                .clientOptions(clientOptions)
                .build();

        return new LettuceConnectionFactory(clusterConfig, clientConfig);
    }



    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        return template;
    }


}

