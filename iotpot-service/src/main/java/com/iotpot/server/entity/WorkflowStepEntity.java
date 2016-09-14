package com.iotpot.server.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "workflow_step")
public class WorkflowStepEntity extends BaseEntity {

  public WorkflowStepEntity(String name, String description, Integer duration) {
    super(name);
    this.description = description;
    this.duration = duration;
  }
  public WorkflowStepEntity() {

  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getDuration() {
    return duration;
  }

  public void setDuration(Integer duration) {
    this.duration = duration;
  }

  private String description;
  private Integer duration;
}
