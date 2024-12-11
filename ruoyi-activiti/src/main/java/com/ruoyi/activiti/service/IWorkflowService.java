package com.ruoyi.activiti.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.QueryWorkflowDTO;

/**
 * 业务流程Service接口
 *
 * @author ruoyi
 * @date 2024-08-21
 */
public interface IWorkflowService extends IService<Workflow> {


    /**
     * 查询业务流程列表
     *
     * @param queryWorkflowDTO
     * @return 业务流程
     */
    public List<Workflow> list(QueryWorkflowDTO queryWorkflowDTO);

    /**
     * 检查是否已存在流程
     *
     * @param bizModel 业务模块
     * @param bizId   业务ID
     * @return true存在 false不存在
     */
    public boolean checkExistedWorkflow(String bizModel, Long bizId);

    public Workflow getOne(String processInstanceId);

    public Workflow getOne(String bizModel, Long bizId);

    /**
     * 根据参数查询代办数量
     *
     * @param flowStatusList 状态List
     * @param approverId 待审批人
     * @param dataType 数据类型 0-自己 1-他人
     * @param bizModel 业务模块                
     * @return 数量
     */
    public Integer selectCountByParam(List<Integer> flowStatusList, Long approverId, Integer dataType, String bizModel);

}
