package com.yanolja.scbj.global.config.fcm;

import com.yanolja.scbj.global.config.fcm.FCMRequest.Data;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class FCMTokenRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    private final String DEVICE_TOKEN_PREFIX = "DEVICE: ";

    public FCMTokenRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String email, String token) {
        redisTemplate.opsForValue()
            .set(DEVICE_TOKEN_PREFIX + email, token);
    }

    public String getToken(String email) {
        return (String) redisTemplate.opsForValue().get(DEVICE_TOKEN_PREFIX + email);
    }

    public void deleteToken(String email) {
        redisTemplate.delete(email);
    }

    public boolean hasKey(String email) {
        return redisTemplate.hasKey(email);
    }
}