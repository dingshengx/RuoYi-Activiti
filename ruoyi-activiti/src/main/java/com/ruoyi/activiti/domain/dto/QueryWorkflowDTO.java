package com.ruoyi.activiti.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


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
public class QueryWorkflowDTO {


    /** 主键ID */
    @ApiModelProperty(value = "主键ID")
    private Long id;


    /** 流程实例ID */
    @ApiModelProperty(value = "流程实例ID")
    private String procInstId;


    /** 业务ID关联业务主键 */
    @ApiModelProperty(value = "业务ID关联业务主键")
    private Long bizId;


    /** 业务模块跟实体表名一样 */
    @ApiModelProperty(value = "业务模块跟实体表名一样")
    private String bizModel;


    /** 当前节点名称 */
    @ApiModelProperty(value = "当前节点名称")
    private String currentNodeName;


    /** 当前节点Key */
    @ApiModelProperty(value = "当前节点Key")
    private String currentNodeKey;


    /** 申请人账号 */
    @ApiModelProperty(value = "申请人账号")
    private String applicant;


    /** 申请人姓名 */
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


    /** 开始时间 */
    @ApiModelProperty(value = "开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String beginTime;

    /** 结束时间 */
    @ApiModelProperty(value = "结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private String endTime;

    /** 时间标记 */
    @ApiModelProperty(value = "时间标记 0-申请时间 1-审批时间(审批完成页面的完成时间)")
    private Integer timeTag;

    /** 当前记录起始索引 */
    @ApiModelProperty(value = "当前记录起始索引")
    public String pageNum;

    /** 每页显示记录数 */
    @ApiModelProperty(value = "每页显示记录数")
    public String pageSize;

    /** 排序列 */
    @ApiModelProperty(value = "排序字段")
    public String orderByColumn;

    /** 排序的方向 "desc" 或者 "asc" */
    @ApiModelProperty(value = "排序的方向(asc-顺序 desc-倒序 默认asc)")
    public String isAsc;

}
