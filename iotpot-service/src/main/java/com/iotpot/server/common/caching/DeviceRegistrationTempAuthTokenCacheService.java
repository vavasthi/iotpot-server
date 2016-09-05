/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;

import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.DeviceRegistrationTempAuthToken;
import com.iotpot.server.common.config.annotations.ORMCache;
import com.iotpot.server.cacheloading.ApplianceRegistrationTempAuthTokenCacheDatabaseLoaderService;
import com.iotpot.server.mapper.DeviceRegistrationTempAuthTokenMapper;
import com.iotpot.server.pojos.Account;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by vinay on 2/22/16.
 */
@Service
@ORMCache(name = IoTPotConstants.IOTPOT_TEMP_AUTH_TOKEN_CACHE_NAME,
        expiry = IoTPotConstants.HALF_HOUR,
        prefix = IoTPotConstants.IOTPOT_TEMP_AUTH_TOKEN_CACHE_PREFIX)
public class DeviceRegistrationTempAuthTokenCacheService extends AbstractGeneralCacheService {

  @Autowired
  private DeviceCacheService deviceCacheService;
  @Autowired
  private DeviceRegistrationTempAuthTokenMapper deviceRegistrationTempAuthTokenMapper;
  @Autowired
  private ApplianceRegistrationTempAuthTokenCacheDatabaseLoaderService applianceRegistrationTempAuthTokenCacheDatabaseLoaderService;

  public DeviceRegistrationTempAuthToken findByTempAuthToken(String tempAuthToken) {
    DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken
        = get(tempAuthToken, DeviceRegistrationTempAuthToken.class);
    if (deviceRegistrationTempAuthToken != null) {

      return deviceRegistrationTempAuthToken;
    }
    return storeToCacheAndReturn(applianceRegistrationTempAuthTokenCacheDatabaseLoaderService
            .loadFromDatabaseByFindByTempAuthToken(tempAuthToken));
  }

  public DeviceRegistrationTempAuthToken save(DeviceRegistrationTempAuthToken pojo){

    return applianceRegistrationTempAuthTokenCacheDatabaseLoaderService.save(pojo);
  }

  public DeviceRegistrationTempAuthToken create(Account account, Appliance appliance){

    return applianceRegistrationTempAuthTokenCacheDatabaseLoaderService.create(account, appliance);
  }

  public DeviceRegistrationTempAuthToken delete(DeviceRegistrationTempAuthToken pojo){

    evictFromCache(pojo);
    return applianceRegistrationTempAuthTokenCacheDatabaseLoaderService.delete(pojo);
  }
  private void evictFromCache(DeviceRegistrationTempAuthToken pojo) {

    List<Object> keys = new ArrayList<>();
    keys.add(pojo.getTempAuthToken());
    deleteObject(keys);
  }

  private void storeToCache(DeviceRegistrationTempAuthToken pojo) {

    Map<Object, Object> keyValuePairs = new HashMap<>();
    keyValuePairs.put(pojo.getTempAuthToken(), pojo);
    storeObject(keyValuePairs);
  }

  private DeviceRegistrationTempAuthToken
  storeToCacheAndReturn(DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken) {

    if (deviceRegistrationTempAuthToken != null) {
      storeToCache(deviceRegistrationTempAuthToken);
    }
    return deviceRegistrationTempAuthToken;
  }

}
