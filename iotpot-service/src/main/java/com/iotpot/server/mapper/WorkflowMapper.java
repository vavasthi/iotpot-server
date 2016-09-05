/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;

import com.iotpot.server.common.exception.EncryptionException;
import com.iotpot.server.dao.*;
import com.iotpot.server.entity.*;
import com.iotpot.server.pojos.*;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
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
public final class WorkflowMapper {

  Logger logger = Logger.getLogger(WorkflowMapper.class);

  @Autowired
  private TenantDao tenantDao;
  @Autowired
  private DataCenterDao dataCenterDao;
  @Autowired
  private RoleDao roleDao;
  @Autowired
  private ApplianceDao applianceDao;
  @Autowired
  private TenantMapper tenantMapper;
  @Autowired
  private SessionMapper sessionMapper;
  @Autowired
  private DeviceMapper deviceMapper;
  @Autowired
  private WorkflowDao workflowDao;
  @Autowired
  private WorkflowKeywordDao workflowKeywordDao;
  @Autowired
  private WorkflowStepDao workflowStepDao;

  public List<Workflow> mapEntitiesIntoPojos(Iterable<WorkflowEntity> entities) {
    List<Workflow> pojos = new ArrayList<>();

    entities.forEach(e -> pojos.add(mapEntityIntoPojo(e)));

    return pojos;
  }

  public List<WorkflowEntity> mapPojosIntoEntities(Iterable<Workflow> pojos) {
    List<WorkflowEntity> entities = new ArrayList<>();
    pojos.forEach(e -> entities.add(mapPojoIntoEntity(e)));
    return entities;
  }

  public Workflow mapEntityIntoPojo(WorkflowEntity entity) {
    List<String> keywords = null;
    if (entity.getKeywords() != null && entity.getKeywords().size() > 0) {
      keywords = new ArrayList<>();
      for (WorkflowKeywordEntity keywordEntity : entity.getKeywords()) {
        keywords.add(keywordEntity.getName());
      }
    }
    List<WorkflowStep> steps = null;
    if (entity.getSteps() != null && entity.getSteps().size() > 0) {
      steps = new ArrayList<>();
      for (WorkflowStepEntity step : entity.getSteps()) {
        steps.add(new WorkflowStep(step.getName(), step.getDescription(), step.getDuration()));
      }
    }
    Workflow pojo = new Workflow(
            tenantMapper.mapEntityIntoPojo(entity.getTenant()),
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedBy(),
            entity.getName(),
            entity.getDescription(),
            keywords,
            steps);
    return pojo;
  }

  public WorkflowEntity mapPojoIntoEntity(Workflow pojo) {

    List<WorkflowStepEntity> steps = null;
    if (pojo.getSteps() != null && pojo.getSteps().size() > 0) {
      steps = new ArrayList<>();
      for (WorkflowStep step : pojo.getSteps()) {
        WorkflowStepEntity stepEntity
                = new WorkflowStepEntity(step.getName(), step.getDescription(), step.getDuration());
        stepEntity = workflowStepDao.save(stepEntity);
        steps.add(stepEntity);
      }
    }
    WorkflowEntity workflowEntity = new WorkflowEntity(tenantDao.findOne(pojo.getTenant().getId()),
            pojo.getName(),
            pojo.getDescription());
    workflowEntity.setSteps(steps);
    workflowEntity = workflowDao.save(workflowEntity);
    List<WorkflowKeywordEntity> keywords = null;
    if (pojo.getKeywords() != null && pojo.getKeywords().size() > 0) {
      keywords = new ArrayList<>();
      for (String keyword : pojo.getKeywords()) {
        WorkflowKeywordEntity wfke
                = workflowKeywordDao.save(new WorkflowKeywordEntity(keyword, workflowEntity.getId()));
        keywords.add(wfke);
      }
    }
    workflowEntity.setKeywords(keywords);
    return workflowEntity;
  }

}
