/*
 * Copyright 2016 (c) Hubble Connected (HKT) Ltd. - All Rights Reserved
 *
 * Proprietary and confidential.
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited.
 */

package com.iotpot.server.endpoints.v1;

import com.iotpot.server.cacheloading.TenantCacheDatabaseLoaderService;
import com.iotpot.server.common.caching.TenantCacheService;
import com.iotpot.server.pojos.Appliance;
import com.iotpot.server.pojos.Tenant;
import com.iotpot.server.pojos.Workflow;
import com.iotpot.server.pojos.WorkflowStep;
import com.iotpot.server.pojos.constants.IoTPotConstants;
import com.iotpot.server.service.DeviceService;
import com.iotpot.server.service.TenantService;
import com.iotpot.server.service.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

/**
 * Created by vinay on 1/4/16.
 */
@RestController
@RequestMapping(IoTPotConstants.V1_WORKFLOW_ENDPOINT)
public class WorkflowEndpoint extends BaseEndpoint {

  @Autowired
  private WorkflowService workflowService;

  @Autowired
  private TenantCacheService tenantCacheService;

  @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Workflow getDevice(@PathVariable("tenant") String tenant, @PathVariable("id") UUID id) throws IOException {

    return workflowService.findOne(id);
  }

  @RequestMapping(value = "/search/{keyword}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public List<Workflow> getDevice(@PathVariable("tenant") String tenant,
                                  @PathVariable("keyword") String keyword) throws IOException {

    return workflowService.findByKeyword(keyword.toLowerCase());
  }

  @RequestMapping(value = "/dummy", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
  public Workflow getDevice(@PathVariable("tenant") String tenant) throws IOException {

    Workflow workflow = new Workflow();
    workflow.setName("Eggs");
    workflow.setDescription("This is a recipe for eggs");
    List<String> keywords = new ArrayList<>();
    keywords.add("eggs");
    keywords.add("oil");
    workflow.setKeywords(keywords);
    List<WorkflowStep> steps = new ArrayList<>();
    steps.add(new WorkflowStep("Step 1", "This is step s1", 10));
    steps.add(new WorkflowStep("Step 2", "This is step s2", 10));
    workflow.setSteps(steps);
    return workflow;
  }

  @RequestMapping(method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
  public Workflow save(@PathVariable("tenant") String tenantDescriminator,
                       @RequestBody @Valid Workflow workflow) throws IOException {

    Tenant tenant = tenantCacheService.findByDiscriminator(tenantDescriminator);
    workflow.setTenant(tenant);
    return workflowService.save(workflow);
  }
}
