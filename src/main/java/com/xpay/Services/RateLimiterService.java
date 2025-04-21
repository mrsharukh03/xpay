package com.xpay.Services;
import com.xpay.Anotations.RateLimitResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class RateLimiterService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public RateLimitResponse isAllowed(String key, int limit, int windowInSeconds) {
        String redisKey = "ratelimit:" + key;
        Long count = redisTemplate.opsForValue().increment(redisKey);

        if (count == 1) {
            redisTemplate.expire(redisKey, Duration.ofSeconds(windowInSeconds));
        }

        boolean allowed = count <= limit;
        Long ttl = redisTemplate.getExpire(redisKey); // in seconds

        return new RateLimitResponse(allowed, ttl != null ? ttl : 0);
    }

}
