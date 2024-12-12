package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 流程定义 DTO
 *
 * @author dingsheng
 * @date 2024/8/01
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流程定义")
public class QueryProcessDefinitionDTO {

  @ApiModelProperty(value = "排序字段 deployTime_")
  public String orderByColumn;

  @ApiModelProperty(value = "排序的方向(asc-顺序 desc-倒序 默认asc)")
  public String isAsc;

  @ApiModelProperty(value = "流程名称")
  private String name;

  @ApiModelProperty(value = "流程KEY")
  private String key;

  @ApiModelProperty(value = "流程实例状态 1 激活 2 挂起")
  private Integer suspendState;

  @ApiModelProperty(value = "开始时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private String beginTime;

  @ApiModelProperty(value = "结束时间")
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private String endTime;
}
