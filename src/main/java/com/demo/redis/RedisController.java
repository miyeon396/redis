package com.demo.redis;

import com.demo.redis.Service.CallService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/redis")
public class RedisController {

    private final CallService callService;

    @PostMapping("/set")
    public String set(@RequestParam String key, @RequestParam String value) {
        callService.setValue(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return callService.getValue(key);
    }

    @GetMapping("/cluster/info")
    public ResponseEntity<String> getClusterInfo() {
        String info = callService.getClusterInfo();
        return ResponseEntity.ok(info);
    }

    @GetMapping("/cluster/status")
    public ResponseEntity<Map<String, Object>> getClusterStatus() {
        Map<String, Object> status = new HashMap<>();

        try {
            boolean connected = callService.isClusterConnected();
            String info = callService.getClusterInfo();

            status.put("connected", connected);
            status.put("cluster_info", info);
            status.put("timestamp", System.currentTimeMillis());

        } catch (Exception e) {
            status.put("connected", false);
            status.put("error", e.getMessage());
            status.put("timestamp", System.currentTimeMillis());
        }

        return ResponseEntity.ok(status);
    }

    // Failover 테스트용 엔드포인트
    @PostMapping("/test/failover")
    public ResponseEntity<Map<String, Object>> testFailover() {
        Map<String, Object> result = new HashMap<>();
        String testKey = "failover-test-" + System.currentTimeMillis();
        String testValue = "test-value-" + System.currentTimeMillis();

        try {
            // 1. 값 설정 테스트
            callService.setValue(testKey, testValue);
            result.put("set_result", "success");

            // 2. 값 조회 테스트
            String retrievedValue = callService.getValue(testKey);
            result.put("get_result", retrievedValue != null ? "success" : "failed");
            result.put("retrieved_value", retrievedValue);

            // 3. 클러스터 정보
            String clusterInfo = callService.getClusterInfo();
            result.put("cluster_info", clusterInfo);

            result.put("test_key", testKey);
            result.put("test_value", testValue);
            result.put("overall_status", "success");

        } catch (Exception e) {
            result.put("overall_status", "failed");
            result.put("error", e.getMessage());
        }

        return ResponseEntity.ok(result);
    }
}