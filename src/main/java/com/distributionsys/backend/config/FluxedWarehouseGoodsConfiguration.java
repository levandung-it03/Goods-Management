package com.distributionsys.backend.config;

import com.distributionsys.backend.entities.redis.FluxedGoodsFromWarehouse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class FluxedWarehouseGoodsConfiguration {
    @Bean
    ReactiveRedisOperations<String, FluxedGoodsFromWarehouse> redisOperations(ReactiveRedisConnectionFactory factory) {
        var serializer = new Jackson2JsonRedisSerializer<>(FluxedGoodsFromWarehouse.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, FluxedGoodsFromWarehouse> builder =
            RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, FluxedGoodsFromWarehouse> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}