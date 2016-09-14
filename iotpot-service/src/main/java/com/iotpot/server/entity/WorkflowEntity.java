package com.iotpot.server.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "workflow")
public class WorkflowEntity extends BaseEntity {

  public WorkflowEntity(TenantEntity tenant, String name, String description) {
    super(name);
    this.tenant = tenant;
    this.description = description;
    this.keywords = new ArrayList<>();
  }

  public WorkflowEntity() {
  }

  public TenantEntity getTenant() {
    return tenant;
  }

  public void setTenant(TenantEntity tenant) {
    this.tenant = tenant;
  }

  public List<WorkflowStepEntity> getSteps() {
    return steps;
  }

  public void setSteps(List<WorkflowStepEntity> steps) {
    this.steps = steps;
  }

  public List<WorkflowKeywordEntity> getKeywords() {
    return keywords;
  }

  public void setKeywords(List<WorkflowKeywordEntity> keywords) {
    this.keywords = keywords;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  private String description;
  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<WorkflowKeywordEntity> keywords;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<WorkflowStepEntity> steps;

  @ManyToOne(fetch = FetchType.LAZY)
  private TenantEntity tenant;
}
