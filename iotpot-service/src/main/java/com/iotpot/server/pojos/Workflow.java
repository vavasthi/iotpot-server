package com.iotpot.server.pojos;

import org.joda.time.DateTime;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Workflow extends Base {

  public Workflow() {

  }
  public Workflow(Tenant tenant,
                  UUID id,
                  DateTime createdAt,
                  DateTime updatedAt,
                  String createdBy,
                  String updatedBy,
                  String name,
                  String description,
                  List<String> keywords,
                  List<WorkflowStep> steps) {
    super(id, createdAt, updatedAt, createdBy, updatedBy, name);
    this.description = description;
    this.keywords = keywords;
    this.steps = steps;
  }

  public Tenant getTenant() {
    return tenant;
  }

  public void setTenant(Tenant tenant) {
    this.tenant = tenant;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<String> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<String> keywords) {
    this.keywords = keywords;
  }

  public List<WorkflowStep> getSteps() {
    return steps;
  }

  public void setSteps(List<WorkflowStep> steps) {
    this.steps = steps;
  }

  private Tenant tenant;
  private String description;
  private List<String> keywords;
  private List<WorkflowStep> steps;
}
