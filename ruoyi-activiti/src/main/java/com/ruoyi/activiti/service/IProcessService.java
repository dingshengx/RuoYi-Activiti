package com.ruoyi.activiti.service;

import com.ruoyi.activiti.domain.dto.ApprovalInfo2DTO;
import com.ruoyi.activiti.domain.dto.ApprovalInfoDTO;
import com.ruoyi.activiti.domain.dto.StartWorkflowDTO;
import org.activiti.bpmn.model.ExtensionElement;
import org.activiti.bpmn.model.UserTask;
import org.activiti.engine.history.HistoricVariableInstance;
import org.activiti.engine.task.Task;

import java.util.List;
import java.util.Map;

/**
 * 流程处理Service接口
 *
 * @author jackyfan
 * @date 2024-07-05
 */
public interface IProcessService {

    /**
     * 发起流程、保存流程信息到biz_workflow表
     * @param startWorkflowDTO 发起流程参数DTO
     * @return 流程实例ID
     */
    public String submit(StartWorkflowDTO startWorkflowDTO);

    public void approval(ApprovalInfo2DTO approvalInfo2DTO);

    public void rollback(ApprovalInfo2DTO approvalInfo2DTO);

    /**
     * 发起流程,不会保存流程信息到biz_workflow表
     *
     * @param processDefinitionKey 流程key
     * @param businessKey          业务key
     * @param variables            流程变量
     * @return
     */
    @Deprecated
    public String submit(String processDefinitionKey, String businessKey, Map<String, Object> variables);

    @Deprecated
    public void approval(ApprovalInfoDTO approvalInfoDTO);

    @Deprecated
    public void rollback(ApprovalInfoDTO approvalInfoDTO);

    public Task getCurrentTaskByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例ID获取当前任务（会审时存在多个代办）
     * @param processInstanceId 流程实例id
     * @return 代办任务列表
     */
    public List<Task> getCurrentTasksByProcessInstanceId(String processInstanceId);

    public boolean isProcessInstanceEnded(String processInstanceId);

    public List<UserTask> getUserTasksByProcessInstanceId(String processInstanceId);

    public Map<String, HistoricVariableInstance> getProcessVariables(String processInstanceId);

    public String getExtensionElementsProperty(String processDefinitionId, String taskDefinitionKey,
                                               String propertyName);

    public String getExtensionElementsProperty(Map<String, List<ExtensionElement>> taskElements, String propertyName);

    public int getTaskSn(String processDefinitionId, String taskDefinitionKey, String formKey);

    public int getTaskSn(Map<String, List<ExtensionElement>> taskElements, String formKey);
}
