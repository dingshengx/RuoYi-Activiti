package com.ruoyi.activiti.domain.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 供应商开发申请VO
 * 
 * @author jackyfan
 * @date 2024-07-05
 */
@Data
@ApiModel(value = "审批记录集合",description = "分组汇集审批记录")
public class ApprovalRecordGroupVO
{
   @ApiModelProperty(value = "审批记录")
   private List<ApprovalRecordVO> records;

   @ApiModelProperty(value = "流程节点Key")
   private String taskDefKey;

   @ApiModelProperty(value = "流程节点名称")
   private String taskDefName;

   @ApiModelProperty(value = "排序号")
   private String sn;


}
