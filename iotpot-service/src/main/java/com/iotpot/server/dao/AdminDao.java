/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.AdminEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AdminDao extends CrudRepository<AdminEntity, UUID> {

  @Query("SELECT ae from  com.iotpot.server.entity.AdminEntity ae where ae.name = :name ")
  List<AdminEntity> findByName(@Param("name") String name);

  @Query("SELECT ae from  com.iotpot.server.entity.AdminEntity ae where ae.name = :name or ae.adminEmail = :adminEmail")
  List<AdminEntity> findByNameOrEmail(@Param("name") String name,
                                      @Param("adminEmail") String adminEmail);
}
