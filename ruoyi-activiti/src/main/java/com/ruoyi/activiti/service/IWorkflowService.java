package com.ruoyi.activiti.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ruoyi.activiti.domain.Workflow;
import com.ruoyi.activiti.domain.dto.QueryWorkflowDTO;
import java.util.List;

/**
 * 业务流程Service接口
 *
 * <p>这里具体业务相关的接口，区别于流程相关的Service接口（IProcessDefinitionService IProcessQueryService IProcessService）
 *
 * @author ruoyi
 * @date 2024-08-21
 */
public interface IWorkflowService extends IService<Workflow> {

  /**
   * 查询业务流程列表
   *
   * @param queryWorkflowDTO 入参
   * @return 业务流程
   */
  public List<Workflow> list(QueryWorkflowDTO queryWorkflowDTO);

  /**
   * 检查是否已存在流程
   *
   * @param bizModel 业务模块
   * @param bizId 业务ID
   * @return true存在 false不存在
   */
  public boolean checkExistedWorkflow(String bizModel, Long bizId);

  /**
   * 根据流程实例ID获取业务流程对象
   *
   * @param processInstanceId 流程实例ID
   * @return 业务流程对象
   */
  public Workflow getOne(String processInstanceId);

  /**
   * 根据业务模块和业务ID获取业务流程对象
   *
   * @param bizModel 业务模块
   * @param bizId 业务ID
   * @return 业务流程对象
   */
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
  public Integer selectCountByParam(
      List<Integer> flowStatusList, Long approverId, Integer dataType, String bizModel);
}
