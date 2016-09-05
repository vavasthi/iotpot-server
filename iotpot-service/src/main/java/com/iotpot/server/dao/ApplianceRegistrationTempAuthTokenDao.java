/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.ApplianceRegistrationTempAuthTokenEntity;
import com.iotpot.server.entity.ApplianceEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

/**
 * Created by vinay on 1/6/16.
 */
public interface ApplianceRegistrationTempAuthTokenDao extends CrudRepository<ApplianceRegistrationTempAuthTokenEntity, UUID> {

  @Query("SELECT re from  com.iotpot.server.entity.ApplianceRegistrationTempAuthTokenEntity re where re.tempAuthToken = :tempAuthToken ")
  ApplianceRegistrationTempAuthTokenEntity findByTempAuthToken(@Param("tempAuthToken") String tempAuthToken);

  @Query("SELECT re from  com.iotpot.server.entity.ApplianceRegistrationTempAuthTokenEntity re where re.applianceEntity = :applianceEntity ")
  ApplianceRegistrationTempAuthTokenEntity findByApplianceEntity(@Param("applianceEntity") ApplianceEntity applianceEntity);
}
