package com.ruoyi.activiti.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


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
    /**
     * 流程编号key
     */
    private String processDefinitionKey;

    /**
     * 业务ID
     */
    private Long bizId;

    /**
     * 业务模块
     */
    private String bizModel;

    /**
     * 申请人账号
     */
    @Deprecated
    private String applicant;

    /**
     * 申请人姓名
     */
    @Deprecated
    private String applicantName;

    /**
     * 申请人ID
     */
    @Deprecated
    private Long applicantId;

    /**
     * 申请部门
     */
    @Deprecated
    private String applyDept;
    /**
     * 流程变量
     */
    private Map<String, Object> variables = new HashMap<>();

    public String getBusinessKey() {
        return this.bizModel + ":" + this.bizId;
    }


}
