package com.ruoyi.activiti.domain.dto;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 发起业务流程参数
 *
 * @author ruoyi
 * @date 2024-08-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StartWorkflowDTO {
  /** 流程编号key */
  private String processDefinitionKey;

  /** 业务ID */
  private Long bizId;

  /** 业务模块 */
  private String bizModel;

  /** 流程变量 */
  private Map<String, Object> variables = new HashMap<>();

  public String getBusinessKey() {
    return this.bizModel + ":" + this.bizId;
  }
}
