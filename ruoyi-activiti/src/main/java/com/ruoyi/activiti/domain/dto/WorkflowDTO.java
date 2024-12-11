package com.ruoyi.activiti.domain.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import javax.validation.constraints.NotNull;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 业务流程 DTO
 * 
 * @author ruoyi
 * @date 2024-08-21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "业务流程")
public class WorkflowDTO {


    /** 主键ID */
    @ApiModelProperty(value = "主键ID")
    private Long id;


    /** 流程实例ID */
    @NotBlank(message = "流程实例ID必填")
    @ApiModelProperty(value = "流程实例ID")
    private String procInstId;


    /** 业务ID关联业务主键 */
    @NotNull(message = "业务ID关联业务主键必填")
    @ApiModelProperty(value = "业务ID关联业务主键")
    private Long bizId;


    /** 业务模块跟实体表名一样 */
    @NotBlank(message = "业务模块跟实体表名一样必填")
    @ApiModelProperty(value = "业务模块跟实体表名一样")
    private String bizModel;


    /** 当前节点名称 */
    @ApiModelProperty(value = "当前节点名称")
    private String currentNodeName;


    /** 当前节点Key */
    @ApiModelProperty(value = "当前节点Key")
    private String currentNodeKey;


    /** 申请人账号 */
    @NotBlank(message = "申请人账号必填")
    @ApiModelProperty(value = "申请人账号")
    private String applicant;


    /** 申请人姓名 */
    @NotBlank(message = "申请人姓名必填")
    @ApiModelProperty(value = "申请人姓名")
    private String applicantName;


    /** 申请人ID */
    @ApiModelProperty(value = "申请人ID")
    private Long applicantId;


    /** 申请部门 */
    @ApiModelProperty(value = "申请部门")
    private String applyDept;


    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "申请时间")
    private LocalDateTime applyTime;


    /** 待审批人账号 */
    @ApiModelProperty(value = "待审批人账号")
    private String approverBy;


    /** 待审批人姓名 */
    @ApiModelProperty(value = "待审批人姓名")
    private String approverName;


    /** 待审批人ID */
    @ApiModelProperty(value = "待审批人ID")
    private Long approverId;


    /** 已审批人账号 */
    @ApiModelProperty(value = "已审批人账号")
    private String approvedBy;


    /** 已审批人姓名 */
    @ApiModelProperty(value = "已审批人姓名")
    private String approvedName;


    /** 已审批人ID */
    @ApiModelProperty(value = "已审批人ID")
    private Long approvedId;


    /** 已审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "已审批时间")
    private LocalDateTime approvedTime;


    /** 备注 */
    @ApiModelProperty(value = "备注")
    private String remark;


}
