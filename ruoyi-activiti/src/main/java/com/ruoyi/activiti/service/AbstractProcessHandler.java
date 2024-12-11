package com.ruoyi.activiti.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.activiti.domain.vo.ActRuTaskVO;
import com.ruoyi.activiti.mapper.ActRuTaskMapper;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.system.service.ISysUserService;
import lombok.extern.slf4j.Slf4j;
import org.activiti.api.process.model.ProcessInstance;
import org.activiti.api.process.model.builders.ProcessPayloadBuilder;
import org.activiti.api.process.runtime.ProcessRuntime;
import org.activiti.api.task.model.Task;
import org.activiti.api.task.model.builders.TaskPayloadBuilder;
import org.activiti.api.task.runtime.TaskRuntime;
import org.activiti.engine.ProcessEngine;
import org.activiti.engine.ProcessEngines;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 流程处理抽象类
 *
 * @author dingsheng
 * @date 2024/6/28
 */
@Slf4j
@Component
public abstract class AbstractProcessHandler {

    @Autowired
    private ProcessRuntime processRuntime;

    @Autowired
    private TaskRuntime taskRuntime;

    @Autowired
    private TaskService taskService;

    @Autowired
    private ActRuTaskMapper actRuTaskMapper;

    protected static final String NODE = "node";
    protected static final String NICK_NAME = "nickName";
    protected static final String USER_ID = "userId";
    @Autowired
    private ISysUserService sysUserService;


    /**
     * 启动流程
     *
     * @param flowKey     流程key
     * @param flowName    流程名称
     * @param businessKey 业务key
     * @param variables   参数
     * @return 流程实例
     */
    public ProcessInstance start(String flowKey, String flowName, String businessKey, Map<String, Object> variables) throws RuntimeException {
        return processRuntime.start(ProcessPayloadBuilder.start()
                                                         .withProcessDefinitionKey(flowKey)
                                                         .withName(flowName)
                                                         .withBusinessKey(businessKey)
                                                         .withVariables(variables)
                                                         .build());
    }

    /**
     * 审批
     *
     * @param params     参数
     * @param instanceId 流程实例id
     * @param required   参数是否需要再处理（设置路由、获取配置参数等处理）
     */
    public void approval(Map<String, Object> params, String instanceId, boolean required) throws RuntimeException {

        // 查询代办任务
        String taskId = selectTaskIdByProcessId(instanceId);
        Task task = taskRuntime.task(taskId);

        String node = task.getFormKey();

        // 查询流程变量当前已经拥有的参数
        Map<String, Object> currentParams = selectVariables(instanceId);
        log.info(String.format("流程实例[%s]历史参数：[%s]", instanceId, currentParams.toString()));
        currentParams.putAll(params);

        // 参数处理
        if (required) {
            processVariables(currentParams, node);
            log.info(String.format("流程实例[%s]当前参数：[%s]", instanceId, currentParams.toString()));
        }

        // 推进流程
        progressWorkflow(task, currentParams);

    }

    /**
     * 流程审批参数处理(不同业务各自实现)
     *
     * @param params 参数
     * @param node   节点
     */
    public abstract void processVariables(Map<String, Object> params, String node);

    /**
     * 推进流程
     *
     * @param task      任务
     * @param variables 参数
     */
    public void progressWorkflow(Task task, Map<String, Object> variables) {

        boolean hasVariables = !variables.isEmpty();

        if (task.getAssignee() == null) {
            taskRuntime.claim(TaskPayloadBuilder.claim()
                                                .withTaskId(task.getId())
                                                .build());
        }
        if (hasVariables) {
            //带参数完成任务
            taskRuntime.complete(TaskPayloadBuilder.complete()
                                                   .withTaskId(task.getId())
                                                   .withVariables(variables)
                                                   .build());
        } else {
            taskRuntime.complete(TaskPayloadBuilder.complete()
                                                   .withTaskId(task.getId())
                                                   .build());
        }
    }

    /**
     * 根据实例id查询taskId
     *
     * @param processId 流程实例id
     * @return taskId
     */
    public String selectTaskIdByProcessId(String processId) {
        // 根据流程实例ID获取当前活动的（未完成的）任务
        org.activiti.engine.task.Task task = taskService.createTaskQuery()
                                                        .processInstanceId(processId)
                                                        .active() // 确保查询的是活动的任务
                                                        .singleResult();

        if (task != null) {
            // 返回找到的任务ID
            return task.getId();
        } else {
            // 如果没有找到对应的任务，可以处理这种情况，例如返回null或抛出异常
            return null;
        }
    }

    /**
     * 判断流程是否完结
     *
     * @param processInstanceId 流程实例id
     * @return 是或否
     */
    public boolean isProcessEnded(String processInstanceId) {
        // 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();

        // 通过流程实例ID查询正在运行的流程实例
        RuntimeService runtimeService = processEngine.getRuntimeService();
        org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                                                                                    .processInstanceId(processInstanceId)
                                                                                    .singleResult();

        // 如果没有返回结果，说明流程实例已经结束
        return processInstance == null;
    }

    /**
     * 根据流程查询下一个节点的相关信息
     *
     * @param processInstanceId 流程实例id
     */
    public List<ActRuTaskVO> selectActRuTask(String processInstanceId) {
        return actRuTaskMapper.selectActRuTaskVo(processInstanceId);
    }

    /**
     * 批量下一个节点的相关信息
     *
     * @param processInstanceIdList 流程实例idList
     */
    public List<ActRuTaskVO> selectActRuTaskList(List<String> processInstanceIdList) {
        return actRuTaskMapper.selectActRuTaskVoList(processInstanceIdList);
    }

    /**
     * 使用流程实例ID获取所有变量
     *
     * @param processInstanceId 流程实例id
     * @return 参数
     */
    public Map<String, Object> selectVariables(String processInstanceId) {
        // 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // 使用流程实例ID获取所有变量
        return runtimeService.getVariables(processInstanceId);
    }

    /**
     * 异步处理下一个节点的信息
     *
     * @param businessKey       业务KEY
     * @param processInstanceId 流程实例id
     * @param isInit            是否是初始化节点
     */
    @Async
    public void asyncUpdateNextNodeMsg(String businessKey, String processInstanceId, boolean isInit) {
        try {
            // 睡眠1秒
            Thread.sleep(1000);
            List<ActRuTaskVO> actRuTaskVOList = selectActRuTask(processInstanceId);
            if (CollectionUtil.isNotEmpty(actRuTaskVOList)) {
                List<SysUser> userList = sysUserService.selectUserByUserNames(actRuTaskVOList.stream()
                                                                                             .map(ActRuTaskVO::getApproverBy)
                                                                                             .toArray(String[]::new));
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put(NODE, actRuTaskVOList.get(0)
                                                  .getNode());
                paramMap.put(NICK_NAME, StrUtil.join(",", userList.stream()
                                                                  .map(SysUser::getNickName)
                                                                  .collect(Collectors.toList())));
                paramMap.put(USER_ID, StrUtil.join(",", userList.stream()
                                                                .map(SysUser::getUserId)
                                                                .collect(Collectors.toList())));

                doAsyncUpdateNextNodeMsg(businessKey, isInit, paramMap);
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
            Thread.currentThread()
                  .interrupt();
        }
    }

    /**
     * 实际执行下一节点业务信息
     * 各自业务自己实现
     *
     * @param businessKey 业务KEY
     * @param isInit      是否是初始化节点
     * @param paramMap    下一节点信息
     */
    public abstract void doAsyncUpdateNextNodeMsg(String businessKey, boolean isInit, Map<String, String> paramMap);


    /**
     * 根据 processId 作废流程实例
     *
     * @param processInstanceId 流程实例ID
     */
    public void invalid(String processInstanceId) {
        // 获取流程引擎实例
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        RuntimeService runtimeService = processEngine.getRuntimeService();

        // 检查流程实例是否存在
        org.activiti.engine.runtime.ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                                                                                    .processInstanceId(processInstanceId)
                                                                                    .singleResult();

        // 执行删除
        if (processInstance != null) {
            // 作废流程实例
            runtimeService.deleteProcessInstance(processInstanceId, "流程被作废");
        } else {
            throw new RuntimeException("流程实例不存在: " + processInstanceId);
        }
    }
}
