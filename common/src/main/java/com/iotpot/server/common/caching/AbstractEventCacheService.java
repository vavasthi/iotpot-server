/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;


import com.iotpot.server.common.H2OEventRedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by insdoddaba on 3/7/16.
 */

@Service
public class AbstractEventCacheService extends AbstractCacheService {

  @Autowired
  private H2OEventRedisConfiguration redisConfiguration;
  protected void storeObject(final Map<Object, Object> keyValuePairs) {

    storeObject(redisConfiguration.redisEventTemplate(), keyValuePairs);
  }
  protected void deleteObject(final List<Object> keys) {

    deleteObject(redisConfiguration.redisEventTemplate(), keys);
  }
  protected void deleteKey(final Object key) {

    deleteKey(redisConfiguration.redisEventTemplate(), key);
  }
  protected <T> T get(Object key, Class<T> type) {
    return get(redisConfiguration.redisEventReadReplicaTemplate(), redisConfiguration.redisEventTemplate(), key, type);
  }

  protected Boolean setIfAbsent(Object key, Object value) {

    return setIfAbsent(redisConfiguration.redisEventTemplate(), key, value);
  }
}
