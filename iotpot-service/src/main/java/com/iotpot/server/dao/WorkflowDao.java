/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.AccountEntity;
import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.entity.TenantEntity;
import com.iotpot.server.entity.WorkflowEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Created by vinay on 1/6/16.
 */
public interface WorkflowDao extends CrudRepository<WorkflowEntity, UUID> {

  @Query("SELECT ae from  com.iotpot.server.entity.WorkflowEntity ae where ae.tenant = :tenant")
  List<WorkflowEntity> findByTenant(@Param("tenant") TenantEntity tenantEntity);
}
