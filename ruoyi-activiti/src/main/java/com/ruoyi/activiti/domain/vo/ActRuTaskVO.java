package com.ruoyi.activiti.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author dingsheng
 * @date 2024/7/2
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActRuTaskVO {

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 节点编号
     */
    private String node;
    /**
     * 节点名称
     */
    private String name;
    /**
     * 待审批人
     */
    private String approverBy;
    /**
     * 创建时间
     */
    private Date createTime;
}
