/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.common.caching;

import com.iotpot.server.cacheloading.TenantCacheDatabaseLoaderService;
import com.iotpot.server.mapper.TenantMapper;
import com.iotpot.server.dao.TenantDao;
import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.pojos.Tenant;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.common.config.annotations.IoTPotCache;
import com.iotpot.server.entity.TenantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Service
@IoTPotCache(name = IoTPotConstants.IOTPOT_TENANT_CACHE_NAME, expiry = IoTPotConstants.SIX_HOURS, prefix = IoTPotConstants.IOTPOT_TENANT_CACHE_PREFIX)
public class TenantCacheService extends AbstractGeneralCacheService {

  @Autowired
  private TenantDao tenantDao;
  @Autowired
  private TenantMapper tenantMapper;
  @Autowired
  private TenantCacheDatabaseLoaderService tenantCacheDatabaseLoaderService;

  public Tenant findByDiscriminator(String discriminator) {

    Tenant tenant = null;
    UUID id = get(discriminator, UUID.class);
    if (id != null) {

      tenant = get(id, Tenant.class);
      if (tenant != null) {
        return tenant;
      } else {
        deleteKey(discriminator);
      }
    }
    return storeToCacheAndReturn(tenantCacheDatabaseLoaderService.loadFromDatabaseByFindByDiscriminator(discriminator));
  }

  public Tenant findOne(UUID id) {

    Tenant tenant = get(id, Tenant.class);
    if (tenant != null) {
      return tenant;
    }
    return storeToCacheAndReturn(tenantCacheDatabaseLoaderService.loadFromDatabaseByFindOne(id));
  }

  public Tenant deleteTenant(Tenant tenant) {
    TenantEntity tenantEntity = tenantDao.findOne(tenant.getId());
    if (tenantEntity != null) {
      for (DataCenterEntity cre : tenantEntity.getDatacenters()) {
        cre.getTenants().remove(tenantEntity);
      }
      tenantDao.delete(tenantEntity);
      evictFromCache(tenant);
    }
    return tenant;
  }

  public Tenant save(Tenant tenant) {

    return tenantCacheDatabaseLoaderService.save(tenant);
  }

  public Tenant update(UUID id, Tenant tenant) throws InvocationTargetException, IllegalAccessException {

    return tenantCacheDatabaseLoaderService.update(id, tenant);
  }


  private void storeToCache(Tenant tenant) {

    Map<Object, Object> keyValuePairs = new HashMap<>();
    keyValuePairs.put(tenant.getId(), tenant);
    keyValuePairs.put(tenant.getDiscriminator(), tenant.getId());
    storeObject(keyValuePairs);
  }
  private void evictFromCache(Tenant tenant) {
    List<Object> keys = new ArrayList<>();
    keys.add(tenant.getId());
    keys.add(tenant.getDiscriminator());
    deleteObject(keys);
  }
  private Tenant storeToCacheAndReturn(Tenant tenant) {

    if (tenant != null) {
      storeToCache(tenant);
    }
    return tenant;
  }

}
