/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.RoleEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface RoleDao extends CrudRepository<RoleEntity, UUID> {

  @Query("SELECT re from  com.iotpot.server.entity.RoleEntity re where re.name = :name ")
  Set<RoleEntity> findByRole(@Param("name") String name);

}
