package com.xpay.AppConfig;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(org.springframework.data.redis.connection.RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Key serialization: StringRedisSerializer
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Value serialization: GenericJackson2JsonRedisSerializer
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        // Optional: Hash serialization (if using hashes in Redis)
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager(org.springframework.data.redis.connection.RedisConnectionFactory connectionFactory) {
        Map<String, RedisCacheConfiguration> cacheConfigs = new HashMap<>();
        cacheConfigs.put("allAdmins", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(20)));
        cacheConfigs.put("clients", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(20)));
        cacheConfigs.put("clientTransactions", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)));
   cacheConfigs.put("clientBalance", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(3)));
   cacheConfigs.put("walletBalance", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(3)));
cacheConfigs.put("userTransactions", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5)));

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .disableCachingNullValues()
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigs)
                .build();
    }

}
