package com.demo.redis.config;

import java.time.Duration;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import lombok.RequiredArgsConstructor;

/**
 * cluster mode의 redis 설정
 */
@EnableRedisRepositories
@RequiredArgsConstructor
@Configuration
public class RedisConfig {

	private final RedisClusterProperties redisClusterProperties;

	// redis에 연결하기 위해 RedisConnectionFactory가 필요.
	// RedisCluster, SockedOption, Cluster topology refresh option, cluster client option
	@Bean
	public RedisConnectionFactory redisConnectionFactory() {
		final List<String> nodes = redisClusterProperties.getNodes();
		final String password = redisClusterProperties.getPassword();
		final int maxRedirects = redisClusterProperties.getMaxRedirects();
		final List<RedisNode> redisNodes = nodes.stream()
			.map(node -> new RedisNode(node.split(":")[0], Integer.parseInt(node.split(":")[1])))
			.toList();

		// 1. Redis Cluster 설정
		// 기본적인 Redis Cluster 설정
		// RedisClusterConfiguration 인스턴스 생성하여 redis node들 등록해주고, max-redirects 설정, 보안위해 password 설정
		RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration();
		clusterConfiguration.setClusterNodes(redisNodes);
		clusterConfiguration.setMaxRedirects(maxRedirects);
		// clusterConfiguration.setPassword(password);
		

		// 2. Socket 옵션
		// Lettuce는 Redis 인스턴스와 통신하기 위해 Socket을 활용한다.
		// 이 때 Keep Alive와 connecention Time을 설정해주는 것이 좋다.
		SocketOptions socketOptions = SocketOptions.builder()
			.connectTimeout(Duration.ofMillis(100L)) // 소켓 연결 시간 초과 설정. NW 이슈시 빠른 포기를 통해 연쇄 장애 막음
			.keepAlive(true) //소켓 연결이 일정 시간 동안 사용되지 않더라도 TCP Connection 유지. 주기적으로 패킷 보내 Ack 수신. 일정시간안에 Ack 받지 못하면 종료
			.build();

		// 3. Cluster topology refresh 옵션
		// Redis Cluster는 여러 노드로 구성되고, 노드에 대한 추가 및 삭제, Failover 등의 이벤트가 발생한다.
		// 이 때 포톨로지가 변경되는데, Redis와 연결되는 클라이언트 또한 이 정보를 알기 위해 동기화 해야 한다.
		// ClusterTopologyRefreshOptions는 Redis Cluster의 토폴로지 갱신을 제어하기 위한 설정을 제공
		// Cluster 토폴로지는 노드 구성, 슬롯 할당, 노드 상태 등의 정보를 포함하고,
		// 이를 정기적으로 또는 이벤트 기반으로 갱신하여 클러스터의 최신 상태를 유지
		ClusterTopologyRefreshOptions clusterTopologyRefreshOptions = ClusterTopologyRefreshOptions.builder()
			.dynamicRefreshSources(true)
				// 클러스터의 동적 소스 갱신 활성화
				// 클러스터 노드가 갱신될 때, 새로운 노드가 자동으로 갱신 소스 목록에 추가되며, 삭제된 노드는 목록에서 제거
				// false일 경우 Redis 클라이언트가 seed 노드에만 질의하여 새로운 노드를 찾음
				// 대규모 Redis 클러스터에서는 false 추천
			.enableAllAdaptiveRefreshTriggers()
				// 적응형 갱신 트리거 모두 활성화
				// 트리거는 다음 목록을 포함할 수 있음
				// MOVED_REDIRECT, ASK_REDIRECT, PERSISTENT_RECONNECTS, UNCOVERED_SLOT,  UNKNOWN_NODE
			.enablePeriodicRefresh(Duration.ofMinutes(30L))
				// 30분 마다 Cluster 토폴로지를 업데이트
				// 비활성화 시, 명령을 실행하고 오류가 발생할 때만 업데이트
				// 너무 짧은 주기는 Redis Cluster 전체에 부하를 줄 수 있음
			.build();

		// 4. Cluster Client 옵션
		// 2, 3에서 설정한 옵션을 통해 Client 옵션 추가
		ClientOptions clientOptions = ClusterClientOptions.builder()
			.topologyRefreshOptions(clusterTopologyRefreshOptions)
			.socketOptions(socketOptions)
			.build();

		// 5. Lettuce Client 옵션
		// 4에서 설정한 Client 설정을 활용하여 Lettuce Client 옵션을 설정해주었다.
		// Lettuce 라이브러리는 지연 연결을 사용하기 때문에 Command Timeout 값을 Connection Timeout 값보다 크게 설정해줘야 한다.
		LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
			.clientOptions(clientOptions)
			.commandTimeout(Duration.ofMillis(3000L))
			.build();

		return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
	}
}
