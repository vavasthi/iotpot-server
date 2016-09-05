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

/**
 * Created by vinay on 7/27/16.
 */
@Configuration
public class H2OAuthFailureRedisConfiguration {
    private
    @Value("${redis.authfailure.host:localhost}")
    String redisHost;
    private
    @Value("${redis.authfailure.readreplica:localhost}")
    String redisReadReplica;
    private
    @Value("${redis.authfailure.port:6379}")
    int redisPort;
    private
    @Value("${redis.authfailure.password:redis123}")
    String redisPassword;
    @Value("${redis.authfailure.database:1}")
    private int redisDatabase;
    @Value("${redis.authfailure.pool.maxIdle:20}")
    private int maxIdle;
    private
    @Value("${redis.authfailure.pool.minIdle:5}")
    int minIdle;
    private
    @Value("${redis.authfailure.pool.maxTotal:2000}")
    int maxTotal;
    private
    @Value("${redis.authfailure.pool.maxWaitMillis:30000}")
    int maxWaitMillis;
    @Bean
    JedisConnectionFactory jedisAuthFailureConnectionFactory() {
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

    @Bean(name = "redisAuthFailureTemplate")
    public RedisTemplate<Object, Object> redisAuthFailureTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisAuthFailureConnectionFactory());
        return redisTemplate;
    }

    @Bean
    JedisConnectionFactory jedisReadReplicaAuthFailureConnectionFactory() {
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

    @Bean(name = "redisAuthFailureReadReplicaTemplate")
    public RedisTemplate<Object, Object> redisAuthFailureReadReplicaTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisReadReplicaAuthFailureConnectionFactory());
        return redisTemplate;
    }


}
