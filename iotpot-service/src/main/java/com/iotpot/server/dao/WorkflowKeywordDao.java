/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.entity.TenantEntity;
import com.iotpot.server.entity.WorkflowKeywordEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Created by vinay on 1/6/16.
 */
public interface WorkflowKeywordDao extends CrudRepository<WorkflowKeywordEntity, UUID> {

  @Query("SELECT wfke from  com.iotpot.server.entity.WorkflowKeywordEntity wfke where wfke.name = :name")
  List<WorkflowKeywordEntity> findByName(@Param("name") String name);
}
