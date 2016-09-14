/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;

import com.iotpot.server.pojos.DeviceRegistrationTempAuthToken;
import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.ApplianceEntity;
import com.iotpot.server.entity.ApplianceRegistrationTempAuthTokenEntity;
import com.iotpot.server.pojos.Account;
import com.iotpot.server.pojos.Appliance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public final class DeviceRegistrationTempAuthTokenMapper {

  @Autowired
  private AccountMapper accountMapper;
  @Autowired
  private DeviceMapper deviceMapper;
  @Autowired
  private AccountDao accountDao;
  @Autowired
  private ApplianceDao applianceDao;

  public  List<DeviceRegistrationTempAuthToken>
  mapEntitiesIntoPojos(Iterable<ApplianceRegistrationTempAuthTokenEntity> entities) {
    List<DeviceRegistrationTempAuthToken> pojos = new ArrayList<>();

    entities.forEach(e -> pojos.add(mapEntityIntoPojo(e)));

    return pojos;
  }

  public  List<ApplianceRegistrationTempAuthTokenEntity> mapPojosIntoEntities(Iterable<DeviceRegistrationTempAuthToken> pojos) {

    List<ApplianceRegistrationTempAuthTokenEntity> entities = new ArrayList<>();

    pojos.forEach(e -> entities.add(mapPojoIntoEntity(e)));

    return entities;
  }

  public  DeviceRegistrationTempAuthToken mapEntityIntoPojo(ApplianceRegistrationTempAuthTokenEntity entity) {
    Account account = null;
    Appliance appliance = null;
    if (entity.getAccountEntity() != null) {
      account = accountMapper.mapEntityIntoPojo(entity.getAccountEntity());
    }
    if (entity.getApplianceEntity() != null) {
      appliance = deviceMapper.mapEntityIntoPojo(entity.getApplianceEntity());
    }
    DeviceRegistrationTempAuthToken pojo = new DeviceRegistrationTempAuthToken(
        entity.getId(),
        account,
            appliance,
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getCreatedBy(),
        entity.getUpdatedBy(),
        entity.getName(),
        entity.getTempAuthToken(),
        entity.getExpiry());
    return pojo;
  }

  public ApplianceRegistrationTempAuthTokenEntity mapPojoIntoEntity(DeviceRegistrationTempAuthToken pojo) {
    AccountEntity accountEntity = null;
    ApplianceEntity applianceEntity = null;
    if (pojo.getAccount() != null) {
      accountEntity = accountDao.findOne(pojo.getAccount().getId());
    }
    if (pojo.getAppliance() != null) {

      applianceEntity = applianceDao.findOne(pojo.getAppliance().getId());
    }
    ApplianceRegistrationTempAuthTokenEntity te
        = new ApplianceRegistrationTempAuthTokenEntity(accountEntity,
            applianceEntity,
        pojo.getTempAuthToken());
    return te;
  }

}
