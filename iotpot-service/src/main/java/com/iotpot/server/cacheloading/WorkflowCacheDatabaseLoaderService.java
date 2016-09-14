/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.cacheloading;

import com.iotpot.server.dao.*;
import com.iotpot.server.entity.ApplianceEntity;
import com.iotpot.server.entity.SessionEntity;
import com.iotpot.server.entity.WorkflowEntity;
import com.iotpot.server.entity.WorkflowKeywordEntity;
import com.iotpot.server.mapper.DeviceMapper;
import com.iotpot.server.mapper.ObjectPatcher;
import com.iotpot.server.mapper.WorkflowMapper;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.Session;
import com.iotpot.server.pojos.Workflow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.datatype.DatatypeConfigurationException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class WorkflowCacheDatabaseLoaderService extends AbstractDatabaseLoaderService {

  @Autowired
  private WorkflowDao workflowDao;

  @Autowired
  private WorkflowKeywordDao workflowKeywordDao;
  @Autowired
  private WorkflowMapper workflowMapper;

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public Workflow loadFromDatabaseByFindOne(UUID id) {
    return workflowMapper.mapEntityIntoPojo(workflowDao.findOne(id));
  }

  @Transactional(readOnly = true, propagation = Propagation.REQUIRED)
  public List<Workflow> loadFromDatabaseByKeyword(String keyword) {

    List<WorkflowKeywordEntity> workflowKeywordEntities = workflowKeywordDao.findByName(keyword);
    if (workflowKeywordEntities == null || workflowKeywordEntities.size() == 0) {
      return new ArrayList<>();
    }
    List<WorkflowEntity> workflowEntities = new ArrayList<>();
    for (WorkflowKeywordEntity wfke : workflowKeywordEntities) {
      WorkflowEntity wfe = workflowDao.findOne(wfke.getWorkflowId());
      workflowEntities.add(wfe);
    }
    return workflowMapper.mapEntitiesIntoPojos(workflowEntities);
  }
  @Transactional(propagation = Propagation.REQUIRED)
  public Workflow save(Workflow workflow) {

    WorkflowEntity wfe = workflowMapper.mapPojoIntoEntity(workflow);
    return workflowMapper.mapEntityIntoPojo(workflowDao.save(wfe));
  }

}
