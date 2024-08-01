package com.example.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Getter
@Setter
/*
    Redis:
    개요:
        Remote Dictionary Server의 약자. key-value의 비정형 데이터를 관리함.
        비관계형 DB. NoSQL.
        모든 데이터를 메모리에 저장하는 In-Memory DB이며 속도가 매우 "빠르다".

    장점:
        영속성 지원:
            서버가 내려가도 DISK에 저장된 데이터(RDB, AOF 방식)를 읽어 MEMORY에 로딩. - 백업
            RDB: Snapshooting 방식. 순간적으로 데이터 전체를 MEMORY -> DISK 이동. 특정 시간 마다 저장.
            AOF: Append On File. SAVE연산을 모두 log 파일에 기록. 초 단위로 이벤트를 로그에 저장.
                즉, RDB는 속도와 파일 크기, AOF는 데이터 유지에서 유리함: 목적이 캐시면 RDB, 모든 데이터 유지면 AOF, 내구성이면 RDB+AOF.
                일반적으로 일정 시간마다 RDB 스냅샷 저장, 이후 변경사항은 AOF 백업하는 방식을 사용함.
        Read 성능 증대 위한 서버 측 복제 지원
        Create 증대를 위한 클라이언트 측 Sharding
        다양한 데이터형(문자열, 리스트, 해시, 셋) 지원

    단점:
        데이터를 메모리에 저장하므로 "공간 제약"이 있어 보조 데이터 저장소로 사용(클러스터 기능으로 저장공간 확장).


    문법:
        keys * : DB내 모든 key, key-value 출력
        get <key> : 해당 key의 value 출력
        smembers set:<key> : set 자료구조 출력
        zranange <key>
        hgetall <key> : hash 자료구조 출력

        set <key> <value> : key-value 저장
        sadd set<key> <value> : set 자료 구조로 저장. 순서 없이 단순히 모아둠.
        zadd <key> <int> <value> : 값에 가중치(int)를 부여함.
        hset <key> <map-key> <map-value> : 해시 map 구조로 저장.

        append <key> <value> : 저장했던 key-value의 value 뒤에 문자열을 추가함
    https://redis.io/
 */
// Redis host와 port번호 지정 + 레디스 형식인 template 설정
//cli에서 keys * 이나 get <key>로 redis db 확인 가능.
public class RedisConfig {
    private String host;
    private int port;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());

        // 일반적인 key:value의 경우 시리얼라이저
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new StringRedisSerializer());

        // Hash를 사용할 경우 시리얼라이저
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new StringRedisSerializer());

        // 모든 경우
        redisTemplate.setDefaultSerializer(new StringRedisSerializer());

        return redisTemplate;
    }
}