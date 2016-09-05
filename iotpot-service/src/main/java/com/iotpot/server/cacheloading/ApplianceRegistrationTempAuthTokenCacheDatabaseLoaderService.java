/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.cacheloading;

import com.iotpot.server.mapper.DeviceRegistrationTempAuthTokenMapper;
import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.ApplianceRegistrationTempAuthTokenDao;
import com.iotpot.server.entity.ApplianceRegistrationTempAuthTokenEntity;
import com.iotpot.server.pojos.Account;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.DeviceRegistrationTempAuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Created by vavasthi on 17/4/16.
 */
@Service
public class ApplianceRegistrationTempAuthTokenCacheDatabaseLoaderService extends  AbstractDatabaseLoaderService {

  @Autowired
  private ApplianceRegistrationTempAuthTokenDao applianceRegistrationTempAuthTokenDao;

  @Autowired
  private DeviceRegistrationTempAuthTokenMapper deviceRegistrationTempAuthTokenMapper;

  @Autowired
  private AccountDao accountDao;

  @Autowired
  private ApplianceDao applianceDao;

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public DeviceRegistrationTempAuthToken loadFromDatabaseByFindOne(UUID id) {

    DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken = null;
    ApplianceRegistrationTempAuthTokenEntity de = applianceRegistrationTempAuthTokenDao.findOne(id);
    if(de != null){
      deviceRegistrationTempAuthToken = deviceRegistrationTempAuthTokenMapper.mapEntityIntoPojo(de);
    }
    return deviceRegistrationTempAuthToken;
  }
  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public DeviceRegistrationTempAuthToken loadFromDatabaseByFindByTempAuthToken(String tempAuthToken) {

    DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken = null;
    ApplianceRegistrationTempAuthTokenEntity applianceRegistrationTempAuthTokenEntity
            = applianceRegistrationTempAuthTokenDao.findByTempAuthToken(tempAuthToken);
    if (applianceRegistrationTempAuthTokenEntity != null) {

      deviceRegistrationTempAuthToken
              = deviceRegistrationTempAuthTokenMapper.mapEntityIntoPojo(applianceRegistrationTempAuthTokenEntity);
    }
    return deviceRegistrationTempAuthToken;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public DeviceRegistrationTempAuthToken save(DeviceRegistrationTempAuthToken deviceRegistrationTempAuthToken) {
    ApplianceRegistrationTempAuthTokenEntity applianceRegistrationTempAuthTokenEntity
            = deviceRegistrationTempAuthTokenMapper.mapPojoIntoEntity(deviceRegistrationTempAuthToken);
    applianceRegistrationTempAuthTokenEntity
            = applianceRegistrationTempAuthTokenDao.save(applianceRegistrationTempAuthTokenEntity);
    return deviceRegistrationTempAuthTokenMapper.mapEntityIntoPojo(applianceRegistrationTempAuthTokenEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public DeviceRegistrationTempAuthToken delete(DeviceRegistrationTempAuthToken device) {

    applianceRegistrationTempAuthTokenDao.delete(device.getId());
    return device;
  }

  public DeviceRegistrationTempAuthToken create(Account account, Appliance appliance) {

    String authToken = generateDeviceAuthToken(appliance.getDeviceRegistrationId());
    ApplianceRegistrationTempAuthTokenEntity entity
            = new ApplianceRegistrationTempAuthTokenEntity(accountDao.findOne(account.getId()),
            applianceDao.findOne(appliance.getId()),
            authToken);
    entity = applianceRegistrationTempAuthTokenDao.save(entity);
    return deviceRegistrationTempAuthTokenMapper.mapEntityIntoPojo(entity);
  }
}
