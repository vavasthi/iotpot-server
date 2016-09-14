/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class IoTPotRedisConfiguration {
    private
    @Value("${redis.host}")
    String redisHost;
    private
    @Value("${redis.readreplica}")
    String redisReadReplica;
    private
    @Value("${redis.port}")
    int redisPort;
    @Value("${redis.database}")
    private int redisDatabase;
    @Value("${redis.pool.maxIdle:20}")
    private int maxIdle;
    private
    @Value("${redis.pool.minIdle:5}")
    int minIdle;
    private
    @Value("${redis.pool.maxTotal:2000}")
    int maxTotal;
    private
    @Value("${redis.pool.maxWaitMillis:30000}")
    int maxWaitMillis;
    @Bean
    @Primary
    JedisConnectionFactory jedisConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisHost);
        factory.setPort(redisPort);
        factory.setDatabase(redisDatabase);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxTotal(maxTotal);
        factory.setPoolConfig(poolConfig);
        factory.setUsePool(true);
        return factory;
    }

    @Bean(name = "redisTemplate")
    @Primary
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    JedisConnectionFactory jedisReadReplicaConnectionFactory() {
        JedisConnectionFactory factory = new JedisConnectionFactory();
        factory.setHostName(redisReadReplica);
        factory.setPort(redisPort);
        factory.setDatabase(redisDatabase);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWaitMillis(maxWaitMillis);
        poolConfig.setMaxTotal(maxTotal);
        factory.setPoolConfig(poolConfig);
        factory.setUsePool(true);
        return factory;
    }

    @Bean(name = "redisReadReplicaTemplate")
    public RedisTemplate<Object, Object> redisReadReplicaTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisReadReplicaConnectionFactory());
        return redisTemplate;
    }


}
