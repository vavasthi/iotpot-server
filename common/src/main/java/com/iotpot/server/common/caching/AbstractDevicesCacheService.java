/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;


import com.iotpot.server.common.H2ODevicesRedisConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by insdoddaba on 3/7/16.
 */

@Service
public class AbstractDevicesCacheService extends AbstractCacheService {

  @Autowired
  private H2ODevicesRedisConfiguration redisConfiguration;
  protected void storeObject(final Map<Object, Object> keyValuePairs) {

    storeObject(redisConfiguration.redisDevicesTemplate(), keyValuePairs);
  }
  protected void deleteObject(final List<Object> keys) {

    deleteObject(redisConfiguration.redisDevicesTemplate(), keys);
  }
  protected void deleteKey(final Object key) {

    deleteKey(redisConfiguration.redisDevicesTemplate(), key);
  }
  protected <T> T get(Object key, Class<T> type) {
    return get(redisConfiguration.redisDevicesReadReplicaTemplate(),
            redisConfiguration.redisDevicesTemplate(), key, type);
  }

  protected Boolean setIfAbsent(Object key, Object value) {

    return setIfAbsent(redisConfiguration.redisDevicesTemplate(), key, value);
  }
}
