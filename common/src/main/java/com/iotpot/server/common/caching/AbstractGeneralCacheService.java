/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;


import com.iotpot.server.common.IoTPotRedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AbstractGeneralCacheService extends AbstractCacheService {

  @Autowired
  private IoTPotRedisConfiguration redisConfiguration;
  protected void storeObject(final Map<Object, Object> keyValuePairs) {

    storeObject(redisConfiguration.redisTemplate(), keyValuePairs);
  }
  protected void deleteObject(final List<Object> keys) {

    deleteObject(redisConfiguration.redisTemplate(), keys);
  }
  protected void deleteKey(final Object key) {

    deleteKey(redisConfiguration.redisTemplate(), key);
  }
  protected <T> T get(Object key, Class<T> type) {
    return get(redisConfiguration.redisReadReplicaTemplate(), redisConfiguration.redisTemplate(), key, type);
  }

  protected Boolean setIfAbsent(Object key, Object value) {

    return setIfAbsent(redisConfiguration.redisTemplate(), key, value);
  }
}
