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
public class H2ODevicesRedisConfiguration {
    private
    @Value("${redis.devices.host:localhost}")
    String redisHost;
    private
    @Value("${redis.devices.readreplica:localhost}")
    String redisReadReplica;
    private
    @Value("${redis.devices.port:6379}")
    int redisPort;
    private
    @Value("${redis.devices.password:redis123}")
    String redisPassword;
    @Value("${redis.devices.database:1}")
    private int redisDatabase;
    @Value("${redis.devices.pool.maxIdle:20}")
    private int maxIdle;
    private
    @Value("${redis.devices.pool.minIdle:5}")
    int minIdle;
    private
    @Value("${redis.devices.pool.maxTotal:2000}")
    int maxTotal;
    private
    @Value("${redis.devices.pool.maxWaitMillis:30000}")
    int maxWaitMillis;
    @Bean
    JedisConnectionFactory jedisDevicesConnectionFactory() {
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

    @Bean(name = "redisDevicesTemplate")
    public RedisTemplate<Object, Object> redisDevicesTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisDevicesConnectionFactory());
        return redisTemplate;
    }

    @Bean
    JedisConnectionFactory jedisReadReplicaDevicesConnectionFactory() {
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

    @Bean(name = "redisDevicesReadReplicaUsersTemplate")
    public RedisTemplate<Object, Object> redisDevicesReadReplicaTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<Object, Object>();
        redisTemplate.setConnectionFactory(jedisReadReplicaDevicesConnectionFactory());
        return redisTemplate;
    }


}
