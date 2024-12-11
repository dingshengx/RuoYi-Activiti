package com.ruoyi.activiti.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.ruoyi.activiti.domain.dto.AttachmentDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 新品开发计划审批 DTO
 *
 * @author dingsheng
 * @date 2024/6/28
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "审批记录", description = "用于返回审批记录信息，包括附件。")
public class ApprovalRecordVO {

    /**
     * 流程实例ID
     */
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;
    /**
     * 审批人
     */
    @ApiModelProperty(value = "审批人账号")
    private Long  approvedId;

    @ApiModelProperty(value = "审批人账号")
    private String approvedBy;

    @ApiModelProperty(value = "审批人姓名")
    private String approvedName;

    @ApiModelProperty(value = "审批部门名称")
    private String deptName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "审批时间")
    private String approvedTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "任务开始时间")
    private String taskStartTime;

    /**
     * 审批意见：通过1-驳回0
     */
    @ApiModelProperty(value = "审批意见：通过1-驳回0")
    private Integer state;

    @ApiModelProperty(value = "流程节点Key")
    private String taskDefKey;

    @ApiModelProperty(value = "流程节点名称")
    private String taskDefName;

    /**
     * 审批备注
     */
    @ApiModelProperty(value = "审批备注")
    private String remark;

    /**
     * 业务Key
     */
    @ApiModelProperty(value = "业务Key,用于关联业务数据")
    private String businessKey;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 附件
     */
    @ApiModelProperty(value = "审批附件")
    List<AttachmentVO> attachments;

}
