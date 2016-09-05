/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.WorkflowKeywordEntity;
import com.iotpot.server.entity.WorkflowStepEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Created by vinay on 1/6/16.
 */
public interface WorkflowStepDao extends CrudRepository<WorkflowStepEntity, UUID> {

}
