package com.markerhub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


//重新自定义Redis的序列化方式
@Configuration
public class RedisConfig {

    @Bean
    RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(redisConnectionFactory);  // 连接redis连接池

        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
            jackson2JsonRedisSerializer.setObjectMapper(new ObjectMapper());  //设置value值的 类型

        redisTemplate.setKeySerializer(new StringRedisSerializer());       //  设置key值类型为字符串格式
        redisTemplate.setValueSerializer(new StringRedisSerializer());     // 设置value值类型为json格式

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());   //  设置HashKey值类型为字符串格式
        redisTemplate.setHashValueSerializer(new StringRedisSerializer()); //设置HashValue值类型为json格式

        return redisTemplate;
    }
}
