package ru.yandex.practicum.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

@Configuration
public class RedisConfig {
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisTemplate(LettuceConnectionFactory redisConnectionFactory, ObjectMapper objectMapper) {
        objectMapper.registerModule(new JavaTimeModule());

        RedisSerializationContext<String, Object> serializationContext =
                RedisSerializationContext.<String, Object>newSerializationContext(new StringRedisSerializer())
                        .key(new StringRedisSerializer())
                        .value(new GenericJackson2JsonRedisSerializer(objectMapper))
                        .hashKey(new StringRedisSerializer())
                        .hashValue(new GenericJackson2JsonRedisSerializer(objectMapper))
                        .build();

        return new ReactiveRedisTemplate<>(redisConnectionFactory, serializationContext);
    }
}