/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.cacheloading;

import com.iotpot.server.entity.ApplianceEntity;
import com.iotpot.server.mapper.DeviceMapper;
import com.iotpot.server.mapper.ObjectPatcher;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.SessionDao;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.entity.SessionEntity;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

@Service
public class ApplianceCacheDatabaseLoaderService extends AbstractDatabaseLoaderService {

  @Autowired
  private ApplianceDao applianceDao;

  @Autowired
  private DeviceMapper deviceMapper;

  @Autowired
  private SessionDao sessionDao;

  @Autowired
  private TenantDao tenantDao;

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Appliance loadFromDatabaseByFindOne(UUID id) {

    Appliance appliance = null;
    ApplianceEntity de = applianceDao.findOne(id);
    if(de != null){
      appliance = deviceMapper.mapEntityIntoPojo(de);
    }
    return appliance;
  }
  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Appliance loadFromDatabaseByFindByRegistrationId(String deviceRegistrationId) {

    Appliance appliance = null;
    ApplianceEntity applianceEntity = applianceDao.findByDeviceRegistrationId(deviceRegistrationId);
    if (applianceEntity != null) {
      appliance = deviceMapper.mapEntityIntoPojo(applianceEntity);
    }
    return appliance;
  }
  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Appliance loadFromDatabaseByFindByAuthToken(String authToken) {

    Appliance appliance = null;
    SessionEntity sessionEntity = sessionDao.findByAuthToken(authToken);
    if (sessionEntity != null) {

      ApplianceEntity applianceEntity = applianceDao.findBySessionEntity(sessionEntity);
      if (applianceEntity != null) {
        appliance = deviceMapper.mapEntityIntoPojo(applianceEntity);
      }
    }
    return appliance;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Appliance deleteToken(UUID id) {

    ApplianceEntity applianceEntity = applianceDao.findOne(id);
    applianceEntity.getSessionEntity().setActorEntity(null);
    applianceEntity.setSessionEntity(null);
    applianceEntity = applianceDao.save(applianceEntity);
    return deviceMapper.mapEntityIntoPojo(applianceEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Appliance save(Appliance appliance) {

    applianceDao.delete(appliance.getId());
    return appliance;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Appliance update(UUID id, Appliance appliance) throws InvocationTargetException, IllegalAccessException {

    ApplianceEntity storedEntity = applianceDao.findOne(id);
    ApplianceEntity newEntity = deviceMapper.mapPojoIntoEntity(appliance);
    ObjectPatcher.diffAndPatch(storedEntity, newEntity);
    return appliance;
  }
  @Transactional(propagation = Propagation.REQUIRED)
  public Appliance delete(Appliance appliance) {

    applianceDao.delete(appliance.getId());
    return appliance;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Appliance createSession(Appliance appliance, String remoteAddr, String applicationId)
          throws DatatypeConfigurationException {

    ApplianceEntity applianceEntity = applianceDao.findOne(appliance.getId());
    SessionEntity sessionEntity
            = new SessionEntity(generateDeviceAuthToken(appliance.getDeviceRegistrationId()),
            remoteAddr,
            applicationId,
            applianceEntity,
            Session.SESSION_TYPE.DEVICE_SESSION.getIValue());
    sessionEntity = sessionDao.save(sessionEntity);
    applianceEntity.setSessionEntity(sessionEntity);
    applianceEntity = applianceDao.save(applianceEntity);
    appliance = deviceMapper.mapEntityIntoPojo(applianceEntity);
    return appliance;
  }
}
