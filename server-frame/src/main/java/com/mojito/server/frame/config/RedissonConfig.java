package com.mojito.server.frame.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * @author hw
 * @create 2021/5/16
 */

@Configuration
public class RedissonConfig {


    @Value("${spring.redis.database}")
    private int databaseId;

    @Value("${spring.redis.host}")
    private String hostName;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${spring.redis.timeout}")
    private int timeout;

    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.redis.pool.min-idle}")
    private int minIdle;


    @Value("${spring.redis.pool.max-active}")
    private int maxActive;



    @Bean("redissonClient")
    public RedissonClient createRedissonClient(){
        Config redissonConfig = new Config();
        redissonConfig.useSingleServer().setAddress(buildRedisAddress());
        redissonConfig.useSingleServer().setDatabase(databaseId).setConnectionPoolSize(maxActive).setConnectionMinimumIdleSize(minIdle).setConnectTimeout(timeout);
        if(StringUtils.hasText(password)){
            redissonConfig.useSingleServer().setPassword(password);
        }
        Codec codec = new JsonJacksonCodec();
        redissonConfig.setCodec(codec);
        return Redisson.create(redissonConfig);
    }


    private String buildRedisAddress(){
        StringBuilder stringBuilder = new StringBuilder("redis://");
        stringBuilder.append(hostName).append(":").append(port);
        return stringBuilder.toString();
    }
}
