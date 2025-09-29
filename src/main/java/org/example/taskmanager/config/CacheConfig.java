package org.example.taskmanager.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // Configure ObjectMapper with JavaTimeModule for LocalDateTime support
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        Jackson2JsonRedisSerializer<Object> serializer = new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
        
        // Default cache configuration
        RedisCacheConfiguration defaultCacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Default TTL: 10 minutes
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues();

        // Specific cache configurations with different TTLs
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        
        // Tasks cache - 15 minutes TTL
        cacheConfigurations.put("tasks", defaultCacheConfig.entryTtl(Duration.ofMinutes(15)));
        
        // Users cache - 30 minutes TTL (users change less frequently)
        cacheConfigurations.put("users", defaultCacheConfig.entryTtl(Duration.ofMinutes(30)));
        
        // Notifications cache - 5 minutes TTL (notifications are more dynamic)
        cacheConfigurations.put("notifications", defaultCacheConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
