/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;

import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.pojos.DataCenter;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public final class ComputeRegionMapper {

  public static List<DataCenter> mapEntitiesIntoPojos(Iterable<DataCenterEntity> entities) {
    List<DataCenter> pojos = new ArrayList<>();

    entities.forEach(e -> pojos.add(mapEntityIntoPojo(e)));

    return pojos;
  }

  public static List<DataCenterEntity> mapPojosIntoEntities(Iterable<DataCenter> pojos) {

    List<DataCenterEntity> entities = new ArrayList<>();

    pojos.forEach(e -> entities.add(mapPojoIntoEntity(e)));

    return entities;
  }

  public static DataCenter mapEntityIntoPojo(DataCenterEntity entity) {
    DataCenter pojo = new DataCenter(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getCreatedBy(),
            entity.getUpdatedBy(),
            entity.getName(),
            entity.getIdentityURL(),
            entity.getApiEndpointURL(),
            entity.getCsEndpointURL(),
            entity.getStunURL(),
            entity.getMqttURL(),
            entity.getNtpURL(),
            entity.getUserCount());
    return pojo;
  }

  public static DataCenterEntity mapPojoIntoEntity(DataCenter pojo) {
    DataCenterEntity te
            = new DataCenterEntity(pojo.getName(),
            pojo.getIdentityURL(),
            pojo.getApiEndpointURL(),
            pojo.getCsEndpointURL(),
            pojo.getStunURL(),
            pojo.getMqttURL(),
            pojo.getNtpURL());
    return te;
  }

}
