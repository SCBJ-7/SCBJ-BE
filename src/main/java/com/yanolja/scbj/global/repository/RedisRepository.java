package com.yanolja.scbj.global.repository;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    public Boolean lock(final String key) {
        Boolean lock = redisTemplate.opsForValue()
            .setIfAbsent(key, "lock", Duration.ofMillis(1_000));
        return lock;
    }

    public Boolean unlock(final String key) {
        return redisTemplate.delete(key);
    }

}
