/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.endpoints.v1;

import com.iotpot.server.common.exception.PatchingException;
import com.iotpot.server.pojos.DataCenter;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.service.ComputeRegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.UUID;

/**
 * Created by vinay on 1/4/16.
 */
@RestController
@RequestMapping(IoTPotConstants.V1_COMPUTE_REGION_ENDPOINT)
public class ComputeRegionEndpoint extends BaseEndpoint {
  private final ComputeRegionService computeRegionService;

  @Autowired
  public ComputeRegionEndpoint(ComputeRegionService computeRegionService) {
    this.computeRegionService = computeRegionService;
  }

  @Transactional(readOnly = true)
  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public
  @ResponseBody
  List<DataCenter> listComputeRegions() {
    return computeRegionService.listComputeRegions();
  }

  @Transactional(readOnly = true)
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataCenter getComputeRegion(@PathVariable("id") UUID id) throws IOException {

    return computeRegionService.getComputeRegion(id);
  }

  @Transactional
  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataCenter createComputeRegion(@RequestBody @Valid DataCenter dataCenter) {
    return computeRegionService.createComputeRegion(dataCenter);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataCenter updateComputeRegion(@PathVariable("id") UUID id,
                                        @RequestBody @Valid DataCenter dataCenter) throws IllegalAccessException, PatchingException,
      InvocationTargetException {
    return computeRegionService.updateRegion(id, dataCenter);
  }

  @Transactional
  @RequestMapping(value = "/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
  public DataCenter deleteComputeRegion(@PathVariable("id") UUID id) throws IllegalAccessException, PatchingException,
      InvocationTargetException {
    return computeRegionService.deleteComputeRegion(id);
  }
}
