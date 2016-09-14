/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.endpoints.v1;

import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.service.DeviceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping(IoTPotConstants.V1_DEVICES_ENDPOINT)
public class DeviceEndpoint extends BaseEndpoint {

  private final DeviceService deviceService;

  @Autowired
  public DeviceEndpoint(DeviceService deviceService) {
    this.deviceService = deviceService;
  }

  @Transactional(readOnly = true)
  @RequestMapping(method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public
  @ResponseBody
  Set<Appliance> listDevices() {
    return deviceService.list();
  }

  @Transactional(readOnly = true)
  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Appliance getDevice(@PathVariable("id") UUID id) throws IOException {

    return deviceService.findOne(id);
  }

}
