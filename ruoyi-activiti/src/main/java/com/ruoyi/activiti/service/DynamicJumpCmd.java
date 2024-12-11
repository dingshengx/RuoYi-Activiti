package com.ruoyi.activiti.service;

import lombok.AllArgsConstructor;
import org.activiti.bpmn.model.BpmnModel;
import org.activiti.bpmn.model.FlowElement;
import org.activiti.engine.ActivitiException;
import org.activiti.engine.ActivitiIllegalArgumentException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.Command;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.persistence.entity.ExecutionEntity;
import org.activiti.engine.impl.persistence.entity.ExecutionEntityManager;
import org.activiti.engine.impl.util.ProcessDefinitionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 流程跳转命令
 */
@AllArgsConstructor
public class DynamicJumpCmd implements Command<Void> {
    //流程实例编号
    protected String processInstanceId;
    //跳转起始节点
    protected String fromActivityId;
    //跳转目标节点
    protected String toActivityId;

    public Void execute(CommandContext commandContext) {
        //processInstanceId参数不能为空
        if (this.processInstanceId == null) {
            throw new ActivitiIllegalArgumentException("流程实例ID不能为空");
        }
        //获取执行实例管理类
        ExecutionEntityManager executionEntityManager = commandContext.getExecutionEntityManager();
        //获取执行实例
        ExecutionEntity execution = (ExecutionEntity) executionEntityManager.findById(this.processInstanceId);
        if (execution == null) {
            throw new ActivitiException("流程执行里找不到该流程实例ID： " + this.processInstanceId);
        }
        if (!execution.isProcessInstanceType()) {
            throw new ActivitiException("该ID不是一个流程实例： " + this.processInstanceId);
        }
        List<ExecutionEntity> activeExecutionEntities = new ArrayList<>();
        //获取所有子执行实例
        List<ExecutionEntity> childExecutions =
                executionEntityManager.findChildExecutionsByProcessInstanceId(execution.getId());
        for (ExecutionEntity childExecution : childExecutions) {
            if (childExecution.getCurrentActivityId().equals(this.fromActivityId)) {
                activeExecutionEntities.add(childExecution);
            }
        }
        if (activeExecutionEntities.isEmpty()) {
            throw new ActivitiException("流程执行里找不到活动节点id:" + this.fromActivityId);
        }
        //获取流程模型
        BpmnModel bpmnModel = ProcessDefinitionUtil.getBpmnModel(execution.getProcessDefinitionId());
        //获取当前节点
        FlowElement fromActivityElement = bpmnModel.getFlowElement(this.fromActivityId);
        //获取目标节点
        FlowElement toActivityElement = bpmnModel.getFlowElement(this.toActivityId);
        //校验id为fromActivityId的节点是否存在
        if (fromActivityElement == null) {
            throw new ActivitiException("流程定义里找不到该活动节点：" + this.fromActivityId);
        }
        //校验id为toActivityId的节点是否存在
        if (toActivityElement == null) {
            throw new ActivitiException("流程定义里找不到该活动节点：" + this.toActivityId);
        }
        //删除当前节点所在的执行实例及相关数据
        for (ExecutionEntity activeExecutionEntity : activeExecutionEntities) {
            //不能删除多实例根节点
            if (!activeExecutionEntity.isMultiInstanceRoot()) {
                executionEntityManager.deleteExecutionAndRelatedData(activeExecutionEntity,
                        fromActivityElement.getName() + "跳转到" + toActivityElement.getName(), false);
            }
        }
        //再删除多实例父节点
        for (ExecutionEntity activeExecutionEntity : activeExecutionEntities) {
            if (activeExecutionEntity.isMultiInstanceRoot()) {
                executionEntityManager.deleteExecutionAndRelatedData(activeExecutionEntity,
                        fromActivityElement.getName() + "跳转到" + toActivityElement.getName(), false);
            }
        }

        //创建当前流程实例的子执行实例
        ExecutionEntity newChildExecution = executionEntityManager.createChildExecution(execution);
        //设置执行实例的当前活动节点为目标节点
        newChildExecution.setCurrentFlowElement(toActivityElement);
        //向operations中压入继续流程的操作类
        Context.getAgenda().planContinueProcessOperation(newChildExecution);

        return null;
    }
}
