package com.example.japanweb.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        var keySerializer = RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer());
        var valueSerializer = RedisSerializationContext.SerializationPair
                .fromSerializer(new JdkSerializationRedisSerializer(getClass().getClassLoader()));

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(keySerializer)
                .serializeValuesWith(valueSerializer)
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(30));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("dashboard_courses", defaultConfig.entryTtl(Duration.ofMinutes(10)));
        cacheConfigurations.put("course_structure_courses", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("course_structure_course_by_id", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("course_structure_chapters", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("course_structure_sections", defaultConfig.entryTtl(Duration.ofMinutes(5)));
        cacheConfigurations.put("course_structure_lessons", defaultConfig.entryTtl(Duration.ofMinutes(5)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
