package com.iotpot.server.pojos;

public class WorkflowStep {

  public WorkflowStep(String name, String description, Integer duration) {
    this.name  = name;
    this.description = description;
    this.duration = duration;
  }
  public WorkflowStep() {

  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
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

  private String name;
  private String description;
  private Integer duration;
}
