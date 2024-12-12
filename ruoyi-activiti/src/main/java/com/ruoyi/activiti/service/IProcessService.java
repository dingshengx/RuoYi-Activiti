package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.ApprovalInfoDTO;
import com.ruoyi.activiti.domain.dto.StartWorkflowDTO;
import java.util.List;
import java.util.Map;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;

/**
 * 流程处理Service接口
 *
 * @author jackyfan
 * @date 2024-07-05
 */
public interface IProcessService {

  /**
   * 发起
   *
   * @param startWorkflowDTO 入参
   * @return 流程实例ID
   */
  public String submit(StartWorkflowDTO startWorkflowDTO);

  /**
   * 审批
   *
   * @param approvalInfoDTO 入参
   */
  public void approval(ApprovalInfoDTO approvalInfoDTO);

  /**
   * 驳回
   *
   * @param approvalInfoDTO 入参
   */
  public void rollback(ApprovalInfoDTO approvalInfoDTO);

  /**
   * 根据流程实例ID获取当前任务
   *
   * @param processInstanceId 流程实例id
   * @return 代办任务
   */
  public Task getCurrentTaskByProcessInstanceId(String processInstanceId);

  /**
   * 根据流程实例ID获取当前任务list（会审时存在多个代办）
   *
   * @param processInstanceId 流程实例id
   * @return 代办任务列表
   */
  public List<Task> getCurrentTasksByProcessInstanceId(String processInstanceId);

  /**
   * 判断流程是否结束
   *
   * @param processInstanceId 流程实例id
   * @return 结束状态
   */
  public boolean isProcessInstanceEnded(String processInstanceId);

  /**
   * 根据流程实例ID获取对应流程定义的用户任务节点集合
   *
   * @param processInstanceId 流程实例ID
   * @return 用户任务节点集合
   */
  public List<UserTask> getUserTasksByProcessInstanceId(String processInstanceId);

  /**
   * 获取流程变量（排除掉用户任务的变量）
   *
   * @param processInstanceId 流程实例ID
   * @return 参数列表
   */
  public Map<String, HistoricVariableInstance> getProcessVariables(String processInstanceId);

  /**
   * 获取拓展信息
   *
   * @param processDefinitionId 流程实例ID
   * @param taskDefinitionKey 任务节点key
   * @param propertyName 拓展字段名称
   * @return 拓展字段值
   */
  public String getExtensionElementsProperty(
      String processDefinitionId, String taskDefinitionKey, String propertyName);

  /**
   * 获取拓展信息
   *
   * @param taskElements 用户任务拓展元素
   * @param propertyName 拓展字段名称
   * @return 拓展字段值
   */
  public String getExtensionElementsProperty(
      Map<String, List<ExtensionElement>> taskElements, String propertyName);

  /**
   * 获取任务编号（对应上面的propertyName，约定为sn，查询进度等会用到）
   *
   * @param processDefinitionId 流程定义ID
   * @param taskDefinitionKey 任务节点key
   * @param formKey 表单key
   * @return 任务编号
   */
  public int getTaskSn(String processDefinitionId, String taskDefinitionKey, String formKey);

  /**
   * 获取任务编号（对应上面的propertyName，约定为sn，查询进度等会用到）
   *
   * @param taskElements 用户任务拓展元素
   * @param formKey 表单key
   * @return 任务编号
   */
  public int getTaskSn(Map<String, List<ExtensionElement>> taskElements, String formKey);
}
