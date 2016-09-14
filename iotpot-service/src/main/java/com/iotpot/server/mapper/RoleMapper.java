/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.mapper;


import com.iotpot.server.entity.RoleEntity;
import com.iotpot.server.pojos.IoTPotRole;

import java.util.HashSet;
import java.util.Set;

public final class RoleMapper {

  public  static Set<IoTPotRole> mapEntitiesIntoDTOs(Iterable<RoleEntity> entities) {
    Set<IoTPotRole> dtos = new HashSet<>();

    entities.forEach(e -> dtos.add(mapEntityIntoPojo(e)));

    return dtos;
  }

  public static Set<RoleEntity> mapPojosIntoEntitiess(Iterable<IoTPotRole> pojos) {
    Set<RoleEntity> entities = new HashSet<>();

    pojos.forEach(e -> entities.add(mapPojoIntoEntity(e)));

    return entities;
  }

  public static IoTPotRole mapEntityIntoPojo(RoleEntity role) {
    IoTPotRole pojo = new IoTPotRole(role.getName());
    return pojo;
  }

  public static RoleEntity mapPojoIntoEntity(IoTPotRole pojo) {

    RoleEntity re  = new RoleEntity(pojo.getAuthority());
    return re;
  }

}
