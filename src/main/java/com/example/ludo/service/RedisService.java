package com.example.ludo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final RedisTemplate redisTemplate;
    public void set(String key, Object value, Long ttl) {
        redisTemplate.opsForHash().put("locks", key, value);
    }

    public Object get(String key) {
        return redisTemplate.opsForHash().get("locks", key);
    }

    public void delete(String key) {
        redisTemplate.opsForHash().delete("locks", key);
    }
}
