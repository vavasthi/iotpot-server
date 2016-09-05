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
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by vinay on 7/27/16.
 */
@Configuration
public class H2OUsersRedisConfiguration {
    private
    @Value("${redis.users.host:localhost}")
    String redisHost;
    private
    @Value("${redis.users.readreplica:localhost}")
    String redisReadReplica;
    private
    @Value("${redis.users.port:6379}")
    int redisPort;
    private
    @Value("${redis.users.password:redis123}")
    String redisPassword;
    @Value("${redis.users.database:1}")
    private int redisDatabase;
    @Value("${redis.users.pool.maxIdle:20}")
    private int maxIdle;
    private
    @Value("${redis.users.pool.minIdle:5}")
    int minIdle;
    private
    @Value("${redis.users.pool.maxTotal:2000}")
    int maxTotal;
    private
    @Value("${redis.users.pool.maxWaitMillis:30000}")
    int maxWaitMillis;
    @Bean
    JedisConnectionFactory jedisUsersConnectionFactory() {
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

    @Bean(name = "redisUsersTemplate")
    public RedisTemplate<Object, Object> redisUsersTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisUsersConnectionFactory());
        return redisTemplate;
    }

    @Bean
    JedisConnectionFactory jedisReadReplicaUsersConnectionFactory() {
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

    @Bean(name = "redisUsersReadReplicaUsersTemplate")
    public RedisTemplate<Object, Object> redisUsersReadReplicaTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisReadReplicaUsersConnectionFactory());
        return redisTemplate;
    }


}
