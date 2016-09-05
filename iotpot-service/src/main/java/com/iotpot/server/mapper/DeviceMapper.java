/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;


import com.iotpot.server.dao.AccountDao;
import com.iotpot.server.dao.RoleDao;
import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.ApplianceEntity;
import com.iotpot.server.entity.RoleEntity;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.IoTPotRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by vinay on 1/8/16.
 */
@Service
public final class DeviceMapper {

  @Autowired
  private RoleDao roleDao;
  @Autowired
  private AccountDao accountDao;
  @Autowired
  private SessionMapper sessionMapper;

  public Set<Appliance> mapEntitiesIntoDTOs(Iterable<ApplianceEntity> entities) {
    Set<Appliance> dtos = new HashSet<>();

    entities.forEach(e -> dtos.add(mapEntityIntoPojo(e)));

    return dtos;
  }

  public  Set<ApplianceEntity> mapPojosIntoEntities(Iterable<Appliance> pojos) {
    Set<ApplianceEntity> entities = new HashSet<>();

    pojos.forEach(e -> entities.add(mapPojoIntoEntity(e)));

    return entities;
  }

  public Appliance mapEntityIntoPojo(ApplianceEntity entity) {
    UUID accountId = null;
    if (entity.getAccountEntity() != null) {
      accountId = entity.getAccountEntity().getId();
    }
    Appliance pojo = new Appliance(entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getCreatedBy(),
        entity.getUpdatedBy(),
        entity.getDeviceId(),
        RoleMapper.mapEntitiesIntoDTOs(entity.getRoles()));
    pojo.setAccountId(accountId);
    if (entity.getSessionEntity() != null) {

      pojo.setSession(sessionMapper.mapEntityIntoPojo(entity.getSessionEntity()));
    }
    return pojo;
  }

  public ApplianceEntity mapPojoIntoEntity(Appliance pojo) {

    Set<RoleEntity> roleEntities = new HashSet<>();
    for (IoTPotRole r : pojo.getIoTPotRoles()) {
      Set<RoleEntity> roleEntityList = roleDao.findByRole(r.getAuthority());
      if (roleEntityList != null && roleEntityList.size() > 0) {
        roleEntities.add(roleEntityList.iterator().next());
      }
    }
    AccountEntity accountEntity = accountDao.findOne(pojo.getAccountId());
    ApplianceEntity de  = new ApplianceEntity(pojo.getDeviceRegistrationId(), accountEntity);
    de.setRoles(roleEntities);
    return de;
  }
}
