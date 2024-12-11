package com.ruoyi.activiti.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 审批进度VO
 */
@Data
@ApiModel(value = "审批进度",description = "审批进度")
public class ApprovalProgressVO {
    /**
     * 流程实例ID
     */
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    @ApiModelProperty(value = "审批人账号，包括已审批，未审批")
    private String approveBy;

    @ApiModelProperty(value = "审批人姓名，包括已审批，未审批")
    private String approveName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审批时间")
    private String approvedTime;

    @ApiModelProperty(value = "流程节点Key")
    private String taskDefKey;

    @ApiModelProperty(value = "流程节点名称")
    private String taskDefName;

    @ApiModelProperty(value = "审批备注")
    private String remark;

    @ApiModelProperty(value = "任务状态 0-待审批 1-已审批")
    private String flowStatus;

    @ApiModelProperty(value = "排序号")
    private String sn;
}
