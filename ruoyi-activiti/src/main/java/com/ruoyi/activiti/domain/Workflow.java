package com.ruoyi.activiti.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 业务流程对象 biz_workflow
 *
 * @author ruoyi
 * @date 2024-08-21
 */
@Data
@TableName(value = "biz_workflow")
public class Workflow{

    /** 主键ID */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 流程实例ID */
    private String procInstId;

    /** 业务ID关联业务主键 */
    private Long bizId;

    /** 业务模块跟实体表名一样 */
    private String bizModel;

    /** 当前节点名称 */
    private String currentNodeName;

    /** 当前节点Key */
    private String currentNodeKey;

    /** 申请人账号 */
    private String applicant;

    /** 申请人姓名 */
    private String applicantName;

    /** 申请人ID */
    private Long applicantId;

    /** 申请部门 */
    private String applyDept;

    /** 申请时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime applyTime;

    /** 待审批人账号 */
    private String approverBy;

    /** 待审批人姓名 */
    private String approverName;

    /** 待审批人ID */
    private String approverId;

    /** 已审批人账号 */
    private String approvedBy;

    /** 已审批人姓名 */
    private String approvedName;

    /** 已审批人ID */
    private Long approvedId;

    /** 已审批时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime approvedTime;

    /** 字典workflow_status */
    private Integer workflowStatus;
    /** 更新者Id */
    private Long updateId;

    /** 更新者 */
    private String updateBy;

    /** 更新者姓名 */
    private String updateName;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /** 备注 */
    private String remark;

    /** 是否删除 */
    @TableLogic
    private Integer isDel;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
                .append("id", getId())
                .append("procInstId", getProcInstId())
                .append("bizId", getBizId())
                .append("bizModel", getBizModel())
                .append("currentNodeName", getCurrentNodeName())
                .append("currentNodeKey", getCurrentNodeKey())
                .append("applicant", getApplicant())
                .append("applicantName", getApplicantName())
                .append("applicantId", getApplicantId())
                .append("applyDept", getApplyDept())
                .append("applyTime", getApplyTime())
                .append("approverBy", getApproverBy())
                .append("approverName", getApproverName())
                .append("approverId", getApproverId())
                .append("approvedBy", getApprovedBy())
                .append("approvedName", getApprovedName())
                .append("approvedId", getApprovedId())
                .append("approvedTime", getApprovedTime())
                .append("remark", getRemark())
                .append("isDel", getIsDel())
                .append("updateTime", getUpdateTime())
                .append("updateBy", getUpdateBy())
                .append("updateId", getUpdateId())
                .append("updateName", getUpdateName())
                .append("workflowStatus", getWorkflowStatus())
                .toString();
    }
}
