package com.iotpot.server.entity;


import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.UUID;

/**
 * Created by vavasthi on 5/9/16.
 */
@Entity
@Table(name = "workflow_keyword")
public class WorkflowKeywordEntity extends BaseEntity {

  public WorkflowKeywordEntity(String keyword, UUID workflowId) {
    super(keyword.toLowerCase());
    this.workflowId = workflowId;
  }
  public WorkflowKeywordEntity() {

  }

  public UUID getWorkflowId() {
    return workflowId;
  }

  public void setWorkflowId(UUID workflowId) {
    this.workflowId = workflowId;
  }

  private UUID workflowId;
}
