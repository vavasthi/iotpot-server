/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.cacheloading;

import com.iotpot.server.mapper.ObjectPatcher;
import com.iotpot.server.mapper.TenantMapper;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.entity.TenantEntity;
import com.iotpot.server.pojos.Tenant;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

@Service
public class TenantCacheDatabaseLoaderService {

  @Autowired
  private TenantDao tenantDao;

  @Autowired
  private TenantMapper tenantMapper;

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Tenant loadFromDatabaseByFindOne(UUID id) {

    Tenant tenant = null;
    TenantEntity te = tenantDao.findOne(id);
    if(te != null){
      tenant = tenantMapper.mapEntityIntoPojo(te);
    }
    return tenant;
  }
  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Tenant loadFromDatabaseByFindByDiscriminator(String discriminator) {

    Tenant tenant = null;
    TenantEntity tenantEntity = tenantDao.findByDiscriminator(discriminator);
    if (tenantEntity != null) {

      tenant =  tenantMapper.mapEntityIntoPojo(tenantEntity);
    }
    return tenant;
  }

  private String generateAuthToken(String tenant, String username) {
    UUID uuid1 = UUID.randomUUID();
    UUID uuid2 = UUID.randomUUID();

    String token = Long.toHexString(uuid1.getLeastSignificantBits()) +
            Long.toHexString(uuid1.getMostSignificantBits()) +
            Hex.encodeHexString(tenant.getBytes()) +
            Long.toHexString(uuid2.getLeastSignificantBits()) +
            Long.toHexString(uuid2.getMostSignificantBits()) +
            Hex.encodeHexString(username.getBytes());
    return token;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Tenant save(Tenant tenant) {

    TenantEntity tenantEntity = tenantMapper.mapPojoIntoEntity(tenant);
    tenantEntity = tenantDao.save(tenantEntity);
    return tenantMapper.mapEntityIntoPojo(tenantEntity);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  public Tenant update(UUID id, Tenant tenant) throws InvocationTargetException, IllegalAccessException {

    TenantEntity storedEntity = tenantDao.findOne(id);
    TenantEntity newEntity = tenantMapper.mapPojoIntoEntity(tenant);
    ObjectPatcher.diffAndPatch(storedEntity, newEntity);
    return tenantMapper.mapEntityIntoPojo(storedEntity);
  }
}
