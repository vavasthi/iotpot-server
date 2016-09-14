/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.ApplianceEntity;
import com.iotpot.server.entity.SessionEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ApplianceDao extends CrudRepository<ApplianceEntity, UUID> {

  @Query("SELECT de from  com.iotpot.server.entity.ApplianceEntity de where de.deviceId = :deviceId")
  ApplianceEntity findByDeviceRegistrationId(@Param("deviceId") String deviceId);

  @Query("SELECT de from  com.iotpot.server.entity.ApplianceEntity de where de.sessionEntity = :sessionEntity")
  ApplianceEntity findBySessionEntity(@Param("sessionEntity") SessionEntity sessionEntity);

}
