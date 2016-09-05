/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;

import com.iotpot.server.mapper.DeviceMapper;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.SessionDao;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.common.config.annotations.ORMCache;
import com.iotpot.server.cacheloading.ApplianceCacheDatabaseLoaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.datatype.DatatypeConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Created by vinay on 2/22/16.
 */
@Service
@ORMCache(name = IoTPotConstants.IOTPOT_DEVICE_CACHE_NAME, expiry = IoTPotConstants.SIX_DAYS, prefix = IoTPotConstants.IOTPOT_DEVICE_CACHE_PREFIX)
public class DeviceCacheService extends AbstractGeneralCacheService {

  @Autowired
  private ApplianceDao applianceDao;
  @Autowired
  private SessionDao sessionDao;
  @Autowired
  private DeviceMapper deviceMapper;
  @Autowired
  private ApplianceCacheDatabaseLoaderService applianceCacheDatabaseLoaderService;

  public Set<Appliance> list() {
    Set<Appliance> appliances = deviceMapper.mapEntitiesIntoDTOs(applianceDao.findAll());
    return appliances;
  }

  public Appliance findOne(UUID id) {
    Appliance appliance = get(id, Appliance.class);
    if (appliance == null) {

    }
    return storeToCacheAndReturn(applianceCacheDatabaseLoaderService.loadFromDatabaseByFindOne(id));
  }

  /**
   * This method will return a @Ref Appliance pojo that maps to a given deviceRegistrationId.
   * @param deviceRegistrationId
   * @return
   */
  public Appliance findByDeviceRegistrationId(String deviceRegistrationId) {

    UUID id = get(deviceRegistrationId, UUID.class);
    if (id != null) {
      Appliance appliance = get(id, Appliance.class);
      if (appliance != null) {
        return appliance;
      }
      else {
        deleteKey(deviceRegistrationId);
      }
    }
    return storeToCacheAndReturn(applianceCacheDatabaseLoaderService.loadFromDatabaseByFindByRegistrationId(deviceRegistrationId));
  }

  public Appliance findByAuthToken(String authToken) {

    UUID id = get(authToken, UUID.class);
    if (id != null) {
      Appliance appliance = get(id, Appliance.class);
      if (appliance != null) {
        return appliance;
      }
      else {
        deleteKey(authToken);
      }
    }
    return storeToCacheAndReturn(applianceCacheDatabaseLoaderService.loadFromDatabaseByFindByAuthToken(authToken));

  }

  public Appliance deleteToken(UUID id) {

    Appliance cachedAppliance = findOne(id);
    evictFromCache(cachedAppliance);
    return applianceCacheDatabaseLoaderService.deleteToken(id);
  }

  public Appliance save(Appliance appliance) {

    return applianceCacheDatabaseLoaderService.save(appliance);
  }

  public Appliance update(UUID id, Appliance appliance) throws InvocationTargetException, IllegalAccessException {

    Appliance cachedAppliance = findOne(id);
    if (cachedAppliance != null) {

      evictFromCache(cachedAppliance);
      cachedAppliance = storeToCacheAndReturn(applianceCacheDatabaseLoaderService.update(id, appliance));
    }
    return cachedAppliance;
  }

  public Appliance delete(UUID id) {

    Appliance cachedAppliance = findOne(id);
    if (cachedAppliance != null) {

      evictFromCache(cachedAppliance);
      cachedAppliance =  applianceCacheDatabaseLoaderService.delete(cachedAppliance);
    }
    return cachedAppliance;
  }

  public void evictFromCache(Appliance pojo) {
    List<Object> keys = new ArrayList<>();
    keys.add(pojo.getId());
    if (pojo.getSession() != null) {

      keys.add(pojo.getSession().getAuthToken());
    }
    keys.add(pojo.getDeviceRegistrationId());
    deleteObject(keys);
  }

  private void storeToCache(Appliance pojo) {

    Map<Object, Object> keyValuePairs = new HashMap<>();
    keyValuePairs.put(pojo.getId(), pojo);
    if (pojo.getSession() != null) {

      keyValuePairs.put(pojo.getSession().getAuthToken(), pojo.getId());
    }
    keyValuePairs.put(pojo.getDeviceRegistrationId(), pojo.getId());
    storeObject(keyValuePairs);
  }

  private Appliance storeToCacheAndReturn(Appliance appliance) {

    if (appliance != null) {
      storeToCache(appliance);
    }
    return appliance;
  }

  public Appliance createSession(Appliance appliance, String remoteAddr, String applicationId)
          throws DatatypeConfigurationException {

    evictFromCache(appliance);
    return applianceCacheDatabaseLoaderService.createSession(appliance, remoteAddr, applicationId);
  }
}
