/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.dao;

import com.iotpot.server.entity.TenantEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface TenantDao extends CrudRepository<TenantEntity, UUID> {

    @Query("SELECT te from  com.iotpot.server.entity.TenantEntity te where te.discriminator = :discriminator")
    TenantEntity findByDiscriminator(@Param("discriminator") String discriminator);
}
