package com.demo.redis.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.RedisClusterConnection;
import org.springframework.data.redis.connection.RedisClusterNode;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
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

    // 클러스터 상태 확인
    public String getClusterInfo() {
        try {
            return redisTemplate.execute((RedisConnection connection) -> {
                if (connection instanceof RedisClusterConnection) {
                    RedisClusterConnection clusterConn = (RedisClusterConnection) connection;
                    Iterable<RedisClusterNode> nodes = clusterConn.clusterGetNodes();

                    StringBuilder info = new StringBuilder();
                    info.append("Cluster Status: Connected\n");

                    int nodeCount = 0;
                    int masterCount = 0;
                    int slaveCount = 0;

                    for (RedisClusterNode node : nodes) {
                        nodeCount++;
                        if (node.isMaster()) {
                            masterCount++;
                            info.append(String.format("Master: %s:%d (slots: %s)\n",
                                    node.getHost(), node.getPort(),
                                    node.getSlotRange().toString()));
                        } else {
                            slaveCount++;
                            info.append(String.format("Slave: %s:%d (master-id: %s)\n",
                                    node.getHost(), node.getPort(),
                                    node.getMasterId()));
                        }
                    }

                    info.append(String.format("Total nodes: %d (Masters: %d, Slaves: %d)",
                            nodeCount, masterCount, slaveCount));

                    return info.toString();
                } else {
                    return "Connection type: " + connection.getClass().getSimpleName() + " (Not a cluster connection)";
                }
            });
        } catch (Exception e) {
            log.error("Failed to get cluster info: {}", e.getMessage(), e);
            return "Cluster error: " + e.getMessage();
        }
    }

    // 간단한 연결 테스트 메서드 추가
    public boolean isClusterConnected() {
        try {
            redisTemplate.opsForValue().set("connection-test", "ok", Duration.ofSeconds(10));
            String result = redisTemplate.opsForValue().get("connection-test");
            return "ok".equals(result);
        } catch (Exception e) {
            log.error("Connection test failed: {}", e.getMessage());
            return false;
        }
    }
}