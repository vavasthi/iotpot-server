/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.service;

import com.iotpot.server.mapper.ComputeRegionMapper;
import com.iotpot.server.entity.DataCenterEntity;
import com.iotpot.server.common.exception.EntityNotFoundException;
import com.iotpot.server.common.exception.PatchingException;
import com.iotpot.server.pojos.DataCenter;
import com.iotpot.server.mapper.ObjectPatcher;
import com.iotpot.server.dao.DataCenterDao;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

@Service
public class ComputeRegionService {

  private static final Logger logger = Logger.getLogger(ComputeRegionService.class);

  @Autowired
  private DataCenterDao dataCenterDao;
  @Autowired
  private ComputeRegionMapper computeRegionMapper;


  @Transactional(readOnly = true)
  public DataCenter getComputeRegion(UUID id) throws EntityNotFoundException {
    DataCenterEntity cre = dataCenterDao.findOne(id);
    if (cre == null) {
      throw new EntityNotFoundException(id.toString() + " compute region not found.", HttpStatus.NOT_FOUND.value());
    }
    return computeRegionMapper.mapEntityIntoPojo(cre);
  }

  @Transactional(readOnly = true)
  public List<DataCenter> listComputeRegions() {

    return computeRegionMapper.mapEntitiesIntoPojos(dataCenterDao.findAll());
  }

  @Transactional
  public DataCenter createComputeRegion(DataCenter dataCenter) {

    DataCenterEntity cre = computeRegionMapper.mapPojoIntoEntity(dataCenter);
    cre = dataCenterDao.save(cre);
    return computeRegionMapper.mapEntityIntoPojo(cre);
  }

  @Transactional
  public DataCenter updateRegion(UUID id,
                                 DataCenter dataCenter) throws IllegalAccessException,
          PatchingException,
          InvocationTargetException {

    DataCenterEntity cre = dataCenterDao.findOne(id);
    DataCenterEntity newCre = computeRegionMapper.mapPojoIntoEntity(dataCenter);
    ObjectPatcher.diffAndPatch(cre, newCre);
    return computeRegionMapper.mapEntityIntoPojo(cre);
  }

  @Transactional
  public DataCenter deleteComputeRegion(UUID id)
      throws IllegalAccessException, PatchingException, InvocationTargetException {

    DataCenterEntity cre = dataCenterDao.findOne(id);
    DataCenter pojo = computeRegionMapper.mapEntityIntoPojo(cre);
    dataCenterDao.delete(cre);
    return pojo;
  }

}
