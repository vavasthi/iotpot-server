/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.service;

import com.iotpot.server.cacheloading.WorkflowCacheDatabaseLoaderService;
import com.iotpot.server.common.caching.AbstractCacheService;
import com.iotpot.server.common.caching.AccountCacheService;
import com.iotpot.server.common.caching.DeviceCacheService;
import com.iotpot.server.dao.ApplianceDao;
import com.iotpot.server.dao.SessionDao;
import com.iotpot.server.mapper.DeviceMapper;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * Created by vinay on 2/22/16.
 */
@Service
public class WorkflowService extends AbstractCacheService {

  @Autowired
  private WorkflowCacheDatabaseLoaderService workflowCacheDatabaseLoaderService;
  @Autowired
  private AccountCacheService accountCacheService;
  @Autowired
  private ApplianceDao applianceDao;
  @Autowired
  private SessionDao sessionDao;
  @Autowired
  private DeviceMapper deviceMapper;

  public List<Workflow> findByKeyword(String keyword) {
    return workflowCacheDatabaseLoaderService.loadFromDatabaseByKeyword(keyword);
  }
  public Workflow findOne(UUID id) {
    return workflowCacheDatabaseLoaderService.loadFromDatabaseByFindOne(id);
  }
  public Workflow save(Workflow workflow) {
    return workflowCacheDatabaseLoaderService.save(workflow);
  }
}
