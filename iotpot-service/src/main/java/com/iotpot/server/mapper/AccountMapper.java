/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;

import com.iotpot.server.dao.DataCenterDao;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.RoleDao;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.entity.RoleEntity;
import com.iotpot.server.entity.TenantEntity;
import com.iotpot.server.common.exception.EncryptionException;
import com.iotpot.server.pojos.Account;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.IoTPotRole;
import com.iotpot.server.pojos.Session;
import org.apache.log4j.Logger;
import org.apache.log4j.Level;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by vinay on 1/8/16.
 */
@Service
public final class AccountMapper {

  Logger logger = Logger.getLogger(AccountMapper.class);

  private final TenantDao tenantDao;
  private final DataCenterDao dataCenterDao;
  private final RoleDao roleDao;
  private final ApplianceDao applianceDao;
  private final TenantMapper tenantMapper;
  private final SessionMapper sessionMapper;
  private final DeviceMapper deviceMapper;

  @Autowired
  public AccountMapper(TenantDao tenantDao,
                       DataCenterDao dataCenterDao,
                       RoleDao roleDao,
                       ApplianceDao applianceDao,
                       TenantMapper tenantMapper,
                       SessionMapper sessionMapper,
                       DeviceMapper deviceMapper) {

    this.tenantDao = tenantDao;
    this.dataCenterDao = dataCenterDao;
    this.roleDao = roleDao;
    this.applianceDao = applianceDao;
    this.tenantMapper = tenantMapper;
    this.sessionMapper = sessionMapper;
    this.deviceMapper = deviceMapper;
  }
  public List<Account> mapEntitiesIntoPojos(Iterable<AccountEntity> entities) {
    List<Account> pojos = new ArrayList<>();

    entities.forEach(e -> pojos.add(mapEntityIntoPojo(e)));

    return pojos;
  }

  public List<AccountEntity> mapPojosIntoEntities(Iterable<Account> pojos) {
    List<AccountEntity> entities = new ArrayList<>();

    pojos.forEach(e -> {

              try {
                entities.add(mapPojoIntoEntity(e));

              } catch (NoSuchPaddingException
                      | UnsupportedEncodingException
                      | IllegalBlockSizeException
                      | BadPaddingException
                      | NoSuchAlgorithmException
                      | InvalidParameterSpecException
                      | InvalidKeyException
                      | InvalidKeySpecException ex) {
                logger.log(Level.ERROR, "Could not convert POJOs to entity.", ex);
                throw new EncryptionException("Could not convert POJOs to entity");
              }
            }
    );

    return entities;
  }

  public Account mapEntityIntoPojo(AccountEntity entity) {
    if (entity.getRemoteAddresses() != null) {

      entity.getRemoteAddresses().size();
    }
    if (entity.getRoles() != null) {

      entity.getRoles().size();
    }
    Map<String, Session> sessionMap = null;
    if (entity.getSessionMap() != null) {
      sessionMap = entity
              .getSessionMap()
              .entrySet()
              .stream()
              .collect(Collectors.toMap(e -> e.getKey(), e -> sessionMapper.mapEntityIntoPojo(e.getValue())));
    }
    Set<IoTPotRole> ioTPotRoles = null;
    if (entity.getRoles() != null && entity.getRoles().size() > 0) {
      ioTPotRoles = RoleMapper.mapEntitiesIntoDTOs(entity.getRoles());
    }
    Set<Appliance> appliances = null;
    if (entity.getDevices() != null && entity.getDevices().size() > 0) {
      appliances = deviceMapper.mapEntitiesIntoDTOs(entity.getDevices());
    }
    Account pojo = new Account(
            entity.getId(),
            tenantMapper.mapEntityIntoPojo(entity.getTenant()),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedBy(),
            entity.getName(),
            entity.getEmail(),
            entity.getPassword(),
            sessionMap,
            entity.getRemoteAddresses(),
            ioTPotRoles,
            appliances,
            ComputeRegionMapper.mapEntityIntoPojo(entity.getDataCenterEntity()));
    return pojo;
  }

  public AccountEntity mapPojoIntoEntity(Account pojo) throws NoSuchPaddingException, UnsupportedEncodingException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, InvalidParameterSpecException, InvalidKeyException, InvalidKeySpecException {
    TenantEntity tenantEntity = tenantDao.findOne(pojo.getTenant().getId());
    DataCenterEntity dataCenterEntity = null;
    if (pojo.getDataCenter() != null) {

      dataCenterEntity = dataCenterDao.findOne(pojo.getDataCenter().getId());
    }
    Set<RoleEntity> roleEntityList = new HashSet<>();
    for (IoTPotRole ioTPotRole : pojo.getIoTPotRoles()) {
      Set<RoleEntity> rel = roleDao.findByRole(ioTPotRole.getAuthority());
      if (rel != null && rel.size() > 0) {

        roleEntityList.add(rel.iterator().next());
      }
      else {
        RoleEntity roleEntity = new RoleEntity(ioTPotRole.getAuthority());
        roleEntity = roleDao.save(roleEntity);
        roleEntityList.add(roleEntity);
      }
    }
    AccountEntity ae = new AccountEntity(tenantEntity, pojo.getName(), pojo.getEmail(),
            pojo.getPassword(),
            pojo.getRemoteAddresses(),
            roleEntityList,
            dataCenterEntity);
    return ae;
  }

}
