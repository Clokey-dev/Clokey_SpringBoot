package com.clokey.server.domain;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
@Service
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    // 예시 매서드를 만들어 놓았습니다.
    // 유저의 validCode를 Redis에 저장
    public String redisString(String validCode) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String key = "validCode:" + validCode;
        operations.set(key, validCode, 1, TimeUnit.DAYS);  // 만료 시간 1일 설정
        String redis = operations.get(key);
        log.info("validCode = {}", redis);
        return redis;
    }

    // 유저의 세션 데이터를 Redis에서 가져오기
    public String getSessionData(String userId) {
        ValueOperations<String, String> operations = redisTemplate.opsForValue();
        String key = "sessionData:" + userId;
        String redis = operations.get(key);
        log.info("Retrieved sessionData for userId = {}: {}", userId, redis);
        return redis;
    }
}
