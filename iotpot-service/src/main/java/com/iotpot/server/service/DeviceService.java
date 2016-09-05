/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.service;

import com.iotpot.server.common.caching.AccountCacheService;
import com.iotpot.server.mapper.DeviceMapper;
import com.iotpot.server.dao.SessionDao;
import com.iotpot.server.common.caching.AbstractCacheService;
import com.iotpot.server.common.caching.DeviceCacheService;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.pojos.Appliance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vinay on 2/22/16.
 */
@Service
public class DeviceService extends AbstractCacheService {

  @Autowired
  private DeviceCacheService deviceCacheService;
  @Autowired
  private AccountCacheService accountCacheService;
  @Autowired
  private ApplianceDao applianceDao;
  @Autowired
  private SessionDao sessionDao;
  @Autowired
  private DeviceMapper deviceMapper;

  public Set<Appliance> list() {
    return deviceCacheService.list();
  }

  public Appliance findOne(UUID id) {
    return deviceCacheService.findOne(id);
  }

  /**
   * This method will return a @Ref Appliance pojo that maps to a given deviceRegistrationId.
   * @param deviceRegistrationId
   * @return
   */
  public Appliance findByDeviceRegistrationId(String deviceRegistrationId) {

    return deviceCacheService.findByDeviceRegistrationId(deviceRegistrationId);
  }

  public Appliance findByAuthToken(String authToken) {

    return findByAuthToken(authToken);
  }

  public Appliance deleteToken(UUID id) {
    return deviceCacheService.deleteToken(id);
  }

  public Appliance save(Appliance appliance) {
    return deviceCacheService.save(appliance);
  }

  public Appliance update(UUID id, Appliance appliance) throws InvocationTargetException, IllegalAccessException {
    return deviceCacheService.update(id, appliance);
  }

  /**
   * This method would delete the device. If the device was an adopted device, the association needs to be removed.
   * @param id
   * @return
   */
  public Appliance delete(UUID id) {
    Appliance appliance = deviceCacheService.findOne(id);
    if (appliance != null) {

      if (appliance.getAccountId() != null) {
        accountCacheService.unadoptDevice(appliance.getAccountId(), appliance);
      }
      return deviceCacheService.delete(id);
    }
    return null;
  }
}
