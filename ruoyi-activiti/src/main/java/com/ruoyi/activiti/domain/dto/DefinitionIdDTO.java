package com.ruoyi.activiti.domain.dto;

import lombok.Getter;
import lombok.Setter;
import org.activiti.engine.repository.ProcessDefinition;

@Setter
@Getter
public class DefinitionIdDTO {
  private String deploymentID;
  private String resourceName;

  public DefinitionIdDTO() {}

  public DefinitionIdDTO(ProcessDefinition processDefinition) {
    this.deploymentID = processDefinition.getDeploymentId();
    this.resourceName = processDefinition.getResourceName();
  }
}
