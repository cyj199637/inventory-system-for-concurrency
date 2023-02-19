package com.jyj.inventorysystemforconcurrency.redis;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RedisClient {

    private final static String DEFAULT_VALUE = "lock";
    private final static Duration DEFAULT_TIMEOUT = Duration.ofMillis(3_000);

    private final RedisTemplate<String ,String> redisTemplate;

    public Boolean lock(final Long key) {
        return redisTemplate
            .opsForValue()
            .setIfAbsent(generateKey(key), DEFAULT_VALUE, DEFAULT_TIMEOUT);
    }

    public Boolean unlock(final Long key) {
        return redisTemplate.delete(generateKey(key));
    }

    private String generateKey(final Long key) {
        if (key == null) {
            throw new RuntimeException("there is no key");
        }

        return key.toString();
    }
}
