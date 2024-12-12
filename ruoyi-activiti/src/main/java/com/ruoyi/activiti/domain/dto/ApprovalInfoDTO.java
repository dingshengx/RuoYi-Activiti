package com.ruoyi.activiti.domain.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import java.util.Map;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@ApiModel(value = "审批信息", description = "用于提交审批数据传输，包括附件、流程变量。")
public class ApprovalInfoDTO {

  /** 业务Key */
  @ApiModelProperty(value = "业务ID,用于关联业务数据", required = true)
  @NotNull(message = "业务ID不能为空")
  private Long bizId;

  @ApiModelProperty(value = "功能模块,用于关联业务数据", required = true)
  private String bizModel;

  /** 流程实例ID */
  @ApiModelProperty(value = "流程实例ID", hidden = true)
  private String processInstanceId;

  /** 审批人 */
  @ApiModelProperty(value = "审批人账号，默认当前用户",hidden = true)
  private String assignee;

  /** 审批意见：通过1-驳回0 */
  @ApiModelProperty(value = "审批意见：通过1-驳回0", required = true)
  @NotNull(message = "审批意见必选")
  private Integer state;

  /** 审批备注 */
  @ApiModelProperty(value = "审批备注", required = true)
  @NotBlank(message = "审批备注必填")
  private String remark;

  /** 流程变量 */
  @ApiModelProperty(value = "流程变量", example = "{\"purchaser\":\"tony\"}")
  Map<String, Object> variables;

  /** 附件 */
  @ApiModelProperty(value = "审批附件")
  List<AttachmentDTO> attachments;
}
