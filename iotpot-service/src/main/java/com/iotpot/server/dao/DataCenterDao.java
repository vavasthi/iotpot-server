/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.DataCenterEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface DataCenterDao extends CrudRepository<DataCenterEntity, UUID> {

}
